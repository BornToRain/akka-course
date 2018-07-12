package btr.akka.course.chapter3.infrastructure.tool

import java.time.ZoneId
import java.util.Date

/**
  * 日期工具类
  */
object DateTool
{
  /**
    * java.util.Date => java.time.LocalDateTime
    */
  @inline
  def toLocalDateTime(date: Date) = (date.toInstant atZone ZoneId.systemDefault).toLocalDateTime
}
