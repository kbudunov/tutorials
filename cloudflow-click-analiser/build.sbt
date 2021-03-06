import sbt._
import sbt.Keys._

val akkaVersion = "2.5.26"

lazy val sensorData =  (project in file("."))
  .enablePlugins(CloudflowAkkaStreamsApplicationPlugin)
  .settings(
    libraryDependencies ++= Seq(
      "com.lightbend.akka"     %% "akka-stream-alpakka-file"  % "1.1.2",
      "com.typesafe.akka"      %% "akka-http-spray-json"      % "10.1.11",
      "ch.qos.logback"         %  "logback-classic"           % "1.2.3",
      "com.typesafe.akka"      %% "akka-http-testkit"         % "10.1.11" % "test",
      "com.typesafe.akka"      %% "akka-actor"                % akkaVersion,
      "com.typesafe.akka"      %% "akka-stream"               % akkaVersion
    ),
    name := "sensor-data",
    organization := "com.lightbend",
    version := "0.1",
    scalaVersion := "2.12.10",
    crossScalaVersions := Vector(scalaVersion.value)
  )