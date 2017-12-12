package utils

import creators.VehicleAdvertCreator
import models.VehicleAdvert
import org.scalatest.FunSuite
import io.circe.syntax._
import utils.jsonCodec.VehicleAdvertSerializator._
import io.circe.parser.decode

class VehicleAdvertSerializationSuite extends FunSuite with VehicleAdvertCreator {
  test("invalid - new advert - no price") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvertNoPrice("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid - new advert - no title") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvertNoTitle("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid - new advert - no id") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvertNoId("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid - new advert - no fuel type") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvertNoFuelType("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid - new advert - no metadata") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvertNoMetadata("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid = existing advert - no mileage") {
    val advert: VehicleAdvert = getMockedVehicleAdvertNoMileage("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }

  test("invalid = existing advert - no first registration") {
    val advert: VehicleAdvert = getMockedVehicleAdvertNoFirstRegistration("_suffix")
    val json:String = advert.asJson.toString()
    decode[VehicleAdvert](json).right.flatMap { result =>
      assert(advert == result)
      Right(Unit)
    }
  }
}