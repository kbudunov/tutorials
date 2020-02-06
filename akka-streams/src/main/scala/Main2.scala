import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.scaladsl.Source

object Main2 extends App {

  implicit val system = ActorSystem("QuickStart")

  val source: Source[Int, NotUsed] = Source(1 to 100)
  source
    .map(i => i * 1)
    .filter(i => i % 2 == 0)
    .runForeach(i => println(i))
}
