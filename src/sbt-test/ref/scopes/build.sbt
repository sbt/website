// #unscoped
organization := name.value
// #unscoped

// #confScoped
Compile / name := "hello"
// #confScoped

// #taskScoped
packageBin / name := "hello"
// #taskScoped

// #confAndTaskScoped
Compile / packageBin / name := "hello"
// #confAndTaskScoped

// #global
// same as Zero / Zero / Zero / concurrentRestrictions
Global / concurrentRestrictions := Seq(
  Tags.limitAll(1)
)
// #global
