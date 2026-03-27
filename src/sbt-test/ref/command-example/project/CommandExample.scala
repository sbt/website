import sbt.*
import Keys.*
import scala.Console

// imports standard command parsing functionality
import complete.DefaultParsers.*

object CommandExample:
  // A simple, no-argument command that prints "Hi",
  //  leaving the current state unchanged.
  def hello = Command.command("hello"): s0 =>
    Console.out.println("Hi!")
    s0

  // A simple, multiple-argument command that prints "Hi" followed by the arguments.
  //   Again, it leaves the current state unchanged.
  def helloAll = Command.args("helloAll", "<name>"): (s0, args) =>
    println("Hi " + args.mkString(" "))
    s0

  // A command that demonstrates failing or succeeding based on the input
  def failIfTrue = Command.single("failIfTrue"):
    case (s0, "true") => s0.fail
    case (s0, _)      => s0

  // Demonstration of a custom parser.
  // The command changes the foreground or background terminal color
  //  according to the input.
  lazy val change = Space ~> (reset | setColor)
  lazy val reset = token("reset" ^^^ Console.RESET)
  lazy val color = token( Space ~> ("blue" ^^^ "4" | "green" ^^^ "2") )
  lazy val select = token( "fg" ^^^ "3" | "bg" ^^^ "4" )
  lazy val setColor = (select ~ color).map: (g, c) =>
    s"\u001B[${g}${c}m"

  def changeColor = Command("color")(_ => change): (s0, ansicode) =>
    Console.out.print(ansicode)
    Console.out.println("Hi")
    s0

  // A command that demonstrates getting information out of State.
  def printState = Command.command("printState"): s0 =>
    import s0.*
    println(s"definedCommands.size registered commands")
    println(s"commands to run: ${show(remainingCommands)}")
    println()

    println(s"original arguments: ${show(configuration.arguments.toSeq)}")
    println(s"base directory: ${configuration.baseDirectory}")
    println()

    println(s"sbt version: ${configuration.provider.id.version}")
    println(s"Scala version (for sbt): ${configuration.provider.scalaProvider.version}")
    println()

    val extracted = Project.extract(s0)
    import extracted.*
    println(s"Current build: ${currentRef.build}")
    println(s"Current project: ${currentRef.project}")
    println(s"Original setting count: ${session.original.size}")
    println(s"Session setting count: ${session.append.size}")
    s0

  def show[A1](s: Seq[A1]) =
    s.map("'" + _ + "'").mkString("[", ", ", "]")

end CommandExample
