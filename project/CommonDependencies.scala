import sbt._
import sbt.Keys._

object CommonDependencies {

  val akkaVersion = "2.4.6"

  val kamonVersion    = "0.6.1"

  val settings = Seq (
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq (
      //"com.typesafe.play" %% "play-netty-server" % "2.5.2",
      "com.chuusai" %% "shapeless" % "2.3.1",
      "org.apache.ignite" % "ignite-core" % "1.5.0.final",
      "org.scalaz" %% "scalaz-core" % "7.2.2",
      "org.apache.ignite" % "ignite-indexing" % "1.5.0.final",
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "ch.qos.logback" % "logback-classic" % "1.1.7",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
      "org.typelevel" %% "cats" % "0.6.0",
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
}