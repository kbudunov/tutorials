import sbt._

object Dependencies {
  val scalaCompat = "2.12"

  private object Version {
    val akka       = "2.5.31"
    val aws        = "1.10.52"
    val akkaConfig = "1.3.0"
  }

  object Akka {
    val AkkaSlf4j  = "com.typesafe.akka"  %% "akka-slf4j"             % Version.akka
    val Stream     = "com.typesafe.akka"  %% "akka-stream"             % Version.akka
    val AlpakkaFTP = "com.lightbend.akka" %% "akka-stream-alpakka-ftp" % "2.0.0-RC1"
  }

  object Kamon {
    val KamonBundle     = "io.kamon" %% "kamon-bundle"     % "2.0.1"
    val KamonPrometheus = "io.kamon" %% "kamon-prometheus" % "2.0.1"
  }

  object Logging {
    val Slf4jApi       = "org.slf4j"                % "slf4j-api"        % "1.7.30"
    val Log4jSlf4jImpl = "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.13.0"
  }

  object AWS {
    val JavaSdkCore = "com.amazonaws" % "aws-java-sdk-core" % Version.aws
    val JavaSdkS3   = "com.amazonaws" % "aws-java-sdk-s3"   % Version.aws
  }

  object Testing {
    val actorTestKit      = "com.typesafe.akka" %% "akka-actor-testkit-typed" % "2.6.4" % Test
    val scalaTest         = "org.scalatest"     %% "scalatest"                % "3.1.1" % Test
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-stream-testkit"      % "2.6.4" % Test
  }

  object Alpakka {
    val elasticsearch      = "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "2.0.2"
  }
}
