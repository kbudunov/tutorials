import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, RunnableGraph, Sink, Source}

object Main1 extends App {
  //ЭТО ЧЕРТЕЖ
  val helloworldSream1: RunnableGraph[NotUsed] =
    Source.single("Hello world!")
      .via(Flow[String].map(s => s.toUpperCase()))
      .to(Sink.foreach(println))
  //ЭТО ЧЕРТЕЖ
  val helloworldSream2: RunnableGraph[NotUsed] =
    Source.repeat("Hello world!")
      .map(s => s.toUpperCase()) //синтаксический сахар для  .via(Flow[String].map(
      .to(Sink.foreach(println))

  //есть интерфейс MATERIALIZER и единственная реализация ActorMaterializer
  //МАТИРИАЛИЗУЕМ
  implicit val actorSystem = ActorSystem("akka-streams-example")
  implicit val materializer = ActorMaterializer()

  //каждый ран дает НЕЗАВИСИМЫЙ стрим (можно налепить скока захочешь)
  helloworldSream1.run() //выведет HELLO WORLD!
  helloworldSream1.run()

}
