sbt dependencyTree
==================

The `dependencyTree` task displays the dependency graph of your project in various formats, helping you visualize and analyze transitive dependencies. It is provided by the `DependencyTreePlugin`, which is enabled by default in sbt (since version 1.4.0).

## Usage

```
dependencyTree [subcommand] [options]
```

Run this in the sbt shell. The task resolves dependencies before generating output, so it may trigger downloads if needed.

### Subcommands

- `tree` (default): Prints an ASCII tree of dependencies, showing hierarchical relationships. Useful for quick overviews.
- `list`: Prints a flat list of all dependencies, one per line. Ideal for scripting or grepping.
- `graph` / `dot`: Prints a GraphViz DOT file for visualization. The `dot` alias is provided for compatibility.
- `html`: Creates a standalone HTML page with the dependency graph rendered as text.
- `html-graph`: Creates an HTML page with an embedded GraphViz diagram (requires GraphViz on the system).
- `json`: Prints dependencies in JSON format, structured for programmatic use.
- `xml`: Prints dependencies in GraphML format, suitable for graph analysis tools.
- `stats`: Prints summary statistics, such as total dependencies and versions.
- `help`: Displays the usage help (same as running `dependencyTree` without arguments).

### Options

- `--quiet`: Suppresses console output and returns the result as a task value (e.g., for use in other tasks).
- `--out <file>`: Writes output to the specified file instead of stdout. The file extension determines the default subcommand:
  - `.txt`: `tree`
  - `.dot`: `graph`
  - `.html`: `html`
  - `.json`: `json`
  - `.xml`: `xml`
- `--browse`: Automatically opens the output file in your default browser (only works with `graph` or `html` subcommands).

## Examples

### ASCII Tree (Default)

Displays a hierarchical view of dependencies, with `[S]` indicating Scala library dependencies.

```
> Compile/dependencyTree
[info] default:example_3:0.1.0-SNAPSHOT
[info]   +-org.scala-lang:scala3-library_3:3.3.1 [S]
[info]   +-com.example:library_3:1.0.0
[info]     +-org.typelevel:cats-core_3:2.9.0
[info]     | +-org.typelevel:cats-kernel_3:2.9.0
[info]     |   +-org.scala-lang:scala3-library_3:3.1.3 (evicted by: 3.3.1)
[info]     +-org.typelevel:cats-effect_3:3.4.0
```

Evicted dependencies (older versions replaced by newer ones) are shown in parentheses.

### List of Dependencies

```
> Compile/dependencyTree list
org.scala-lang:scala3-library_3:3.3.1
com.example:library_3:1.0.0
org.typelevel:cats-core_3:2.9.0
org.typelevel:cats-kernel_3:2.9.0
org.typelevel:cats-effect_3:3.4.0
```

### GraphViz DOT File

Generates a DOT file for rendering graphs (e.g., with `dot` command).

```
> Compile/dependencyTree graph --out dependencies.dot
```

Contents of `dependencies.dot`:

```
digraph "dependency-graph" {
    graph[rankdir="LR"; splines=polyline]
    edge [arrowtail="none"]
    "default:example_3:0.1.0-SNAPSHOT" -> "org.scala-lang:scala3-library_3:3.3.1"
    "default:example_3:0.1.0-SNAPSHOT" -> "com.example:library_3:1.0.0"
    "com.example:library_3:1.0.0" -> "org.typelevel:cats-core_3:2.9.0"
    // ... more edges
}
```

Render to PNG: `dot -Tpng dependencies.dot -o dependencies.png`

### HTML Output

```
> Compile/dependencyTree html --out deps.html --browse
```

Creates `deps.html` with a text-based graph and opens it in the browser.

### HTML with Embedded Graph

```
> Compile/dependencyTree html-graph --browse
```

Requires GraphViz installed. Embeds a visual graph in the HTML.

### JSON Output

```
> Compile/dependencyTree json --out deps.json
```

Sample output:

```json
{
  "organization": "default",
  "name": "example_3",
  "version": "0.1.0-SNAPSHOT",
  "dependencies": [
    {
      "organization": "org.scala-lang",
      "name": "scala3-library_3",
      "version": "3.3.1",
      "configurations": ["compile"],
      "evicted": false
    },
    // ... more
  ]
}
```

### XML (GraphML) Output

```
> Compile/dependencyTree xml --out deps.graphml
```

Suitable for import into graph visualization tools like yEd.

### Statistics

```
> Compile/dependencyTree stats
Total dependencies: 15
Unique organizations: 5
Versions: 10 distinct
Evictions: 2
```

## Configuration Keys

Customize behavior with these settings in `build.sbt`:

- `dependencyTreeIncludeScalaLibrary := true`: Include Scala library dependencies in output (default: `false`).
- `dependencyDotNodeColors := false`: Disable colors in DOT node labels (default: `true`).
- `dependencyDotNodeLabel := (org: String, name: String, version: String) => s"$org:$name:$version"`: Customize DOT node labels.
- `dependencyDotHeader := """digraph "custom" { rankdir="TB"; }"""`: Set a custom DOT header.

## Related Tasks

- `whatDependsOn <module>`: Shows what depends on a specific module (e.g., `whatDependsOn org.example:lib:1.0`).
- `dependencyLicenseInfo`: Displays license information for dependencies.

## Scopes

Available in `Compile` and `Test` configurations. Use `Global/` for cross-configuration views if needed.

