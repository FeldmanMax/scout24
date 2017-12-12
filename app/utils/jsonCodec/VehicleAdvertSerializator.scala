package utils.jsonCodec

import models.VehicleAdvert
import org.joda.time.DateTime
import utils.DateTimeFormatters

object VehicleAdvertSerializator {

  import io.circe._
    implicit val vehicleAdvertDecoder: Decoder[VehicleAdvert] = new Decoder[VehicleAdvert] {
      import cats.syntax.either._
      import utils.jsonCodec.DateTimeSerializator.vehicleAdvertDateFormat
      final def apply(c: HCursor): Decoder.Result[VehicleAdvert] = {
        for {
          title <- c.downField("title").as[String]
          price <- c.downField("price").as[Int]
          isNew <- c.downField("isNew").as[Boolean]
          metadata <- c.downField("metadata").as[Map[String, String]]
        } yield VehicleAdvert(toStringOrDefault(c.downField("id")), title, price, isNew, metadata, toIntOption(c.downField("mileage")), toDateTimeOption(c.downField("firstRegistration")))
      }

      private def toStringOrDefault(cursor: ACursor): String = {
        if(cursor.succeeded) cursor.as[String].getOrElse("")
        else                 ""
      }

      private def toIntOption(cursor: ACursor): Option[Int] = {
        if(cursor.succeeded)  {
          cursor.as[Int] match {
            case Left(_) => None
            case Right(v) => Some(v)
          }
        }
        else                  None
      }

      private def toDateTimeOption(cursor: ACursor): Option[DateTime] = {
        if(cursor.succeeded)  {
          cursor.as[String] match {
            case Left(_) => None
            case Right(v) => Some(DateTimeFormatters.vehicleAdvert.parseDateTime(v))
          }
        }
        else                  None
      }
    }


  implicit val encodeVehicleAdvertList: Encoder[List[VehicleAdvert]] = new Encoder[List[VehicleAdvert]] {
    import io.circe.syntax._
    override def apply(a: List[VehicleAdvert]): Json = {
      val seq: List[(String, Json)] =  a.map(x=>x.id -> x.asJson)
      Json.obj(seq: _*)
    }
  }

  implicit val encodeVehicleAdvert: Encoder[VehicleAdvert] = new Encoder[VehicleAdvert] {

    import io.circe.syntax._

    final def apply(a: VehicleAdvert): Json = {
      val members: List[Option[(String, Json)]] = List(
        Option(("id", Json.fromString(a.id))),
        Option(("title", Json.fromString(a.title))),
        Option(("price", Json.fromInt(a.price))),
        Option(("metadata", a.metadata.asJson)),
        Option(("isNew", Json.fromBoolean(a.isNew))),getMileage(a.mileage), getFirstRegistration(a.firstRegistration))
      Json.obj(members.filter(x=>x.isDefined).map(x=>x.get): _*)
    }

    private def getMileage(mileage: Option[Int]): Option[(String, Json)] = {

      mileage match {
        case None => None
        case Some(m) => Option(("mileage", Json.fromInt(m)))
      }
    }

    private def getFirstRegistration(registration: Option[DateTime]): Option[(String, Json)] = {
      registration match {
        case None => None
        case Some(r) => Option(("firstRegistration", Json.fromString(r.toString(DateTimeFormatters.vehicleAdvert))))
      }
    }
  }
}