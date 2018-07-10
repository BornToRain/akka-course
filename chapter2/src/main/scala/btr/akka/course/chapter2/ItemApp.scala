package btr.akka.course.chapter2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

/**
  * 项目启动入口
  */
object ItemApp extends App
{
  implicit val as                   = ActorSystem("item-app")
  implicit val am                   = ActorMaterializer()
  implicit val ec: ExecutionContext = as.dispatcher

  as actorOf (ItemAppSupervisor props, ItemAppSupervisor.NAME)
}
