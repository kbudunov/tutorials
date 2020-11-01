package com.example.kb.introduction

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import akka.NotUsed
import akka.actor.typed.scaladsl.{ Behaviors, LoggerOps }
import akka.actor.typed.{ ActorRef, ActorSystem, Behavior, Terminated }

//FP style
//look description/SecondExamplePlan.jpg
object SecondExampleDoc {

  def apply(): Behavior[NotUsed] =
    Behaviors.setup { context =>
      val chatRoom  = context.spawn(ChatRoom(), "chatroom")
      val clientRef = context.spawn(Client(), "client")
      context.watch(clientRef)
      chatRoom ! GetSession("John Session", clientRef)

      Behaviors.receiveSignal {
        case (_, Terminated(_)) =>
          Behaviors.stopped
      }
    }

  def main(args: Array[String]): Unit = {
    ActorSystem(SecondExampleDoc(), "ChatRoomDemo")
  }

}

object ChatRoom {
  private final case class PublishSessionMessage(sessionName: String, message: String) extends RoomCommand

  def apply(): Behavior[RoomCommand] =
    chatRoom(List.empty) //initialise session's list

  private def chatRoom(sessions: List[ActorRef[SessionCommand]]): Behavior[RoomCommand] =
    Behaviors.receive { (context, message) =>
      message match {
        case GetSession(screenName, client) =>
          // create a child actor for further interaction with the client
          val ses = context.spawn(
            session(context.self, screenName, client),
            name = URLEncoder.encode(screenName, StandardCharsets.UTF_8.name)
          )
          client ! SessionGranted(ses)
          chatRoom(ses :: sessions) //add client session to list, update current state
        case PublishSessionMessage(sessionName, message) =>
          val notification = NotifyClient(MessagePosted(sessionName, message))
          sessions.foreach(_ ! notification)
          Behaviors.same
      }
    }

  private def session(
    room: ActorRef[PublishSessionMessage],
    sessionName: String,
    client: ActorRef[SessionEvent]
  ): Behavior[SessionCommand] =
    Behaviors.receiveMessage {
      case PostMessage(message) =>
        // from client, publish to others via the room
        room ! PublishSessionMessage(sessionName, message)
        Behaviors.same
      case NotifyClient(message) =>
        // published from the room
        client ! message
        Behaviors.same
    }
}

object Client {

  def apply(): Behavior[SessionEvent] =
    Behaviors.setup { context =>
      Behaviors.receiveMessage {
        case SessionGranted(handle) =>
          handle ! PostMessage("Hello World!")
          Behaviors.same
        case MessagePosted(screenName, message) =>
          context.log.info2("message has been posted by '{}': {}", screenName, message)
          Behaviors.stopped
      }
    }
}

//protocol definition
sealed trait RoomCommand
final case class GetSession(screenName: String, replyTo: ActorRef[SessionEvent]) extends RoomCommand

sealed trait SessionEvent
final case class SessionGranted(handle: ActorRef[PostMessage])      extends SessionEvent
final case class SessionDenied(reason: String)                      extends SessionEvent
final case class MessagePosted(screenName: String, message: String) extends SessionEvent

trait SessionCommand
final case class PostMessage(message: String)                 extends SessionCommand
private final case class NotifyClient(message: MessagePosted) extends SessionCommand
