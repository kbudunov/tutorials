name := "grpc-verificator-service"
version := "0.1"
scalaVersion := "2.13.1"

enablePlugins(AkkaGrpcPlugin)
// ALPN agent
enablePlugins(JavaAgent)
javaAgents += "org.mortbay.jetty.alpn" % "jetty-alpn-agent" % "2.0.9" % "runtime;test"

mainClass in reStart := Some("ru.maxsbk.readfileandcallgrpc.grpc.DecryptFileServer")