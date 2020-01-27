---
out: Understanding-Recompilation.html
---

  [466]: https://github.com/sbt/sbt/issues/466
  [288]: https://github.com/sbt/sbt/issues/288
  [322]: https://github.com/sbt/sbt/issues/322
  [1104]: https://github.com/sbt/sbt/issues/1104
  [1002]: https://github.com/sbt/sbt/issues/1002
  [1010]: https://github.com/sbt/sbt/issues/1010

Understanding Incremental Recompilation
---------------------------------------

Compiling Scala code with scalac is slow, but sbt often makes it faster.
By understanding how, you can even understand how to make compilation even
faster. Modifying source files with many dependencies might require
recompiling only those source files
(which might take 5 seconds for instance)
instead of all the dependencies
(which might take 2 minutes for instance).
Often you can control which will be your case and make
development faster with a few coding practices.

Improving the Scala compilation performance is a major goal of sbt,
and thus the speedups it gives are one of the major motivations to use it.
A significant portion of sbt's sources and development efforts deal
with strategies for speeding up compilation.

To reduce compile times, sbt uses two strategies:

<ol>
<li>Reduce the overhead for restarting Scalac
    <ul>
    <li>Implement smart and transparent strategies for incremental
      recompilation, so that only modified files and the needed
      dependencies are recompiled.</li>
    <li>sbt always runs Scalac in the same virtual machine. If one compiles
      source code using sbt, keeps sbt alive, modifies source code and
      triggers a new compilation, this compilation will be faster because
      (part of) Scalac will have already been JIT-compiled.</li>
    </ul>
</li>
<li>Reduce the number of recompiled source. 
    <ul>
    <li>When a source file <code>A.scala</code> is modified, sbt goes to great effort
        to recompile other source files depending on A.scala only if
        required - that is, only if the interface of A.scala was modified.
        With other build management tools (especially for Java, like ant),
        when a developer changes a source file in a non-binary-compatible
        way, she needs to manually ensure that dependencies are also
        recompiled - often by manually running the clean command to remove
        existing compilation output; otherwise compilation might succeed
        even when dependent class files might need to be recompiled. What is
        worse, the change to one source might make dependencies incorrect,
        but this is not discovered automatically: One might get a
        compilation success with incorrect source code. Since Scala compile
        times are so high, running clean is particularly undesirable.
    </li>
    </ul>
</li>
</ol>

By organizing your source code appropriately, you can minimize the
amount of code affected by a change. sbt cannot determine precisely
which dependencies have to be recompiled; the goal is to compute a
conservative approximation, so that whenever a file must be recompiled,
it will, even though we might recompile extra files.

### sbt heuristics

sbt tracks source dependencies at the granularity of source files. For
each source file, sbt tracks files which depend on it directly; if the
**interface** of classes, objects or traits in a file changes, all files
dependent on that source must be recompiled. At the moment sbt uses the
following algorithm to calculate source files dependent on a given
source file:

-  dependencies introduced through inheritance are included *transitively*;
   a dependency is introduced through inheritance if
   a class/trait in one file inherits from a trait/class in another file
-  all other direct dependencies are considered by name hashing optimization;
   other dependencies are also called "member reference" dependencies because
   they are introduced by referring to a member (class, method, type, etc.)
   defined in some other source file
-  name hashing optimization considers all member reference dependencies in
   context of interface changes of a given source file; it tries to prune
   irrelevant dependencies by looking at names of members that got modified
   and checking if dependent source files mention those names

The name hashing optimization is enabled by default since sbt 0.13.6.

### How to take advantage of sbt heuristics

The heuristics used by sbt imply the following user-visible
consequences, which determine whether a change to a class affects other
classes.

1.  Adding, removing, modifying `private` methods does not require
    recompilation of client classes. Therefore, suppose you add a method
    to a class with a lot of dependencies, and that this method is only
    used in the declaring class; marking it private will prevent
    recompilation of clients. However, this only applies to methods
    which are not accessible to other classes, hence methods marked with
    private or private[this]; methods which are private to a package,
    marked with private[name], are part of the API.
2.  Modifying the interface of a non-private method triggers name
    hashing optimization
3.  Modifying one class does require recompiling dependencies of other
    classes defined in the same file (unlike said in a previous version
    of this guide). Hence separating different classes in different
    source files might reduce recompilations.
