name := "cephAmazonS3Scala"
version := "0.1"
scalaVersion := "2.13.1"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk-core" % "1.10.52",
  "com.amazonaws" % "aws-java-sdk-s3" % "1.10.52")