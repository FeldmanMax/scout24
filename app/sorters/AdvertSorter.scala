package sorters

import models.{Advert, VehicleAdvert}

trait AdvertSorter[T <: Advert] {
  def sort(data: List[T], sort_by: String): List[T]
}

class VehicleAdvertSorter extends AdvertSorter[VehicleAdvert] {
  private val orderByPrice: Ordering[VehicleAdvert] = Ordering.by(x=>x.price)
  private val orderByTitle: Ordering[VehicleAdvert] = Ordering.by(x=>x.title)
  private val defaultOrdering: Ordering[VehicleAdvert] = Ordering.by(x=>x.id)
  override def sort(data: List[VehicleAdvert], sort_by: String): List[VehicleAdvert] = {
    sortImpl(data, sort_by)
  }

  private def sortImpl(data: List[VehicleAdvert], sort_by: String): List[VehicleAdvert] = {
    val ordering: Ordering[VehicleAdvert] = sort_by match {
      case "title" => orderByTitle
      case "price" => orderByPrice
      case _ => defaultOrdering
    }
    data.sorted(ordering)
  }
}