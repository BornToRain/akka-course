package btr.akka.course.chapter2

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import btr.akka.course.chapter2.ItemApi.request
import btr.akka.course.chapter2.ItemApi.response.ItemDTO
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.duration._

/**
  * 针对项目接口的测试用例
  */
class ItemApiSpec extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport
{
  //超时时间: 5s
  implicit val timeout = RouteTestTimeout(5.seconds)

  val itemRoute = new ItemRoute()
  val path      = "/items"

  "项目接口" should
  {
    "项目列表" in
    {
      Get(path) ~> itemRoute.route ~> check
      {
        status                 === OK
        contentType            === `application/json`
        entityAs[Seq[ItemDTO]] === Seq.empty
      }
    }
    "创建项目" in
    {
      Post(path, request.Create("TestItemName")) ~> itemRoute.route ~> check
      {
        response === HttpResponse(Created, List(RawHeader("Location", "")))
      }
    }
    "查询单个项目" in
    {
      //不存在的情况
      Get(s"$path/2") ~> itemRoute.route ~> check
      {
        status === NotFound
      }
      //存在的情况
      Get(s"$path/1") ~> itemRoute.route ~> check
      {
        status === OK
      }
    }
    "修改单个项目" in
    {
      Put(s"$path/1", request.Update("1", "UpdateItemName")) ~> itemRoute.route ~> check
      {
        status === OK
      }
    }
    "再次查询项目列表" in
    {
      Get(path) ~> itemRoute.route ~> check
      {
        status                 === OK
        contentType            === `application/json`
        entityAs[Seq[ItemDTO]] !== Seq.empty
      }
    }
    "删除单个项目" in
    {
      Delete(s"$path/1") ~> itemRoute.route ~> check
      {
        status === NoContent
      }
    }
  }
}
