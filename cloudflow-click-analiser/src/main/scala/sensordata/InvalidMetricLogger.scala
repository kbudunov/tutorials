package sensordata

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class InvalidMetricLogger extends AkkaStreamlet {
  // define the inlet
  val inlet = AvroInlet[InvalidMetric]("in")
  // define the streamlet shape
  val shape = StreamletShape.withInlets(inlet)

  // override createLogic to provide the streamlet behavior
  override def createLogic = new RunnableGraphStreamletLogic() {
    val flow = FlowWithOffsetContext[InvalidMetric]
      .map { invalidMetric ⇒
        system.log.warning(s"Invalid metric detected! $invalidMetric")
        invalidMetric
      }

    def runnableGraph = {
      sourceWithOffsetContext(inlet).via(flow).to(sinkWithOffsetContext)
    }
  }
}