package btr.akka.course.chapter3

import akka.actor.{ActorSystem, Props, Terminated}
import akka.stream.ActorMaterializer
import akka.util.Timeout
import btr.akka.course.chapter3.application.service.ItemService
import btr.akka.course.chapter3.infrastructure.actor.ActorL
import btr.akka.course.chapter3.infrastructure.repository.ItemMemoryRepositoryImpl
import btr.akka.course.chapter3.interfaces.api.{ItemApi, ItemRoute}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * 项目启动入口
  */
object ItemApp extends App
{
  implicit val as                   = ActorSystem("item-app")
  implicit val am                   = ActorMaterializer()
  implicit val ec: ExecutionContext = as.dispatcher
  implicit val timeout              = Timeout(5.seconds)

  as actorOf (ItemAppSupervisor props, ItemAppSupervisor.NAME)
}

/**
  * 项目监管者 负责监管该模块下的Actor运行情况
  */
class ItemAppSupervisor(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext, timeout: Timeout) extends ActorL
{
  private[this] val repository = new ItemMemoryRepositoryImpl()
  private[this] val service    = context actorOf (ItemService props repository, ItemService.NAME)
  private[this] val route      = new ItemRoute(service)
  private[this] val api        = context actorOf (ItemApi props route, ItemApi.NAME)

  Seq(service, api) foreach (context watch)

  override def receive =
  {
    case Terminated(ref) => log info s"Actor已经停止: ${ref.path}"
      context.system terminate
  }
}

object ItemAppSupervisor
{
  final val NAME = "item-app-supervisor"
  @inline
  def props(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext, timeout: Timeout) = Props(new ItemAppSupervisor)
}