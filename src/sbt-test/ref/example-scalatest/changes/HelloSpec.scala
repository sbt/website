import org.scalatest.funsuite._

class HelloSpec extends AnyFunSuite {
  test("Hello should start with H") {
    // Hello, as opposed to hello
    assert("Hello".startsWith("H"))
  }
}
