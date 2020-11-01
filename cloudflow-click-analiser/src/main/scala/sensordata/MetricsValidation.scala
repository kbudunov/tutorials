package sensordata

import cloudflow.akkastream._
import cloudflow.akkastream.util.scaladsl._
import cloudflow.streamlets._
import cloudflow.streamlets.avro._

class MetricsValidation extends AkkaStreamlet {
  // define inlets and outlets
  // note that we have 2 outlets corresponding to valid and invalid records
  val in = AvroInlet[Metric]("in")
  val invalid = AvroOutlet[InvalidMetric]("invalid").withPartitioner(metric ⇒ metric.metric.deviceId.toString)
  val valid = AvroOutlet[Metric]("valid").withPartitioner(RoundRobinPartitioner)
  // define the streamlet shape
  val shape = StreamletShape(in).withOutlets(invalid, valid)

  // override createLogic to provide streamlet behavior
  override def createLogic = new SplitterLogic(in, invalid, valid) {
    def flow = flowWithOffsetContext()
      .map { metric ⇒
        if (!SensorDataUtils.isValidMetric(metric)) Left(InvalidMetric(metric, "All measurements must be positive numbers!"))
        else Right(metric)
      }
  }
}