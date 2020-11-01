package sensordata

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets.{ RoundRobinPartitioner, StreamletShape }
import cloudflow.streamlets.avro._

class SensorDataToMetrics extends AkkaStreamlet {
  // define inlets and outlets
  val in = AvroInlet[SensorData]("in")
  val out = AvroOutlet[Metric]("out").withPartitioner(RoundRobinPartitioner)
  // define the streamlet shape
  val shape = StreamletShape(in, out)
  // define a flow that makes it possible for cloudflow to commit reads
  def flow = {
    FlowWithOffsetContext[SensorData]
      .mapConcat { data ⇒
        List(
          Metric(data.deviceId, data.timestamp, "power", data.measurements.power),
          Metric(data.deviceId, data.timestamp, "rotorSpeed", data.measurements.rotorSpeed),
          Metric(data.deviceId, data.timestamp, "windSpeed", data.measurements.windSpeed)
        )
      }
  }
  // override createLogic to provide streamlet behavior
  override def createLogic = new RunnableGraphStreamletLogic() {
    def runnableGraph = sourceWithOffsetContext(in).via(flow).to(sinkWithOffsetContext(out))
  }
}