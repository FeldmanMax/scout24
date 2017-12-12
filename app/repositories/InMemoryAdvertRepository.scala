package repositories

import models.VehicleAdvert
import utils.{DateTimeFormatters, TimedCache}
import anorm.{~, _}
import anorm.SqlParser.{bool, int, scalar, str}
import org.joda.time.DateTime

trait AdvertRepository {
  def getById(id: String): Either[String, VehicleAdvert]
  def getAll: Either[String, List[VehicleAdvert]]
  def add(advert: VehicleAdvert): Either[String, VehicleAdvert]
  def delete(id: String): Either[String, VehicleAdvert]
  def update(advert: VehicleAdvert): Either[String, VehicleAdvert]
}

class InMemoryAdvertRepository(cache: TimedCache[String, VehicleAdvert]) extends AdvertRepository{
  def getById(id: String): Either[String, VehicleAdvert] = cache.getWithError(id)
  def getAll: Either[String, List[VehicleAdvert]] = cache.getAll()
  def add(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    getById(advert.id) match {
      case Left(_) => update(advert)
      case Right(r) => Left(s"${r.id} is already present")
    }
  }
  def delete(id: String): Either[String, VehicleAdvert] = cache.remove(id)

  def update(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    if(advert.id.isEmpty) Left("Invalid id")
    else                  {
      cache.put(advert.id, advert)
      Right(advert)
    }
  }
}

class SqlAdvertRepository(db: DBWrapper) extends AdvertRepository {
  override def getById(id: String): Either[String, VehicleAdvert] = {
    for {
      metadata <- getAdvertMetadata(id).right
      advert <- getByIdImpl(id).right
    } yield advert.copy(metadata = metadata)
  }

  override def getAll(): Either[String, List[VehicleAdvert]] = {
    keys.right.flatMap { ids =>
      val results: List[Either[String, VehicleAdvert]] = ids.map(id => getById(id))
      if(results.exists(advert => advert.isLeft)) {
        Left(results.filter(advert=>advert.isLeft).map(advert => advert.left.get) mkString "")
      }
      else Right(results.map(advert => advert.right.get))
    }
  }
  override def add(advert: VehicleAdvert) = addAdvertImpl(advert)
  override def delete(id: String): Either[String, VehicleAdvert] = {
    for {
      advert <- getById(id).right
      _ <- deleteMetadata(id).right
      _ <- db.withSafeConnection({ implicit c =>
        SQL"""DELETE FROM  advert_vehicle WHERE id=$id""".executeUpdate()
      }).right.flatMap { _ => Right(advert) }.right
    } yield advert
  }

  override def update(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    Right(advert)
  }

  private def getByIdImpl(id: String): Either[String, VehicleAdvert] = {
    db.withSafeConnection({ implicit c =>
      SQL"""SELECT  * FROM  advert_vehicle""".as(defaultParser.singleOpt)
    }) match {
      case Left(left) => Left(left)
      case Right(advert) => advert match {
        case None => Left(s"Could not bring $id")
        case Some(value) => Right(value)
      }
    }
  }

  private def deleteMetadata(id: String): Either[String, Map[String, String]] = {
    getAdvertMetadata(id).right.flatMap { metadata =>
      db.withSafeConnection({ implicit c =>
        SQL"""DELETE FROM  advert_metadata WHERE id=$id""".executeUpdate()
      }).right.flatMap { _ => Right(metadata) }
    }
  }

  private def getAdvertMetadata(id: String): Either[String, Map[String, String]] = {
    db.withSafeConnection({ implicit c =>
      SQL"""SELECT  * FROM  advert_metadata""".as(metadataParser.*)
    }).right.flatMap { list => Right(list.toMap) }
  }

  private def addAdvertImpl(advert: VehicleAdvert): Either[String, VehicleAdvert] = {
    db.withSafeConnection({ implicit c =>
      val firstRegistrationFormatted: String = if(advert.firstRegistration.isEmpty) "" else advert.firstRegistration.get.toString(DateTimeFormatters.vehicleAdvert)
      val mileageFormatted: Int = if(advert.mileage.isEmpty) -1 else advert.mileage.get
        SQL"""
            INSERT  INTO  advert_vehicle  (id, title, price, isNew, mileage, firstRegistration)
            VALUES                        (${advert.id}, ${advert.title}, ${advert.price}, ${advert.isNew}, $mileageFormatted, $firstRegistrationFormatted)
         """.executeInsert(scalar[Int].singleOpt)
    }).right.flatMap { result =>
      if(result.getOrElse(-1) > 0)  addMetadata(advert.id, advert.metadata).right.flatMap { _ => Right(advert) }
      else                          Left(s"Could not insert id: ${advert.id}")
    }
  }

  private def addMetadata(id: String, metadata: Map[String, String]): Either[String, Map[String, String]] = {
    db.withSafeConnection({ implicit c =>
      metadata.map { case (key, value) =>
        SQL"""
            INSERT  INTO  advert_metadata (id, key, value)
            VALUES                        ($id, $key, $value)
         """.executeInsert(scalar[Int].singleOpt)
      }.toList
    }).right.flatMap { list =>
      if(list.exists(x=>x.isEmpty)) Left(s"Could not insert metadata for id: $id")
      else                          Right(metadata)
    }
  }

  private def keys: Either[String, List[String]] = {
    db.withSafeConnection({ implicit c =>
      SQL"""SELECT  id FROM  advert_metadata""".as(str("id").*)
    })
  }

  private def metadataParser: RowParser[(String, String)] = {
    str("key") ~ str("value") map {
      case key ~ value => key -> value
    }
  }

  private def defaultParser: RowParser[VehicleAdvert] = {
    str("id") ~ str("title") ~ int("price") ~ bool("isNew") ~ int("mileage") ~ str("firstRegistration") map {
      case id ~ title ~ price ~ isNew ~ db_mileage ~ db_firstRegistration => {
        val mileage: Option[Int] = if(db_mileage == -1) None  else Some(db_mileage)
        val firstRegistration: Option[DateTime] = if(db_firstRegistration.isEmpty) None  else Some(DateTimeFormatters.vehicleAdvert.parseDateTime(db_firstRegistration))
        VehicleAdvert(id, title, price, isNew, Map.empty, mileage, firstRegistration)
      }
    }
  }
}