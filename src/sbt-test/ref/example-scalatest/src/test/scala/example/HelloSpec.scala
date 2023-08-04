import org.scalatest.funsuite._

class HelloSpec extends AnyFunSuite {
  test("Hello should start with H") {
    assert("hello".startsWith("H"))
  }
}
