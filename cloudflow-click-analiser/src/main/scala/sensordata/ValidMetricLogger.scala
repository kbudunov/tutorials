package sensordata

import cloudflow.akkastream._
import cloudflow.akkastream.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class ValidMetricLogger extends AkkaStreamlet {
  // define the inlet for the egress
  val inlet = AvroInlet[Metric]("in")
  // define the streamlet shape
  val shape = StreamletShape.withInlets(inlet)

  // override createLogic to provide the streamlet behavior
  override def createLogic = new RunnableGraphStreamletLogic() {

    // log to output
    def log(metric: Metric) = {
      system.log.info(metric.toString)
    }

    // flow to define the metric to log
    def flow = {
      FlowWithOffsetContext[Metric]
        .map { validMetric ⇒
          log(validMetric)
          validMetric
        }
    }

    // build the computation graph
    def runnableGraph = {
      sourceWithOffsetContext(inlet).via(flow).to(sinkWithOffsetContext)
    }
  }
}