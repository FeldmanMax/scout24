package validators

import creators.VehicleAdvertCreator
import org.scalatest.{BeforeAndAfter, FunSuite}

class VehicleAdvertServiceValidatorSuite extends FunSuite with BeforeAndAfter with VehicleAdvertCreator {
  private var validator: AdvertServiceValidator = _

  before {
    validator = new VehicleAdvertServiceValidator()
  }
  test("valid - new advert") {
    validator.isValid(getMockedNewVehicleAdvert("_1")).right.flatMap { _ => Right(Unit) }
  }

  test("valid - old advert") {
    validator.isValid(getMockedVehicleAdvert("_1")).right.flatMap { _ => Right(Unit) }
  }

  test("invalid - new advert - no price") {
    validator.isValid(getMockedNewVehicleAdvertNoPrice("_suffix")) match {
      case Left(left) => assert(left == "Invalid Price")
      case Right(_) => assert(false)
    }
  }

  test("invalid - new advert - no title") {
    validator.isValid(getMockedNewVehicleAdvertNoTitle("_suffix")) match {
      case Left(left) => assert(left == "No Title")
      case Right(_) => assert(false)
    }
  }

  test("invalid - new advert - no id") {
    validator.isValid(getMockedNewVehicleAdvertNoId("_suffix")) match {
      case Left(left) => assert(left == "No Id")
      case Right(_) => assert(false)
    }
  }

  test("invalid - new advert - no fuel type") {
    validator.isValid(getMockedNewVehicleAdvertNoFuelType("_suffix")) match {
      case Left(left) => assert(left == "Fuel is not present in the request")
      case Right(_) => assert(false)
    }
  }

  test("invalid - new advert - no metadata") {
    validator.isValid(getMockedNewVehicleAdvertNoMetadata("_suffix")) match {
      case Left(left) => assert(left == "No metadata was supplied")
      case Right(_) => assert(false)
    }
  }

  test("invalid = existing advert - no mileage") {
    validator.isValid(getMockedVehicleAdvertNoMileage("_suffix")) match {
      case Left(left) => assert(left == "Either Mileage OR First registration were not provided")
      case Right(_) => assert(false)
    }
  }

  test("invalid = existing advert - no first registration") {
    validator.isValid(getMockedVehicleAdvertNoFirstRegistration("_suffix")) match {
      case Left(left) => assert(left == "Either Mileage OR First registration were not provided")
      case Right(_) => assert(false)
    }
  }
}
