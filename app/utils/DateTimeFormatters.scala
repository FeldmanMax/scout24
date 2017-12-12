package utils

import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object DateTimeFormatters {
  val vehicleAdvert: DateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
}
