package models

import org.joda.time.DateTime

sealed trait Advert

case class VehicleAdvert(id: String,
                         title: String,
                         price: Int,
                         isNew: Boolean,
                         metadata: Map[String, String],
                         mileage: Option[Int] = None,
                         firstRegistration: Option[DateTime] = None) extends Advert
