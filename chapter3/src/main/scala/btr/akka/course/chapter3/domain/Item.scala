package btr.akka.course.chapter3.domain

import java.util.Date

import btr.akka.course.chapter3.infrastructure.tool.{DomainError, Regex}
import btr.akka.course.chapter3.interfaces.api.ItemApi.request.Create

/**
  * 项目聚合根
  */
case class Item(id: String, name: String, createTime: Date, updateTime: Date)

object Item
{
  /**
    * 一个很简单的名字校验
    * 来模拟领域的业务校验
    */
  def validateName(name: String) = Regex.chinese findFirstMatchIn name map (Right(_)) getOrElse Left(DomainError(0, "名字错误!必须包含中文!"))

  /**
    * 创建请求校验
    */
  def validate(r: Create) = for
  {
    _ <- validateName(r.name)
  } yield r
}

