import com.typesafe.sbt.SbtAspectj.AspectjKeys
import com.typesafe.sbt.SbtAspectj._

name := "preowned-kittens"

organization := "com.lshoo"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.11.8"


val akkaVersion = "2.4.4"

val kamonVersion    = "0.6.1"

val gitHeadCommitSha = taskKey[String] (
  "Determines the current git commit SHA"
  )

gitHeadCommitSha in ThisBuild := Process("git rev-parse HEAD").lines.head

val makeVersionProperties = taskKey[Seq[File]] (
  "Makes a version.properties file."
)

def PreownedKittenProject(name: String): Project = (
  Project(name, file(name))
  settings(
    libraryDependencies ++= Seq (
      //"com.typesafe.play" %% "play-netty-server" % "2.5.2",
      "com.chuusai" %% "shapeless" % "2.3.0",
      "org.apache.ignite" % "ignite-core" % "1.5.0.final",
      "org.scalaz" %% "scalaz-core" % "7.2.2",
      "org.apache.ignite" % "ignite-indexing" % "1.5.0.final",
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
      "org.aspectj" % "aspectjweaver" % "1.8.9",
      "io.kamon" %% "kamon-core" % kamonVersion,
      "io.kamon" %% "kamon-scala" % kamonVersion,
      "io.kamon" %% "kamon-akka" % kamonVersion,
      "io.kamon" %% "kamon-akka-remote_akka-2.4" % kamonVersion,
      "io.kamon" %% "kamon-statsd" % kamonVersion,
      "io.kamon" %% "kamon-log-reporter" % kamonVersion,
      "io.kamon" %% "kamon-system-metrics" % kamonVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
        "org.scalactic" %% "scalactic" % "2.2.6",
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
    )
    )
  )

lazy val common = (
  PreownedKittenProject("common")
  settings (
    /*makeVersionProperties := {
      val propFile =
        (resourceManaged in Compile).value / "version.properties"
      val content = "version=%s" format (gitHeadCommitSha.value)
      IO.write(propFile, content)
      Seq(propFile)
    },*/
    //(resourceGenerators in Compile) <+= makeVersionProperties
  )
  )

lazy val analytics = (
  PreownedKittenProject("analytics")
  dependsOn(common)
  settings()
  )

lazy val website = (
  PreownedKittenProject("website")
  dependsOn(common)
  settings()
  )

//configure aspectJ plugin to enable Kamon monitoring
aspectjSettings

javaOptions in run <++= AspectjKeys.weaverOptions in Aspectj

fork in run := true