import com.google.inject.{AbstractModule, TypeLiteral}
import com.google.inject.name.Names
import models.VehicleAdvert
import repositories.{AdvertRepository, InMemoryAdvertRepository}
import sorters.{AdvertSorter, VehicleAdvertSorter}
import utils.TimedCache
import validators.{AdvertServiceValidator, VehicleAdvertServiceValidator}

class ConfigModule extends AbstractModule {
  def configure() = {
    bind(classOf[AdvertServiceValidator]).annotatedWith(Names.named("vehicle_advert_validator")).to(classOf[VehicleAdvertServiceValidator])
    bind(new TypeLiteral[AdvertSorter[VehicleAdvert]]{}).annotatedWith(Names.named("vehicle_advert_sorter")).to(classOf[VehicleAdvertSorter])
    bind(classOf[AdvertRepository]).annotatedWith(Names.named("vehicle_advert_repository")).toInstance(new InMemoryAdvertRepository(TimedCache.apply[String, VehicleAdvert]()))
  }
}
