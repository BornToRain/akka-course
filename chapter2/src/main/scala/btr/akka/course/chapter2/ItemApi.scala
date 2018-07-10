package btr.akka.course.chapter2

import java.time.LocalDateTime

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import scala.util.{Failure, Success}

/**
  * 项目Api
  * 继承FailFastCirceSupport,则遇到JSON异常、错误即刻返回
  * 继承ErrorAccumulatingCirceSupport,则会堆积JSON异常、错误批量返回
  */
class ItemApi(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext)
extends Actor with ActorLogging
{
  /**
    * 服务器地址、端口
    */
  val (host, port) = ("0.0.0.0", 9000)
  val itemRoute    = new ItemRoute()

  /**
    * 在本地9000端口上启动一个Akka-http服务
    */
  Http() bindAndHandle (itemRoute.route, host, port) onComplete
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
  def props(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext) = Props(new ItemApi)

  /**
    * 请求参数
    */
  object request
  {
    //创建请求
    case class Create(name: String)
    case class Update(id: String, name: String)
  }
  /**
    * 响应
    */
  object response
  {
    case class ItemDTO(name: String, createTime: LocalDateTime)
  }
}