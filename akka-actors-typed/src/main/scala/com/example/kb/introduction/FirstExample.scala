package com.example.kb.introduction

import akka.actor.typed.scaladsl.{ Behaviors, LoggerOps }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior }
import com.example.kb.introduction.EmailSender.RequestEmail
import com.example.kb.utils.ProjectConfig
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

//OOP Style
object FirstExample {
  private val projectConfig: ProjectConfig = ProjectConfig(ConfigFactory.load)
  private val log                          = LoggerFactory.getLogger("com.example.kb.introduction.FirstExample")

  def main(args: Array[String]): Unit = {
    log.info("App has been started!")

    val emailManager: ActorSystem[EmailManager.SayHello] = ActorSystem(EmailManager(), "helloActorName")
    emailManager ! EmailManager.SayHello("Paul") //this is contract message
  }
}

//Manager
object EmailManager {
  final case class SayHello(name: String) //this is contract (command)

  def apply(): Behavior[SayHello] =
    Behaviors.setup { context =>
      val sender = context.spawn(EmailSender(), "emailSender")

      Behaviors.receiveMessage { message =>
        val replyTo = context.spawn(EmailReceiver(max = 3), message.name)
        sender ! EmailSender.RequestEmail(message.name, replyTo) //sending message in sender actor
        Behaviors.same                                           //does not change self state
      }
    }
}

//Sender
object EmailSender {
  final case class RequestEmail(textMessage: String, replyTo: ActorRef[EmailReceiver.ResponseEmail])

  def apply(): Behavior[RequestEmail] = Behaviors.receive { (context, message) =>
    context.log.info("Sending a message {} to EmailReceiver Actor.", message.textMessage)
    message.replyTo ! EmailReceiver.ResponseEmail(message.textMessage, context.self) //context.self == replyTo.me
    Behaviors.same
  }
}

//Receiver
object EmailReceiver {
  final case class ResponseEmail(textMessage: String, from: ActorRef[RequestEmail])

  def apply(max: Int): Behavior[ResponseEmail] = {
    bot(0, max)
  }

  private def bot(messageCounter: Int, max: Int): Behavior[ResponseEmail] =
    Behaviors.receive { (context, message) =>
      val n = messageCounter + 1
      context.log.info2("Message from EmailSender {}.", message.textMessage, message.textMessage)
      if (n == max) {
        Behaviors.stopped
      } else {
        message.from ! EmailSender.RequestEmail(s"${message.textMessage}, $n.", context.self) //send response message to EmailSender
        bot(n, max)                                                                           //update self state
      }
    }
}
