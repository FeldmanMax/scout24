package creators

import models.VehicleAdvert
import org.joda.time.DateTime
import utils.{DateTimeFormatters, Utils}

trait VehicleAdvertCreator {
  def getMockedNewVehicleAdvert(suffix: String) = VehicleAdvert(Utils.GUID, s"title_$suffix", 100, isNew = true, Map("fuel_type" -> "gasoline"))
  def getMockedVehicleAdvert(suffix: String): VehicleAdvert = {
    val date: DateTime = DateTimeFormatters.vehicleAdvert.parseDateTime(DateTime.now().toString(DateTimeFormatters.vehicleAdvert))
    (new VehicleAdvertBuilder).withId(Utils.GUID).withTitle(s"title_$suffix").withPrice(100).withIsNew(false).withMileage(1000).withFirstRegistration(date).withMetadata(Map("fuel_type" -> "gasoline"))
      .build
  }

  def getMockedNewVehicleAdvertNoPrice(suffix: String): VehicleAdvert = getMockedNewVehicleAdvert(suffix).copy(price = 0)
  def getMockedNewVehicleAdvertNoTitle(suffix: String): VehicleAdvert = getMockedNewVehicleAdvert(suffix).copy(title = "")
  def getMockedNewVehicleAdvertNoId(suffix: String): VehicleAdvert = getMockedNewVehicleAdvert(suffix).copy(id = "")
  def getMockedNewVehicleAdvertNoFuelType(suffix: String): VehicleAdvert = getMockedNewVehicleAdvert(suffix).copy(metadata = Map("f" -> "x"))
  def getMockedNewVehicleAdvertNoMetadata(suffix: String): VehicleAdvert = getMockedNewVehicleAdvert(suffix).copy(metadata = Map.empty)

  def getMockedVehicleAdvertNoMileage(suffix: String): VehicleAdvert = getMockedVehicleAdvert(suffix).copy(mileage = None)
  def getMockedVehicleAdvertNoFirstRegistration(suffix: String): VehicleAdvert = getMockedVehicleAdvert(suffix).copy(firstRegistration = None)
}

class VehicleAdvertBuilder {
  private var id: String = _
  private var title: String = _
  private var price: Int = _
  private var isNew: Boolean = _
  private var metadata: Map[String, String] = _
  private var mileage: Option[Int] = None
  private var firstRegistration: Option[DateTime] = None

  def withId(id: String): VehicleAdvertBuilder = { this.id = id; this }
  def withTitle(title: String): VehicleAdvertBuilder = { this.title = title; this }
  def withPrice(price: Int): VehicleAdvertBuilder = { this.price = price; this }
  def withIsNew(isNew: Boolean): VehicleAdvertBuilder = { this.isNew = isNew; this }
  def withMetadata(metadata: Map[String, String]): VehicleAdvertBuilder = { this.metadata = metadata; this }
  def withMileage(mileage: Int): VehicleAdvertBuilder = { this.mileage = Option(mileage); this }
  def withFirstRegistration(date: DateTime): VehicleAdvertBuilder = { this.firstRegistration = Option(date); this }

  def build: VehicleAdvert = VehicleAdvert(id, title, price, isNew, metadata, mileage, firstRegistration)
}
