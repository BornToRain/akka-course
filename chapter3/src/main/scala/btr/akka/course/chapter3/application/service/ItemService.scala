package btr.akka.course.chapter3.application.service

import java.util.Date

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import btr.akka.course.chapter3.domain.{Item, ItemRepository}
import btr.akka.course.chapter3.infrastructure.tool.{DateTool, IdWorker}
import btr.akka.course.chapter3.interfaces.api.ItemApi.request._
import btr.akka.course.chapter3.interfaces.api.ItemApi.response.ItemDTO

import scala.concurrent.ExecutionContext
import scala.language.postfixOps

/**
  * 项目应用服务
  */
class ItemService(repository: ItemRepository)(implicit ec: ExecutionContext) extends Actor with ActorLogging
{
  @inline
  private[this] def create(r: Create) = Item validate r map
  {
    r =>
      //雪花主键
      val id = IdWorker.getId
      repository insert Item(id, r.name, new Date, new Date)
      id
  }
  @inline
  private[this] def update(r: Update) = repository select r.id map
  {
    _ map
    {
      d =>
        //本次更新的数据
        val update = d copy (name = r.name)
        repository update update
        //转换DTO模型,不对外暴露领域模型.
        ItemDTO(update.id, update.name, DateTool.toLocalDateTime(update.createTime), DateTool.toLocalDateTime(update.updateTime))
    }
  }
  @inline
  private[this] def select(id: String) = repository select id map
  {
    _ map
    {
      d =>
        //转换DTO模型,不对外暴露领域模型.
        ItemDTO(d.id, d.name, DateTool.toLocalDateTime(d.createTime), DateTool.toLocalDateTime(d.updateTime))
    }
  }
  @inline
  private[this] def selectList = repository.selectList map
  {
    _ map
    {
      d =>
        //转换DTO模型,不对外暴露领域模型.
        ItemDTO(d.id, d.name, DateTool.toLocalDateTime(d.createTime), DateTool.toLocalDateTime(d.updateTime))
    }
  }
  @inline
  private[this] def delete(id: String) = repository delete id

  override def receive =
  {
    //创建
    case r: Create  => sender ! create(r)
    //更新
    case r: Update  => update(r) pipeTo sender
    //查询
    case Select(id) => select(id) pipeTo sender
    //列表
    case SelectList => selectList pipeTo sender
    //删除
    case Delete(id) => delete(id) pipeTo sender
  }
}

object ItemService
{
  final val NAME = "item-service"
  @inline
  def props(repository: ItemRepository)(implicit ec: ExecutionContext) = Props(new ItemService(repository))
}