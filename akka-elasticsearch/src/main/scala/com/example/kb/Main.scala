package com.example.kb

import java.util.Date

import akka.NotUsed
import akka.actor.ActorSystem
import akka.event.slf4j.SLF4JLogging
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.alpakka.elasticsearch.{ApiVersion, ElasticsearchWriteSettings, WriteMessage}
import akka.stream.scaladsl.{Sink, Source}
import com.example.kb.utils.ProjectConfig
import com.typesafe.config.ConfigFactory
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient
import spray.json.DefaultJsonProtocol.{jsonFormat1, _}
import spray.json.JsonFormat

object Main extends SLF4JLogging {
  implicit val system: ActorSystem = ActorSystem("BaseAkkaSample")
  val projectConfig: ProjectConfig = ProjectConfig(ConfigFactory.load)

  def main(args: Array[String]): Unit = {
    println(projectConfig.someConfig.message)

    implicit val client                   = RestClient.builder(new HttpHost("localhost", 9200)).build()
    implicit val format: JsonFormat[Book] = jsonFormat1(Book)
    val baseWriteSettings                 = ElasticsearchWriteSettings().withApiVersion(ApiVersion.V7)

    val indexName: String = "source"
//    val copy = ElasticsearchSource
//      .typed[Book](
//        indexName = "source",
//        typeName = "_doc",
//        query = """{"match_all": {}}"""
//      )
//      .map { message: ReadResult[Book] =>
//        WriteMessage.createIndexMessage(message.id, message.source)
//      }
//      .runWith(
//        ElasticsearchSink.create[Book](
//          indexName,
//          typeName = "_doc",
//          settings = baseWriteSettings
//        )
//      )

    val requests = List[WriteMessage[Book, NotUsed]](
      WriteMessage.createIndexMessage(id = "00016", source = Book("Book 11"))
//      WriteMessage.createIndexMessage(id = "00007", source = Book("Book 12")),
//      WriteMessage.createIndexMessage(id = "00008", source = Book("Book 13")),
//      WriteMessage.createIndexMessage(id = "00009", source = Book("Book 14")),
//      WriteMessage.createIndexMessage(id = "00010", source = Book("Book 15")),
//      WriteMessage.createDeleteMessage(id = "00002")
    )

    val writeResults = Source(requests)
      .via(
        ElasticsearchFlow.create[Book](
          indexName,
          "_doc",
          baseWriteSettings
        )
      )
      .runWith(Sink.seq)

    log.info(s"App started!")
  }
}

case class Book(title: String)

case class NotificationEvent(
  _file_id: String,
  loading_id: String, //как формируется?
  _file_name: String,
  file_path: String, //скорей всего содержится в _file_name
  _stage_name: String,
  time_start: Date, //взять eventTime
  time_end: Date,
  file_size: Integer,  //тянуть надо из методаты
  client_sin: Integer, //как-то достается из лоадинг ид?
  _file_type: String,
  _status: String,
  error_code: String, //тянуть из метадаты
  error_text: String  //тянуть из метадаты
)

//case class NotificationEvent(
//  fileId: String,
//  fileName: String,
//  fileType: String,
//  stageName: String,
//  status: String,
//  eventTime: Long,
//  versionId: String,
//  internalFileId: String,
//  internalFileVersionId: String,
//  longTermVersionId: String = "")
