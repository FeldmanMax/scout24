package utils.jsonCodec

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import utils.DateTimeFormatters

object DateTimeSerializator {

  private lazy val vehicleAdvertDateTimeFormatter: DateTimeFormatter = DateTimeFormatters.vehicleAdvert

  implicit val vehicleAdvertDateFormat : Decoder[Option[DateTime]] with Encoder[Option[DateTime]] = new Decoder[Option[DateTime]] with Encoder[Option[DateTime]]{
    override def apply(c: HCursor): Result[Option[DateTime]] = Decoder.decodeString.map { s =>
      Some(vehicleAdvertDateTimeFormatter.parseDateTime(s))
    }.apply(c)

    override def apply(a: Option[DateTime]): Json = {
      a match {
        case None => null
        case Some(date) => Encoder.encodeString(date.toString(vehicleAdvertDateTimeFormatter))
      }
    }
  }
}
