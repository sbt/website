// #fig1
organization := name.value
// #fig1

// #fig2
Compile / name := "hello"
// #fig2

// #fig3
packageBin / name := "hello"
// #fig3

// #fig4
Compile / packageBin / name := "hello"
// #fig4

// #global
// same as Zero / Zero / Zero / concurrentRestrictions
Global / concurrentRestrictions := Seq(
  Tags.limitAll(1)
)
// #global