4.  Changing the implementation of a method should *not* affect its
    clients, unless the return type is inferred, and the new
    implementation leads to a slightly different type being inferred.
    Hence, annotating the return type of a non-private method
    explicitly, if it is more general than the type actually returned,
    can reduce the code to be recompiled when the implementation of such
    a method changes. (Explicitly annotating return types of a public
    API is a good practice in general.)

All the above discussion about methods also applies to fields and
members in general; similarly, references to classes also extend to
objects and traits.

## Implementation of incremental recompilation

This sections goes into details of incremental compiler implementation. It's
starts with an overview of the problem incremental compiler tries to solve
and then discusses design choices that led to the current implementation.

### Overview

The goal of incremental compilation is detect changes to source files or to the classpath and
determine a small set of files to be recompiled in such a way that it'll yield the final result
identical to the result from a full, batch compilation. When reacting to changes the incremental
compiler has to goals that are at odds with each other:

  * recompile as little source files as possible cover all changes to type checking and produced
  * byte code triggered by changed source files and/or classpath

The first goal is about making recompilation fast and it's a sole point of incremental compiler
existence. The second goal is about correctness and sets a lower limit on the size of a set of
recompiled files. Determining that set is the core problem incremental compiler tries to solve.
We'll dive a little bit into this problem in the overview to understand what makes implementing
incremental compiler a challenging task.

Let's consider this very simple example:

```scala
// A.scala
package a
class A {
  def foo(): Int = 12
}

// B.scala
package b
class B {
  def bar(x: a.A): Int = x.foo()
}
```

Let's assume both of those files are already compiled and user changes `A.scala` so it looks like
this:

```scala
// A.scala
package a
class A {
  def foo(): Int = 23 // changed constant
}
```

The first step of incremental compilation is to compile modified source files. That's minimal set of
files incremental compiler has to compile. Modified version of `A.scala` will be compiled
successfully as changing the constant doesn't introduce type checking errors. The next step of
incremental compilation is determining whether changes applied to `A.scala` may affect other files.
In the example above only the constant returned by method `foo` has changed and that does not affect
compilation results of other files.

Let's consider another change to `A.scala`:

```scala
// A.scala
package a
class A {
  def foo(): String = "abc" // changed constant and return type
}
```

As before, the first step of incremental compilation is to compile modified files. In this case we
compile `A.scala` and compilation will finish successfully. The second step is again determining
whether changes to `A.scala` affect other files. We see that the return type of the `foo` public
method has changed so this might affect compilation results of other files. Indeed, `B.scala`
contains call to the `foo` method so has to be compiled in the second step. Compilation of `B.scala`
will fail because of type mismatch in `B.bar` method and that error will be reported back to the
user. That's where incremental compilation terminates in this case.

Let's identify the two main pieces of information that were needed to make decisions in the examples
presented above. The incremental compiler algorithm needs to:

  * index source files so it knows whether there were API changes that might affect other source
    files; e.g. it needs to detect changes to method signatures as in the example above
  * track dependencies between source files; once the change to an API is detected the algorithm
    needs to determine the set of files that might be potentially affected by this change

Both of those pieces of information are extracted from the Scala compiler.

### Interaction with the Scala compiler

Incremental compiler interacts with Scala compiler in many ways:

<ul>
  <li>provides three phases additional phases that extract needed information:
  <ul>
    <li>api phase extracts public interface of compiled sources by walking trees and indexing types</li>
    <li>dependency phase which extracts dependencies between source files (compilation units)</li>
    <li>analyzer phase which captures the list of emitted class files</li>
  </ul>
  </li>
  <li>defines a custom reporter which allows sbt to gather errors and warnings</li>
  <li>subclasses Global to:
  <ul>
    <li>add the api, dependency and analyzer phases</li>
    <li>set the custom reporter</li>
  </ul>
  </li>
  <li>manages instances of the custom Global and uses them to compile files it determined that need
    to be compiled</li>
</ul>

#### API extraction phase

