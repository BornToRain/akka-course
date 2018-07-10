package btr.akka.course.chapter2

import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}

import scala.concurrent.{ExecutionContext, Future}

/**
  * RestApi支持
  * Json    => circe
  * HttpDSL => akka-http
  */
abstract class RestApi(implicit ec: ExecutionContext) extends Directives with JsonSupport
{

  def route: Route

  //Restful创建
  def created(resourceId: String) = extractUri
  {
    uri =>
      val location = uri copy (path = uri.path / resourceId)
      respondWithHeader(Location(location))
      {
        complete(Created)
      }
  }
  //Restful查询、更新
  def completed[T: ToResponseMarshaller](request: Future[Option[T]]) = onSuccess(request)
  {
    case Some(d) => complete(ToResponseMarshallable(d))
    case _       => complete(NotFound)
  }
  //Restful删除
  def deleted(resource: Future[Unit]) = onSuccess(resource)
  {
    complete(NoContent)
  }
}
