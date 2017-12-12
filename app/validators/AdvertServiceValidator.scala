package validators

import models.{Advert, VehicleAdvert}

trait AdvertServiceValidator {
  def isValid[T <: Advert](advert: T): Either[String, Unit]
}

class VehicleAdvertServiceValidator extends AdvertServiceValidator {
  override def isValid[T <: Advert](advert: T): Either[String, Unit] = {
    val vehicleAdvert = advert.asInstanceOf[VehicleAdvert]
    basicValidations(vehicleAdvert).right.flatMap { _ => isValidImpl(vehicleAdvert)}
  }

  private def isValidImpl(advert: VehicleAdvert): Either[String, Unit] = {
    if(advert.isNew) {
      if(!(advert.mileage.isEmpty && advert.firstRegistration.isEmpty)) Left("Mileage OR First registration is sent for a new vehicle")
      else                                                Right(Unit)
    }
    else {
      if(advert.mileage.isEmpty || advert.firstRegistration.isEmpty)    Left("Either Mileage OR First registration were not provided")
      else                                                Right(Unit)
    }
  }

  private def basicValidations(advert: VehicleAdvert): Either[String, Unit] = {
    if(advert.title.isEmpty)                                                    Left("No Title")
    else if(advert.price <= 0)                                                  Left("Invalid Price")
    else if(advert.id.isEmpty)                                                  Left("No Id")
    else if(advert.metadata == null || advert.metadata.isEmpty)                 Left("No metadata was supplied")
    else if(!advert.metadata.contains("fuel_type"))                             Left("Fuel is not present in the request")
    else                                                                        Right(Unit)
  }
}
