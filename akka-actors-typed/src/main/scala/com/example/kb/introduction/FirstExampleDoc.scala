package com.example.kb.introduction

import akka.actor.typed.scaladsl.{ Behaviors, LoggerOps }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import com.example.kb.utils.ProjectConfig
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

//OOP Style
object FirstExampleDoc {
  private val projectConfig: ProjectConfig = ProjectConfig(ConfigFactory.load)
  private val log                          = LoggerFactory.getLogger("com.example.kb.introduction.FirstExampleDoc")

  def main(args: Array[String]): Unit = {
    log.info("App has been started!")

    val system: ActorSystem[HelloWorldMain.SayHello] =
      ActorSystem(HelloWorldMain(), "helloActorName")

    system ! HelloWorldMain.SayHello("John")
    system ! HelloWorldMain.SayHello("Paul")
  }
}

object HelloWorldMain {
  final case class SayHello(name: String)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      val greeter = context.spawn(HelloWorld(), "greeter") //Create a child Actor HelloWorld

      Behaviors.receiveMessage { message =>
        val replyTo = context.spawn(HelloWorldBot(max = 1), message.name) //Create a child Actor HelloWorldBot (World, Akka)
        greeter ! HelloWorld.Greet(message.name, replyTo) //передаем сообщение типа HelloWorld.Greet  greeterТОРУ
        Behaviors.same
      }
    }
}

object HelloWorld {
  final case class Greet(whom: String, replyTo: ActorRef[Greeted])
  final case class Greeted(whom: String, from: ActorRef[Greet])

  def apply(): Behavior[Greet] = Behaviors.receive { (context, message) =>
    context.log.info("Hello {}!", message.whom)
    message.replyTo ! Greeted(message.whom, context.self)
    Behaviors.same
  }
}

object HelloWorldBot {

  def apply(max: Int): Behavior[HelloWorld.Greeted] = {
    bot(0, max)
  }

  private def bot(greetingCounter: Int, max: Int): Behavior[HelloWorld.Greeted] =
    Behaviors.receive { (context, message) =>
      val n = greetingCounter + 1
      context.log.info2("Greeting {} for {}", n, message.whom)
      if (n == max) {
        Behaviors.stopped
      } else {
        message.from ! HelloWorld.Greet(message.whom, context.self)
        bot(n, max)
      }
    }
}
