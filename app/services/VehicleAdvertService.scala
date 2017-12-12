package services

import com.google.inject.Inject
import com.google.inject.name.Named
import models.VehicleAdvert
import repositories.AdvertRepository
import sorters.AdvertSorter
import utils.Utils
import validators.AdvertServiceValidator

class VehicleAdvertService @Inject() (@Named("vehicle_advert_repository") repository: AdvertRepository,
                                      @Named("vehicle_advert_validator")  validator:  AdvertServiceValidator,
                                      @Named("vehicle_advert_sorter")     sorter:     AdvertSorter[VehicleAdvert]) {
  def add(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    val advertWithId: VehicleAdvert = advert.copy(id = Utils.GUID)
    for {
      _ <- validator.isValid(advertWithId).right
      advert <- repository.add(advertWithId).right
    } yield advert
  }

  def getById(key: String): Either[String, VehicleAdvert] = repository.getById(key)
  def getAll(sort_by: String): Either[String, List[VehicleAdvert]] = {
    for{
      adverts <- repository.getAll.right
    } yield sorter.sort(adverts, sort_by)
  }
  def delete(key: String): Either[String, VehicleAdvert] = repository.delete(key)
  def update(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    validator.isValid(advert).right.flatMap { _ =>
      repository.update(advert)
    }
  }
}
