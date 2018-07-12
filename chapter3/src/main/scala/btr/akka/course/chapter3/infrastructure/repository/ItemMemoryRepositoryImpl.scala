package btr.akka.course.chapter3.infrastructure.repository

import btr.akka.course.chapter3.domain.{Item, ItemRepository}

import scala.collection.mutable
import scala.concurrent.{ExecutionContext, Future}

/**
  * 项目内存仓储实现
  */
class ItemMemoryRepositoryImpl(implicit ec: ExecutionContext) extends ItemRepository
{
  //可变Map 模拟数据
  val items: mutable.HashMap[String, Item] = mutable.HashMap.empty

  override def insert(d: Item) =
  {
    items += (d.id -> d)
    Future(true)
  }
  override def update(d: Item) =
  {
    items update (d.id, d)
    Future(true)
  }
  override def select(id: String) = Future(items get id)
  override def selectList = Future(items.values.toList)
  override def delete(id: String) =
  {
    items -= id
    Future(true)
  }
}
