package repositories

import java.sql.{Connection, DriverManager}
import javax.inject.Singleton

import com.google.inject.ImplementedBy
import play.api.db.DB
import play.api.Play.current

@ImplementedBy(classOf[H2DB])
trait DBWrapper {
  def withConnection[A](block: Connection => A): A
  def withSafeConnection[A](block: Connection => A): Either[String, A] = {
    val dbName = "advert_vehicle"
    Class.forName("org.h2.Driver")
    val connection: Connection = DriverManager.getConnection("jdbc:h2:mem:" + dbName, "sa", "")
    try {
      Right(block(connection))
    }
    catch {
      case ex: Exception => Left(ex.getMessage)
    }
    finally {
      connection.close()
    }
  }
}

@Singleton
class H2DB extends DBWrapper {
  def withConnection[A](block: Connection => A): A = {
    DB.withConnection(block)
  }
}
