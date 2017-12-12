package commons

import com.sun.media.sound.InvalidDataException
import io.circe.{Decoder, Encoder, Json, Printer}
import play.api.libs.json.JsValue
import play.api.mvc.{Request, _}
import play.api.mvc.Controller
import io.circe.parser.decode

import scala.util.{Failure, Success, Try}

trait PlayControllerWrapper extends Controller {

  def apiEncodedResponse[A](data: Either[String, A])(implicit m: Encoder[A]): Result = {
    import io.circe.syntax._
    data match {
      case Left(error) => Ok(Map("success" -> "false", "error" -> error).asJson.toString()).withHeaders(CONTENT_TYPE -> "application/json")
      case Right(info) => Ok(Map("success" -> Json.fromString("true"), "data" -> info.asJson).asJson.toString()).withHeaders(CONTENT_TYPE -> "application/json")
    }
  }

  def parseJsonRequest[A: Decoder](implicit request: Request[AnyContent]): Either[String, A] = {
    parse(request, jsBody => jsBody)
  }

  def parseJsonField[A: Decoder](field: String)(implicit request: Request[AnyContent]): Either[String, A] = {
    parse(request, jsBody => (jsBody \ field).get)
  }

  private def parse[A: Decoder](request: Request[AnyContent], extract: JsValue => JsValue): Either[String, A] = {
    request.body.asJson match {
      case None => Left(s"Content is not JSON")
      case Some(jsBody) => decode[A](extract(jsBody).toString()).left.map(x=>x.toString)

//        Try {
//        decode[A](extract(jsBody).toString()) match {
//          case Left(left) => throw new InvalidDataException(left.toString)
//          case Right(r) => r
//        }
//      } match {
//        case Failure(e) => Left(e.getMessage)
//        case Success(obj) => Right(obj)
//      }
    }
  }
}
