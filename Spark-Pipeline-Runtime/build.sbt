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
    //    "org.apache.spark" %% "spark-sqk-kafka-0-10" % sparkV
  )

  val yamlParser = Seq("org.yaml" % "snakeyaml" % "1.33",
    "io.circe" %% "circe-yaml" % "0.14.2",
    "io.circe" %% "circe-parser" % "0.14.6",
    "io.circe" %% "circe-generic" % "0.14.6"
  )

  val okhttp = Seq("com.squareup.okhttp3" % "okhttp" % "4.11.0")
  val reflection= Seq("org.reflections" % "reflections" % "0.10.2")

  val platformProcessors = Seq("com.cred.platform.segmentation" % "Platform-Spark-Processors" % "0.1.0-SNAPSHOT" from "file:///Users/senyarav/workspace/segmentation-platform/Platform-Spark-Processors/target/scala-2.12/Platform-Spark-Processors-assembly-0.1.0-SNAPSHOT.jar"  )

}

lazy val commonSettings = Seq(
  organization:="com.cred.platform.segmentation",
  name:= "Spark-Pipeline-Runtime",
  scalaVersion:= artifacts.scalaV,
  libraryDependencies ++= artifacts.sparkStreaming ++ artifacts.yamlParser ++ artifacts.okhttp,
  scalacOptions++=Seq(
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

    libraryDependencies ++= artifacts.sparkStreaming ++ artifacts.yamlParser ++ artifacts.platformProcessors++artifacts.reflection
  )