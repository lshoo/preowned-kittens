name := "preowned-kittens"

organization := "com.lshoo"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq (
  "org.scalactic" %% "scalactic" % "2.2.6",
  "org.scalatest" %% "scalatest" % "2.2.6" % "test"
)

val gitHeadCommitSha = taskKey[String] (
  "Determines the current git commit SHA"
  )

gitHeadCommitSha := Process("git rev-parse HEAD").lines.head

val makeVersionProperties = taskKey[Seq[File]] (
  "Makes a version.properties file."
)

makeVersionProperties := {
  val propFile =
    new File((resourceManaged in Compile).value, "version.properties")
  val content = "version=%s" format (gitHeadCommitSha.value)
  IO.write(propFile, content)
  Seq(propFile)
}

val taskA = taskKey[String]("taskA")
val taskB = taskKey[String]("taskB")
val taskC = taskKey[String]("taskC")

taskA := {
  val b = taskB.value
  val c = taskC.value
  "TaskA"
}
taskB := {
  //taskC.value
  Thread.sleep(5000); "TaskB"
}
taskC := { Thread.sleep(5000); "TaskC" }

(resourceGenerators in Compile) <+= makeVersionProperties