package btr.akka.course.chapter3.infrastructure.tool

/**
  * 领域错误
  */
case class DomainError(code: Int, msg: String)

object DomainError
{
  lazy val NotFound = DomainError(0, "数据不存在!")
}