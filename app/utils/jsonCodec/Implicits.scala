package utils.jsonCodec

import io.circe.Json
import io.circe.Printer

object Implicits {
  implicit class EncoderOpsImpl(val json: Json) extends AnyVal {
    def asPretty: String = {
      val printer = Printer.noSpaces.copy(dropNullKeys = true)
      printer.pretty(json)
    }
  }
}
