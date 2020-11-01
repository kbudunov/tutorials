package sensordata

import java.nio.file
import java.nio.file._

import akka.NotUsed
import akka.stream.IOResult
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl._
import akka.util.ByteString
import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import spray.json.JsonParser

import scala.concurrent.Future
import scala.concurrent.duration._

class SensorDataFileIngress extends AkkaStreamlet {

  import SensorDataJsonSupport._

  val out = AvroOutlet[SensorData]("out").withPartitioner(RoundRobinPartitioner)
  def shape = StreamletShape.withOutlets(out)

  //private val sourceData = VolumeMount("source-data-mount", "/mnt/data", ReadWriteMany)
  //override def volumeMounts = Vector(sourceData)

  val address: String = "E:\\idea\\cloudflow\\CloudFlowClickAnaliser\\src\\main\\resources\\json"
  //"..\\..\\resources\\json"

  val fs = FileSystems.getDefault
  val path = fs.getPath(address)

  override def createLogic = new RunnableGraphStreamletLogic() {
    val listFiles: NotUsed ⇒ Source[file.Path, NotUsed] = { _ ⇒ Directory.ls(path) }
    val readFile: Path ⇒ Source[ByteString, Future[IOResult]] = { path: Path ⇒ FileIO.fromPath(path).via(JsonFraming.objectScanner(Int.MaxValue)) }
    val parseFile: ByteString ⇒ SensorData = { jsonByteString ⇒ JsonParser(jsonByteString.utf8String).convertTo[SensorData] }

    val emitFromFilesContinuously = Source.tick(10.second, 5.second, NotUsed)
      .flatMapConcat(listFiles)
      .flatMapConcat(readFile)
      .map(parseFile)
    def runnableGraph = emitFromFilesContinuously.to(plainSink(out))
  }
}
