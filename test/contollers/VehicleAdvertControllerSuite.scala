package contollers

import controllers.VehicleAdvertsController
import creators.VehicleAdvertCreator
import models.VehicleAdvert
import org.scalatestplus.play._
import play.api.libs.json.{JsDefined, Json}
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import repositories.InMemoryAdvertRepository
import services.VehicleAdvertService
import validators.VehicleAdvertServiceValidator
import utils.jsonCodec.VehicleAdvertSerializator._
import io.circe.parser.decode
import sorters.VehicleAdvertSorter
import utils.TimedCache

class VehicleAdvertControllerSuite extends PlaySpec with Results with VehicleAdvertCreator {

  trait WithControllerAndRequest {
    val testController = new VehicleAdvertsController(new VehicleAdvertService (new InMemoryAdvertRepository(TimedCache.apply[String, VehicleAdvert]()), new VehicleAdvertServiceValidator, new VehicleAdvertSorter))
    def fakeRequest(method: String = "GET", route: String = "/") = FakeRequest(method, route)
      .withHeaders(
        ("Date", "2014-10-05T22:00:00"),
        ("Authorization", "username=bob;hash=foobar==")
      )

  }

  "REST API" should {
    "add" in new WithControllerAndRequest {
      val uri: String = "http://localhost:9000/api/adverts/vehicle/add"
      val request = fakeRequest("POST", uri).withJsonBody(Json.parse(
        s"""
          |{
          |	"advert": {
          |		"id": "",
          |		"title": "Mazeratti_1",
          |		"price": 12,
          |   "isNew": false,
          |   "metadata": {
          |     "fuel_type": "Gasoline"
          |   },
          |   "mileage": 100,
          |   "firstRegistration": "2010-12-10"
          |	}
          |}
        """.stripMargin
      ))
      val apiResult = call(testController.add, request)
      val jsonResult = contentAsJson(apiResult)
      val retResult = decode[VehicleAdvert]((jsonResult \ "data").asInstanceOf[JsDefined].value.toString())
      val postResponse = retResult.right.get
      assert(postResponse.title == "Mazeratti_1")
      assert(!postResponse.isNew)

      val delete_uri = s"http://localhost:9000/api/adverts/vehicle/${retResult.right.get.id}"
      val deleteRequest = fakeRequest("DELETE", delete_uri)
      contentAsJson(call(testController.deleteAdvertById(retResult.right.get.id), deleteRequest))
    }

    "get - all" in new WithControllerAndRequest {
      val uri: String = "http://localhost:9000/api/adverts/vehicle/add"
      val request = fakeRequest("POST", uri).withJsonBody(Json.parse(
        s"""
           |{
           |	"advert": {
           |		"id": "",
           |		"title": "Mazeratti_1",
           |		"price": 12,
           |   "isNew": false,
           |   "metadata": {
           |     "fuel_type": "Gasoline"
           |   },
           |   "mileage": 100,
           |   "firstRegistration": "2010-12-10"
           |	}
           |}
        """.stripMargin
      ))
      contentAsJson(call(testController.add, request))

      val uriGet: String = "http://localhost:9000/api/adverts/vehicle/"
      val getRequest = fakeRequest("GET", uriGet)
      val getResult = call(testController.index(""), getRequest)
      val jsonResult = contentAsJson(getResult)
      decode[Map[String, VehicleAdvert]]((jsonResult \ "data").asInstanceOf[JsDefined].value.toString()) match {
        case Left(_) => assert(false)
        case Right(v) =>
          assert(v.size == 1, s"current size is ${v.size}")
      }
    }
  }
}
