package btr.akka.course.chapter3.interfaces.api

import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import akka.pattern._
import akka.stream.ActorMaterializer
import akka.util.Timeout
import btr.akka.course.chapter3.infrastructure.http.RestApi
import btr.akka.course.chapter3.infrastructure.tool.DomainError
import btr.akka.course.chapter3.interfaces.api.ItemApi.request
import btr.akka.course.chapter3.interfaces.api.ItemApi.request.{Delete, Select, SelectList}
import btr.akka.course.chapter3.interfaces.api.ItemApi.response.ItemDTO

import scala.concurrent.ExecutionContext

/**
  * 项目路由
  */
class ItemRoute(service: ActorRef)(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext, timeout: Timeout) extends RestApi
{
  //创建单个item
  @inline
  private[this] def create = (post & entity(as[request.Create]))
  {
    r => created
    {
      (service ? r).mapTo[Either[DomainError, String]]
    }
  }
  //返回item列表
  @inline
  private[this] def list = get
  {
    complete
    {
      (service ? SelectList).mapTo[Seq[ItemDTO]]
    }
  }
  //查询单个item
  @inline
  private[this] def show(id: String) = get
  {
    completed
    {
      (service ? Select(id)).mapTo[Option[ItemDTO]]
    }
  }
  //更新单个item
  @inline
  private[this] def edit(id: String) = (put & entity(as[request.Update]))
  {
    r => completed
    {
      (service ? r).mapTo[Option[ItemDTO]]
    }
  }
  //删除单个item
  @inline
  private[this] def remove(id: String) = delete
  {
    deleted
    {
      (service ? Delete(id)).mapTo[Boolean]
    }
  }

  override def route = logRequestResult(("items", Logging.InfoLevel))
  {
    //路由开始
    pathPrefix("items")
    {
      //匹配到/items即终止匹配
      pathEnd
      {
        create ~ list
      } ~
      //匹配到/items/{path-param}
      path(Segment)
      {
        id => show(id) ~ edit(id) ~ remove(id)
      }
    }
  }
}
