package sensordata

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._

import cloudflow.streamlets._
import cloudflow.streamlets.avro._
import SensorDataJsonSupport._

class SensorDataHttpIngress extends AkkaServerStreamlet {
  // create an outlet that will use the default Kafka partitioning
  val out =
  // define the streamlet shapte
    AvroOutlet[SensorData]("out").withPartitioner(RoundRobinPartitioner)
  def shape = StreamletShape.withOutlets(out)
  // override createLogic to provide the streamlet behavior
  override def createLogic = HttpServerLogic.default(this, out)
}