package btr.akka.course.chapter3.domain

import scala.concurrent.Future

/**
  * 项目仓储
  */
trait ItemRepository
{
  def insert(d: Item)   : Future[Boolean]
  def update(d: Item)   : Future[Boolean]
  def select(id: String): Future[Option[Item]]
  def selectList        : Future[Seq[Item]]
  def delete(id: String): Future[Boolean]
}
