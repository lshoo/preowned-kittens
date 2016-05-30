import com.typesafe.sbt.SbtAspectj.AspectjKeys
import com.typesafe.sbt.SbtAspectj._

name := "preowned-kittens"

organization := "com.lshoo"

version in ThisBuild := "1.0"

scalaVersion in ThisBuild := "2.11.8"


val akkaVersion = "2.4.6"

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
  settings(CommonDependencies.settings: _*)
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

lazy val ddd = (
  PreownedKittenProject("ddd")
    dependsOn(common)
    settings()
  )

lazy val kafka = (
  PreownedKittenProject("kafka")
    dependsOn(common)
    settings()
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