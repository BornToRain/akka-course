package btr.akka.course.chapter3.infrastructure.http

import akka.http.scaladsl.marshalling.{ToResponseMarshallable, ToResponseMarshaller}
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.{Directives, Route}
import btr.akka.course.chapter3.infrastructure.tool.DomainError

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
  @inline
  def created(f: Future[Either[DomainError, String]]) = onSuccess(f)
  {
    case Left(e)  => complete(BadRequest -> e)
    case Right(d) => extractUri
    {
      uri =>
        val location = uri copy (path = uri.path / d)
        respondWithHeader(Location(location)) { complete(Created) }
    }
  }
  //Restful查询、更新
  @inline
  def completed[T: ToResponseMarshaller](request: Future[Option[T]]) = onSuccess(request)
  {
    case Some(d) => complete(ToResponseMarshallable(d))
    case _       => complete(NotFound)
  }
  //Restful删除
  @inline
  def deleted(resource: Future[Boolean]) = onSuccess(resource)
  {
    case true => complete(NoContent)
    case _    => complete(NotFound)
  }
}
