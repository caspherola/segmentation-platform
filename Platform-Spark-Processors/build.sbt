import sbt.Keys.libraryDependencies
import sbtrelease.ReleaseStateTransformations._

ThisBuild / version := "0.1.0-SNAPSHOT"


lazy val artifacts = new {
  val scalaV= "2.12.14"
  val sparkV= "3.2.1"

  val sparkStreaming = Seq(
    "org.apache.spark" %% "spark-sql" % sparkV,
    "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkV,
    "org.apache.spark" %% "spark-core" % sparkV,
    "org.apache.spark" %% "spark-streaming" % sparkV,
    "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkV,
  )

  val yamlParser = Seq("org.yaml" % "snakeyaml" % "1.33")
}

lazy val commonSettings = Seq(
  organization := "com.cred.platform.processors",
  name := "Platform-Spark-Processors",
  scalaVersion := artifacts.scalaV,
  scalacOptions ++= Seq(
    "-encoding", "UTF-8",
    "-Xfatal-warnings",
    "-deprecation",
    "-feature",
    "-unchecked",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-language:existentials",
    "-Ywarn-dead-code",
    "-Ypartial-unification"
  )
)
assembly / assemblyMergeStrategy := {
  case PathList("META-INF",_*) => MergeStrategy.discard
  case _ => MergeStrategy.last
}

Compile / assembly / artifact:={
  val art= (Compile / assembly / artifact).value
  art.withClassifier(Some("assembly"))
}

addArtifact(Compile / assembly / artifact , assembly)

lazy val root = (project in file("."))
  .settings(commonSettings)
  .settings(

    libraryDependencies ++= artifacts.sparkStreaming ++ artifacts.yamlParser
  )