package btr.akka.course.chapter2

import akka.http.scaladsl.marshalling.{Marshaller, ToEntityMarshaller}
import akka.http.scaladsl.model.{ContentType, ContentTypeRange, HttpEntity}
import akka.http.scaladsl.model.MediaTypes._
import akka.http.scaladsl.unmarshalling.{FromEntityUnmarshaller, Unmarshaller}
import akka.util.ByteString
import io.circe._
import io.circe.generic.extras.{AutoDerivation, Configuration}
import io.circe.java8.time.TimeInstances

import scala.language.postfixOps

/**
  * Akka-http Json解析支持
  * AutoDerivation 自动派生类,默认会给每个class生成一个Encoder和Decoder,提供给akka-http-circe解析json.
  * TimeInstances java8日期类型的解析支持
  */
trait JsonSupport extends CirceSupport with AutoDerivation with TimeInstances
{
  //响应时候紧凑无空格型输出 null值不输出
  override implicit val printer       = Printer.noSpaces copy (dropNullValues = true)
  //蛇形命名
  override implicit val configuration = (Configuration.default withDefaults) withSnakeCaseMemberNames
}

trait CirceSupport
{
  /**
    * 编码、解码媒体类型
    */
  val types             = Seq(`application/json`)
  val unmarshallerTypes = types map ContentTypeRange.apply

  implicit val printer      : Printer
  implicit val configuration: Configuration

  /**
    * Json => HttpEntity
    */
  @inline
  implicit final def jsonMarshaller: ToEntityMarshaller[Json] = Marshaller.oneOf(types: _*)
  {
    `type` => Marshaller.withFixedContentType(ContentType(`type`))(json => HttpEntity(`type`, printer pretty json))
  }
  /**
    * Json => T
    */
  @inline
  implicit final def marshaller[T : Encoder]: ToEntityMarshaller[T] = jsonMarshaller compose Encoder[T].apply
  /**
    * HttpEntity => Json
    */
  @inline
  implicit final def jsonUnmarshaller: FromEntityUnmarshaller[Json] = (Unmarshaller byteStringUnmarshaller) forContentTypes(unmarshallerTypes: _*) map
  {
    case ByteString.empty => throw Unmarshaller.NoContentException
    case d                => (jawn parseByteBuffer d.asByteBuffer) fold (throw _, identity)
  }
  /**
    * HttpEntity => T
    */
  @inline
  implicit final def unmarshaller[T : Decoder]: FromEntityUnmarshaller[T] =
  {
    @inline
    def decode(json: Json) = (Decoder[T] decodeJson json) fold (throw _, identity)
    jsonUnmarshaller map decode
  }
}