The API extraction phase extracts information from Trees, Types and Symbols and maps it to
incremental compiler's internal data structures described in the
[api.specification](https://raw.github.com/sbt/sbt/0.13/api.specification) file.Those data
structures allow to express an API in a way that is independent from Scala compiler version. Also,
such representation is persistent so it is serialized on disk and reused between compiler runs or
even sbt runs.

The API extraction phase consist of two major components:

  1. mapping Types and Symbols to incremental compiler representation of an extracted API
  2. hashing that representation

##### Mapping Types and Symbols

The logic responsible for mapping Types and Symbols is implemented in
[API.scala](https://github.com/sbt/sbt/blob/0.13/compile/interface/src/main/scala/xsbt/API.scala).
With introduction of Scala reflection we have multiple variants of Types and Symbols. The
incremental compiler uses the variant defined in `scala.reflect.internal` package.

Also, there's one design choice that might not be obvious. When type corresponding to a class or a
trait is mapped then all inherited members are copied instead of declarations in that class/trait.
The reason for doing so is that it greatly simplifies analysis of API representation because all
relevant information to a class is stored in one place so there's no need for looking up parent type
representation. This simplicity comes at a price: the same information is copied over and over again
resulting in a performance hit. For example, every class will have members of `java.lang.Object`
duplicated along with full information about their signatures.

##### Hashing an API representation

The incremental compiler (as it's implemented right now) doesn't need very fine grained information
about the API. The incremental compiler just needs to know whether an API has changed since the last
time it was indexed. For that purpose hash sum is enough and it saves a lot of memory. Therefore,
API representation is hashed immediately after single compilation unit is processed and only hash
sum is stored persistently.

In earlier versions the incremental compiler wouldn't hash. That resulted in a very high memory
consumption and poor serialization/deserialization performance.

The hashing logic is implemented in the [HashAPI.scala](https://github.com/sbt/sbt/blob/0.13/compile
/api/src/main/scala/xsbt/api/HashAPI.scala) file.

#### Dependency phase

The incremental compiler extracts all Symbols given compilation unit depends on (refers to) and then
tries to map them back to corresponding source/class files. Mapping a Symbol back to a source file
is performed by using `sourceFile` attribute that Symbols derived from source files have set.
Mapping a Symbol back to (binary) class file is more tricky because Scala compiler does not track
origin of Symbols derived from binary files. Therefore simple heuristic is used which maps a
qualified class name to corresponding classpath entry. This logic is implemented in dependency phase
which has an access to the full classpath.

The set of Symbols given compilation unit depend on is obtained by performing a tree walk. The tree
walk examines all tree nodes that can introduce a dependency (refer to another Symbol) and gathers
all Symbols assigned to them. Symbols are assigned to tree nodes by Scala compiler during type
checking phase.

_Incremental compiler used to rely on `CompilationUnit.depends` for collecting dependencies.
However, name hashing requires a more precise dependency information. Check [#1002][1002] for
details_.

#### Analyzer phase

Collection of produced class files is extracted by inspecting contents `CompilationUnit.icode`
property which contains  all ICode classes that backend will emit as JVM class files.

### Name hashing algorithm

#### Motivation

Let's consider the following example:

```scala
// A.scala
class A {
  def inc(x: Int): Int = x+1
}

// B.scala
class B {
  def foo(a: A, x: Int): Int = a.inc(x)
}
```

Let's assume both of those files are compiled and user changes `A.scala` so it looks like this:

```scala
// A.scala
class A {
  def inc(x: Int): Int = x+1
  def dec(x: Int): Int = x-1
}
```

Once user hits save and asks incremental compiler to recompile it's project it will do the
following:

  1. Recompile `A.scala` as the source code has changed (first iteration)
  2. While recompiling it will reindex API structure of `A.scala` and detect it has changed
  3. It will determine that `B.scala` depends on `A.scala` and since the API structure of `A.scala` has changed `B.scala` has to be recompiled as well (`B.scala` has been invalidated)
  4. Recompile `B.scala` because it was invalidated in 3. due to dependency change
  5. Reindex API structure of `B.scala` and find out that it hasn't changed so we are done

To summarize, we'll invoke Scala compiler twice: one time to recompile `A.scala` and then to
recompile `B.scala` because `A` has a new method `dec`.

However, one can easily see that in this simple scenario recompilation of `B.scala` is not needed
because addition of `dec` method to `A` class is irrelevant to the `B` class as its not using it
and it is not affected by it in any way.

In case of two files the fact that we recompile too much doesn't sound too bad. However, in
practice, the dependency graph is rather dense so one might end up recompiling the whole project
upon a change that is irrelevant to almost all files in the whole project. That's exactly what
happens in Play projects when routes are modified. The nature of routes and reversed routes is that
every template and every controller depends on some methods defined in those two classes (`Routes`
and `ReversedRoutes`) but changes to specific route definition usually affects only small subset of
all templates and controllers.

The idea behind name hashing is to exploit that observation and make the invalidation algorithm
smarter about changes that can possibly affect a small number of files.

#### Detection of irrelevant dependencies (direct approach)

A change to the API of a given source file `X.scala` can be called irrelevant if it doesn't affect the compilation
result of file `Y.scala` even if `Y.scala` depends on `X.scala`.

From that definition one can easily see that a change can be declared irrelevant only with respect to
a given dependency. Conversely, one can declare a dependency between two source files irrelevant with
respect to a given change of API in one of the files if the change doesn't affect the compilation
result of the other file. From now on we'll focus on detection of irrelevant dependencies.

A very naive way of solving a problem of detecting irrelevant dependencies would be to say that we
keep track of all used methods in `Y.scala` so if a method in `X.scala` is added/removed/modified we
just check if it's being used in `Y.scala` and if it's not then we consider the dependency of `Y.scala`
on `X.scala` irrelevant in this particular case.

Just to give you a sneak preview of problems that quickly arise if you consider that strategy let's
consider those two scenarios.

##### Inheritance

We'll see how a method not used in another source file might affect its compilation result. Let's
consider this structure:

```scala
// A.scala
abstract class A

// B.scala
class B extends A
```

Let's add an abstract method to class `A`:

```scala
// A.scala
abstract class A {
  def foo(x: Int): Int
}
```

Now, once we recompile `A.scala` we could just say that since `A.foo` is not used in `B` class then
we don't need to recompile `B.scala`. However, this is not true because `B` doesn't implement a newly
introduced, abstract method and an error should be reported.

Therefore, a simple strategy of looking at used methods for determining whether a given dependency
is relevant or not is not enough.

##### Enrichment pattern

Here we'll see another case of newly introduced method (that is not used anywhere yet) that affects
compilation results of other files. This time, no inheritance will be involved but we'll use
enrichment pattern (implicit conversions) instead.

Let's assume we have the following structure:

```scala
// A.scala
class A

// B.scala
class B {
  class AOps(a: A) {
    def foo(x: Int): Int = x+1
  }
  implicit def richA(a: A): AOps = new AOps(a)
  def bar(a: A): Int = a.foo(12) // this is expanded to richA(a).foo so we are calling AOPs.foo method
}
```

Now, let's add a `foo` method directly to `A`:

```scala
// A.scala
class A {
  def foo(x: Int): Int = x-1
}
```

Now, once we recompile `A.scala` and detect that there's a new method defined in the `A` class we would
need to consider whether this is relevant to the dependency of `B.scala` on `A.scala`. Notice that in
`B.scala` we do not use `A.foo` (it didn't exist at the time `B.scala` was compiled) but we use
`AOps.foo` and it's not immediately clear that `AOps.foo` has anything to do with `A.foo`. One would
need to detect the fact that a call to `AOps.foo` as a result of implicit conversion `richA` that
was inserted because we failed to find `foo` on `A` before.

This kind of analysis gets us very quickly to the implementation complexity of Scala's type checker and
is not feasible to implement in a general case.

##### Too much information to track

All of the above assumed we actually have full information about the structure of the API and used methods
preserved so we can make use of it. However, as described in
[Hashing an API representation](#hashing-an-api-representation) we do not store the whole
representation of the API but only its hash sum. Also, dependencies are tracked at source file
level and not at class/method level.

One could imagine reworking the current design to track more information but it would be a very big
undertaking. Also, the incremental compiler used to preserve the whole API structure but it switched to
hashing due to the resulting infeasible memory requirements.

#### Detection of irrelevant dependencies (name hashing)

As we saw in the previous chapter, the direct approach of tracking more information about what's being
used in the source files becomes tricky very quickly. One would wish to come up with a simpler and less
precise approach that would still yield big improvements over the existing implementation.

The idea is to not track all the used members and reason very precisely about when a given change to some
members affects the result of the compilation of other files. We would track just the used _simple names_
instead and we would also track the hash sums for all members with the given simple name. The simple name
means just an unqualified name of a term or a type.

Let's see first how this simplified strategy addresses the problem with the
[enrichment pattern](#enrichment-pattern). We'll do that by simulating the name hashing algorithm.
Let's start with the original code:

```scala
// A.scala
class A

// B.scala
class B {
  class AOps(a: A) {
    def foo(x: Int): Int = x+1
  }
  implicit def richA(a: A): AOps = new AOps(a)
  def bar(a: A): Int = a.foo(12) // this is expanded to richA(a).foo so we are calling AOPs.foo method
}
```

During the compilation of those two files we'll extract the following information:

```
usedNames("A.scala"): A
usedNames("B.scala"): B, AOps, a, A, foo, x, Int, richA, AOps, bar

nameHashes("A.scala"): A -> ...
nameHashes("B.scala"): B -> ..., AOps -> ..., foo -> ..., richA -> ..., bar -> ...
```

The `usedNames` relation track all the names mentioned in the given source file. The `nameHashes` relation
gives us a hash sum of the groups of members that are put together in one bucket if they have the same
simple name. In addition to the information presented above we still track the dependency of `B.scala` on
`A.scala`.

Now, if we add a `foo` method to `A` class:

```scala
// A.scala
class A {
  def foo(x: Int): Int = x-1
}
```

and recompile, we'll get the following (updated) information:

```
usedNames("A.scala"): A, foo
nameHashes("A.scala"): A -> ..., foo -> ...
```

The incremental compiler compares the name hashes before and after the change and detects that the hash
sum of `foo` has changed (it's been added). Therefore, it looks at all the source files that depend
on `A.scala`, in our case it's just `B.scala`, and checks whether `foo` appears as a used name. It
does, therefore it recompiles `B.scala` as intended.

You can see now, that if we added another method to `A` like `xyz` then `B.scala` wouldn't be
recompiled because nowhere in `B.scala` is the name `xyz` mentioned. Therefore, if you have
reasonably non-clashing names you should benefit from a lot of dependencies between source files
marked as irrelevant.

It's very nice that this simple, name-based heuristic manages to withstand the "enrichment pattern"
test. However, name-hashing fails to pass the other test of [inheritance](#inheritance). In order to
address that problem, we'll need to take a closer look at the dependencies introduced by inheritance vs
dependencies introduced by member references.

#### Dependencies introduced by member reference and inheritance

The core assumption behind the name-hashing algorithm is that if a user adds/modifies/removes a member
of a class (e.g. a method) then the results of compilation of other classes won't be affected unless
they are using that particular member. Inheritance with its various override checks makes the whole
situation much more complicated; if you combine it with mix-in composition that introduces new
fields to classes inheriting from traits then you quickly realize that inheritance requires special
handling.

The idea is that for now we would switch back to the old scheme whenever inheritance is involved.
Therefore, we track dependencies introduced by member reference separately from dependencies
introduced by inheritance. All dependencies introduced by inheritance are _not_ subject to name-hashing
analysis so they are never marked as irrelevant.

The intuition behind the dependency introduced by inheritance is very simple: it's a dependency a
class/trait introduces by inheriting from another class/trait. All other dependencies are called
dependencies by member reference because they are introduced by referring (selecting) a member
(method, type alias, inner class, val, etc.) from another class. Notice that in order to inherit from
a class you need to refer to it so dependencies introduced by inheritance are a strict subset of
member reference dependencies.

Here's an example which illustrates the distinction:

```scala
// A.scala
class A {
  def foo(x: Int): Int = x+1
}

// B.scala
class B(val a: A)

// C.scala
trait C

// D.scala
trait D[T]

// X.scala
class X extends A with C with D[B] {
  // dependencies by inheritance: A, C, D
  // dependencies by member reference: A, C, D, B
}

// Y.scala
class Y {
  def test(b: B): Int = b.a.foo(12)
  // dependencies by member reference: B, Int, A
}
```

There are two things to notice:

  1. `X` does not depend on `B` by inheritance because `B` is passed as a type parameter to `D`; we
     consider only types that appear as parents to `X`
  2. `Y` _does_ depend on `A` even if there's no explicit mention of `A` in the source file; we
     select a method `foo` defined in `A` and that's enough to introduce a dependency

To sum it up, the way we want to handle inheritance and the problems it introduces is to track all
dependencies introduced by inheritance separately and have a much more strict way of invalidating
dependencies. Essentially, whenever there's a dependency by inheritance it will react to any
(even minor) change in parent types.

#### Computing name hashes

One thing we skimmed over so far is how name hashes are actually computed.

As mentioned before, all definitions are grouped together by their simple name and then hashed as one
bucket. If a definition (for example a class) contains other definition then those nested
definitions do _not_ contribute to a hash sum. The nested definitions will contribute to hashes of
buckets selected by their name.

### What is included in the interface of a Scala class

It is surprisingly tricky to understand which changes to a class require
recompiling its clients. The rules valid for Java are much simpler (even
if they include some subtle points as well); trying to apply them to
Scala will prove frustrating. Here is a list of a few surprising points,
just to illustrate the ideas; this list is not intended to be complete.

1.  Since Scala supports named arguments in method invocations, the name
    of method arguments are part of its interface.
2.  Adding a method to a trait requires recompiling all implementing
    classes. The same is true for most changes to a method signature in
    a trait.
3.  Calls to `super.methodName` in traits are resolved to calls to an
    abstract method called `fullyQualifiedTraitName\$\$super\$methodName`;
    such methods only exist if they are used. Hence, adding the first
    call to `super.methodName` for a specific method name changes the
    interface. At present, this is not yet handled—see [#466][466].
4.  `sealed` hierarchies of case classes allow to check exhaustiveness
    of pattern matching. Hence pattern matches using case classes must
    depend on the complete hierarchy - this is one reason why
    dependencies cannot be easily tracked at the class level (see Scala
    issue [SI-2559](https://github.com/scala/bug/issues/2559) for an
    example.). Check [#1104][1104] for detailed discussion of tracking
    dependencies at class level.

#### Debugging an interface representation

If you see spurious incremental recompilations or you want to understand
what changes to an extracted interface cause incremental recompilation
then sbt 0.13 has the right tools for that.

In order to debug the interface representation and its changes as you
modify and recompile source code you need to do two things:

1.  Enable the incremental compiler's `apiDebug` option.
2.  Add [diff-utils library](https://code.google.com/p/java-diff-utils/) to sbt's
    classpath. Check documentation of `sbt.extraClasspath` system
    property in the Command-Line-Reference.

> **warning**
>
> Enabling the `apiDebug` option increases significantly
> the memory consumption and degrades the performance of the incremental
> compiler. The underlying reason is that in order to produce
> meaningful debugging information about interface differences
> the incremental compiler has to retain the full representation of the
> interface instead of just the hash sum as it does by default.
>
> Keep this option enabled when you are debugging the incremental compiler
> problem only.

Below is a complete transcript which shows how to enable interface
debugging in your project. First, we download the `diffutils` jar and
pass it to sbt:

``` 
curl -O https://java-diff-utils.googlecode.com/files/diffutils-1.2.1.jar
sbt -Dsbt.extraClasspath=diffutils-1.2.1.jar
[info] Loading project definition from /Users/grek/tmp/sbt-013/project
[info] Set current project to sbt-013 (in build file:/Users/grek/tmp/sbt-013/)
> set incOptions := incOptions.value.withApiDebug(true)
[info] Defining *:incOptions
[info] The new value will be used by compile:incCompileSetup, test:incCompileSetup
[info] Reapplying settings...
[info] Set current project to sbt-013 (in build file:/Users/grek/tmp/sbt-013/)
```

Let's suppose you have the following source code in `Test.scala`:

```scala
class A {
  def b: Int = 123
}
```

compile it and then change the `Test.scala` file so it looks like:

```scala
class A {
   def b: String = "abc"
}
```

and run `compile` again. Now if you run `last compile` you should
see the following lines in the debugging log

``` 
> last compile
[...]
[debug] Detected a change in a public API:
[debug] --- /Users/grek/tmp/sbt-013/Test.scala
[debug] +++ /Users/grek/tmp/sbt-013/Test.scala
[debug] @@ -23,7 +23,7 @@
[debug]  ^inherited^ final def ##(): scala.this#Int
[debug]  ^inherited^ final def synchronized[ java.lang.Object.T0 >: scala.this#Nothing <: scala.this#Any](x\$1: <java.lang.Object.T0>): <java.lang.Object.T0>
[debug]  ^inherited^ final def \$isInstanceOf[ java.lang.Object.T0 >: scala.this#Nothing <: scala.this#Any](): scala.this#Boolean
[debug]  ^inherited^ final def \$asInstanceOf[ java.lang.Object.T0 >: scala.this#Nothing <: scala.this#Any](): <java.lang.Object.T0>
[debug]  def <init>(): this#A
[debug] -def b: scala.this#Int
[debug] +def b: java.lang.this#String
[debug]  }
```

You can see a unified diff of the two interface textual represetantions. As
you can see, the incremental compiler detected a change to the return
type of `b` method.

#### Why changing the implementation of a method might affect clients, and why type annotations help

This section explains why relying on type inference for return types of
public methods is not always appropriate. However this is an important
design issue, so we cannot give fixed rules. Moreover, this change is
often invasive, and reducing compilation times is not often a good
enough motivation. That is also why we discuss some of the implications
from the point of view of binary compatibility and software engineering.

Consider the following source file `A.scala`:

```scala
import java.io._
object A {
  def openFiles(list: List[File]) = 
    list.map(name => new FileWriter(name))
}
```

Let us now consider the public interface of trait `A`. Note that the
return type of method `openFiles` is not specified explicitly, but
computed by type inference to be `List[FileWriter]`. Suppose that after
writing this source code, we introduce some client code and then modify
`A.scala` as follows:

```scala
import java.io._
object A {
  def openFiles(list: List[File]) =
    Vector(list.map(name => new BufferedWriter(new FileWriter(name))): _*)
}
```

Type inference will now compute the result type as `Vector[BufferedWriter]`;
in other words, changing the implementation lead to a change to the
public interface, with two undesirable consequences:

1.  Concerning our topic, the client code needs to be recompiled, since
    changing the return type of a method, in the JVM, is a
    binary-incompatible interface change.
2.  If our component is a released library, using our new version
    requires recompiling all client code, changing the version number,
    and so on. Often not good, if you distribute a library where binary
    compatibility becomes an issue.
3.  More in general, the client code might now even be invalid. The
    following code will for instance become invalid after the change:

```scala
val res: List[FileWriter] = A.openFiles(List(new File("foo.input")))
```

Also the following code will break:

```scala
val a: Seq[Writer] = new BufferedWriter(new FileWriter("bar.input"))
A.openFiles(List(new File("foo.input")))
```

How can we avoid these problems?

Of course, we cannot solve them in general: if we want to alter the
interface of a module, breakage might result. However, often we can
remove *implementation details* from the interface of a module. In the
example above, for instance, it might well be that the intended return
type is more general - namely `Seq[Writer]`. It might also not be the
case - this is a design choice to be decided on a case-by-case basis. In
this example I will assume however that the designer chooses
`Seq[Writer]`, since it is a reasonable choice both in the above
simplified example and in a real-world extension of the above code.

The client snippets above will now become

```scala
val res: Seq[Writer] =
  A.openFiles(List(new File("foo.input")))

val a: Seq[Writer] =
  new BufferedWriter(new FileWriter("bar.input")) +:
  A.openFiles(List(new File("foo.input")))
```

### Bytecode Enhancers

sbt added an extension point whereby users can effectively manipulate
Java bytecode (`.class` files) *before* the incremental compiler
attempts to cache the classfile hashes.  This allows libraries like
Ebean to function with sbt without corrupting the compiler cache and
rerunning compile every few seconds.

This splits the compile task into several subTasks:

1. `previousCompile`: This task returns the previously persisted
   `Analysis` object for this project.

2. `compileIncremental`: This is the core logic of compiling
    Scala/Java files together.  This task actually does the work of
    compiling a project incrementally, including ensuring a minimum
    number of source files are compiled.  After this method, all
    .class files that would be generated by scalac + javac will be
    available.

3. `manipulateByteCode`: This is a stub task which takes the
   `compileIncremental` result and returns it.  Plugins which need to
   manipulate bytecode are expected to override this task with their
   own implementation, ensuring to call the previous behavior.

4. `compile`: This task depends on `manipulateBytecode` and then
   persists the `Analysis` object containing all incremental compiler
   information.

Here's an example of how to hook the new `manipulateBytecode` key in
your own plugin:

```scala
    Compile / manipulateBytecode := {
      val previous = (Compile / manipulateBytecode).value
      // Note: This must return a new Compiler.CompileResult with our changes.
      doManipulateBytecode(previous)
    }
```

### Further references

The incremental compilation logic is implemented in
<https://github.com/sbt/sbt/blob/0.13/compile/inc/src/main/scala/inc/Incremental.scala>.
Some discussion on the incremental recompilation policies is available
in issue [#322][322], [#288][288] and [#1010][1010].
