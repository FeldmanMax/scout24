package controllers

import com.google.inject.Inject
import commons.PlayControllerWrapper
import models.VehicleAdvert
import play.api.mvc.{Action, EssentialAction}
import services.VehicleAdvertService
import utils.jsonCodec.VehicleAdvertSerializator._

class VehicleAdvertsController @Inject()(service: VehicleAdvertService) extends PlayControllerWrapper {
  def index(sort_by: String): EssentialAction = Action {
    apiEncodedResponse(service.getAll(sort_by))
  }

  def add: EssentialAction = Action { implicit request =>
    // TODO: to for comprehantion
    val result: Either[String, VehicleAdvert] = parseJsonField[VehicleAdvert]("advert").right.flatMap { parsedRequest =>
        service.add(parsedRequest)
    }
    apiEncodedResponse(result)
  }

  def advertById(id: String): EssentialAction = Action {
    apiEncodedResponse(service.getById(id))
  }

  def deleteAdvertById(id: String): EssentialAction = Action {
    apiEncodedResponse(service.delete(id))
  }

  def update(id: String): EssentialAction = Action { implicit request =>
    val result: Either[String, VehicleAdvert] = parseJsonField[VehicleAdvert]("advert").right.flatMap { parsedRequest =>
      val advertToUpdate = if(parsedRequest.id.isEmpty) parsedRequest.copy(id = id) else parsedRequest
      service.update(advertToUpdate)
    }
    apiEncodedResponse(result)
  }
}
