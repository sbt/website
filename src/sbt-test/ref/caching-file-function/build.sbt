lazy val countInput = taskKey[Seq[File]]("")
lazy val countFiles = taskKey[Seq[File]]("")

def doCount(in: Set[File], outDir: File): Set[File] =
  in map { source =>
    val out = outDir / source.getName
    val c = IO.readLines(source).size
    IO.write(out, c + "\n")
    out
  }

lazy val root = (project in file("."))
  .settings(
    countInput :=
      sbt.nio.file.FileTreeView.default
        .list(Glob(baseDirectory.value + "/*.md"))
        .map(_._1.toFile),
    countFiles := {
      val s = streams.value
      val in = countInput.value
      val t = crossTarget.value

      // wraps a function doCount in an up-to-date check
      val cachedFun = FileFunction.cached(s.cacheDirectory / "count") { (in: Set[File]) =>
        doCount(in, t): Set[File]
      }
      // Applies the cached function to the inputs files
      cachedFun(in.toSet).toSeq.sorted
    },
  )
