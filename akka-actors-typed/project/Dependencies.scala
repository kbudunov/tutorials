import sbt._

object Dependencies {
  val scalaCompat = "2.12"

  private object Version {
    val akka      = "2.5.25"
    val akkaActor = "2.6.4"
  }

  object Akka {
    val AkkaActor = "com.typesafe.akka" %% "akka-actor-typed" % Version.akkaActor
  }

  object Logging {
    val Logback = "ch.qos.logback" % "logback-classic" % "1.2.3"
  }

  object Testing {
    val actorTestKit      = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.4" % Test
    val scalaTest         = "org.scalatest"     %% "scalatest"                % "3.1.1" % Test
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit"      % "2.6.4" % Test
  }
}
