import java.nio.file.Paths

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString

import scala.concurrent._

object Main extends App {
  implicit val system = ActorSystem("QuickStart")

  val source: Source[Int, NotUsed] = Source(1 to 100)
  source.runForeach(i => println(i))


  val factorials = source.scan(BigInt(1))((acc, next) => acc * next)
  val result: Future[IOResult] =
    factorials.map(num => ByteString(s"$num\n")).runWith(FileIO.toPath(Paths.get("factorials.txt")))


  def lineSink(filename: String): Sink[String, Future[IOResult]] =
    Flow[String].map(s => ByteString(s + "\n")).toMat(FileIO.toPath(Paths.get(filename)))(Keep.right)

  factorials.map(_.toString).runWith(lineSink("factorial2.txt"))

}