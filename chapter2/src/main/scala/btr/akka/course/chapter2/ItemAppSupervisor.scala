package btr.akka.course.chapter2

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

/**
  * 项目监管者 负责监管该模块下的Actor运行情况
  */
class ItemAppSupervisor(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext) extends Actor with ActorLogging
{
  private[this] val api = context actorOf (ItemApi props, ItemApi.NAME)

  context watch api

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
  def props(implicit as: ActorSystem, ac: ActorMaterializer, ec: ExecutionContext) = Props(new ItemAppSupervisor)
}
