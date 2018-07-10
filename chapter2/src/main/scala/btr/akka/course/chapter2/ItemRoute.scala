package btr.akka.course.chapter2

import java.time.ZoneId
import java.util.Date

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer
import btr.akka.course.chapter2.ItemApi.request
import btr.akka.course.chapter2.ItemApi.response.ItemDTO

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * 项目路由
  */
class ItemRoute(implicit as: ActorSystem, am: ActorMaterializer, ec: ExecutionContext) extends RestApi
{
  //可变Map 模拟数据
  val items: mutable.HashMap[String, Item] = mutable.HashMap.empty

  @inline
  private[this] def toLocalDateTime(date: Date) = (date.toInstant atZone ZoneId.systemDefault).toLocalDateTime
  //创建单个item
  @inline
  private[this] def create = (post & entity(as[request.Create]))
  {
    r =>
      val id = "1"
      items += (id -> Item(id, r.name))
      created(id)
  }
  //返回item列表
  @inline
  private[this] def list = get
  {
    complete
    {
      items.values map (d => ItemDTO(d.name, toLocalDateTime(d.createTime)))
    }
  }
  //查询单个item
  @inline
  private[this] def show(id: String) = get
  {
    completed
    {
      Future(items get id map (d => ItemDTO(d.name, toLocalDateTime(d.createTime))))
    }
  }
  //更新单个item
  @inline
  private[this] def edit(id: String) = (put & entity(as[request.Update]))
  {
    r => completed
    {
      Future
      {
        items get id map
        {
          d => items update (id, d copy (name = r.name))
            ItemDTO(r.name, toLocalDateTime(d.createTime))
        }
      }
    }
  }
  //删除单个item
  @inline
  private[this] def remove(id: String) = delete
  {
    deleted
    {
      Future(items get id foreach (items -= _.id))
    }
  }

  def route = logRequestResult(("items", Logging.InfoLevel))
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
