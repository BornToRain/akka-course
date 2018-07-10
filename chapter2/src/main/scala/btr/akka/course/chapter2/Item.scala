package btr.akka.course.chapter2

import java.util.Date

case class Item(id: String, name: String, createTime: Date = new Date)