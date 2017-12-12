package repositories

import creators.VehicleAdvertCreator
import models.VehicleAdvert
import org.scalatest.{BeforeAndAfter, FunSuite}
import utils.TimedCache

import org.scalatest.EitherValues._

class ImMemoryAdvertRepositorySuite extends FunSuite with BeforeAndAfter with VehicleAdvertCreator {
  private var repository: ImMemoryAdvertRepository = _

  before {
    repository = new ImMemoryAdvertRepository(TimedCache.apply[String, VehicleAdvert]())
  }

  test("insert/get") {
    val mockedAdvert = getMockedNewVehicleAdvert("someTitle")
    val r = repository.add(mockedAdvert).right.flatMap { _ =>
      repository.getById(mockedAdvert.id).right.flatMap { advert => Right(advert) }
    }
    val advert = r.right.value
    assert(advert.id == mockedAdvert.id)
    assert(advert.title.contains("someTitle"))
  }

  test("insert 10 adverts and get them all back") {
    val list: List[VehicleAdvert] = (0 until 10).map(id => getMockedNewVehicleAdvert(s"$id")).toList
    val addResult: List[Either[String, VehicleAdvert]] = list.map(advert=>repository.add(advert))
    assert(!addResult.exists(result => result.isLeft))
    val advert = repository.getAll.right.flatMap { results => Right(results) }.right.value
    assert(advert.size == 10)
  }

  test("delete an element") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvert("_1")
    repository.add(advert).right.flatMap { _ =>
      repository.delete(advert.id).right.flatMap { deleted =>
        assert(advert.id == deleted.id)
        repository.getById(deleted.id) match {
          case Left(_) => assert(true)
          case Right(_) => assert(false, "Still in the cache")
        }
        Right(Unit)
      }
    }
  }

  test("update element") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvert("_1")
    repository.add(advert).right.flatMap { _ =>
      val updatedAdvert: VehicleAdvert = advert.copy(title = "title_2")
      repository.update(updatedAdvert).right.flatMap { _ =>
        repository.getById(advert.id) match {
          case Left(_) => assert(false)
          case Right(selectedAdvert) => assert(selectedAdvert == updatedAdvert)
        }
        Right("")
      }
    }
  }

  test("insert same id twice") {
    val advert: VehicleAdvert = getMockedNewVehicleAdvert("_1")
    val r = repository.add(advert).right.flatMap { inserted =>Right(repository.add(inserted)) }
    r.right.value match {
      case Right(r) => assert(false, "Inserted twice")
      case Left(_) => assert(true)
    }
  }

  test("get all is empty") {
    repository.getAll match {
      case Left(_) => assert(false)
      case Right(res) => assert(res.size == 0)
    }
  }
}
