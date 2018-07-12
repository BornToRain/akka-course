package btr.akka.course.chapter3.interfaces.api

import java.time.LocalDateTime

import akka.actor.{Actor, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import btr.akka.course.chapter3.infrastructure.actor.ActorL

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * 项目Api
  */
class ItemApi(route: ItemRoute)(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext) extends ActorL
{
  /**
    * 服务器地址、端口
    */
  val (host, port) = ("0.0.0.0", 9000)

  /**
    * 在本地9000端口上启动一个Akka-http服务
    */
  Http() bindAndHandle (route.route, host, port) onComplete
  {
    case Success(d) => log info s"Item Http Server Started: ${d.localAddress}"
    case Failure(e) => log error s"Item Http Server Failed: ${e.getMessage}"
  }

  override def receive = Actor.emptyBehavior
}

object ItemApi
{
  final val NAME = "item-api"
  @inline
  def props(route: ItemRoute)(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext) = Props(new ItemApi(route))

  /**
    * 请求参数
    */
  object request
  {
    case class  Create(name: String)
    case class  Update(id: String, name: String)
    case class  Select(id: String)
    case object SelectList
    case class  Delete(id: String)
  }

  /**
    * 响应参数
    */
  object response
  {
    case class ItemDTO(id: String, itemName: String, createTime: LocalDateTime, updateTime: LocalDateTime)
  }
}
