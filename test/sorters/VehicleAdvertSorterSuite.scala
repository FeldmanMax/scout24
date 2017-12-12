package sorters

import creators.VehicleAdvertCreator
import models.VehicleAdvert
import org.scalatest.FunSuite

class VehicleAdvertSorterSuite extends FunSuite with VehicleAdvertCreator {
  test("order by price") {
    val sorter: VehicleAdvertSorter = new VehicleAdvertSorter
    val adverts : List[VehicleAdvert] = List(getMockedVehicleAdvert("_1").copy(price = 321), getMockedVehicleAdvert("_2").copy(price = 123))
    assert(sorter.sort(adverts, "price").head.price == 123)
  }

  test("order by title") {
    val sorter: VehicleAdvertSorter = new VehicleAdvertSorter
    val adverts : List[VehicleAdvert] = List(getMockedVehicleAdvert("_1").copy(title = "b"), getMockedVehicleAdvert("_2").copy(title = "a"))
    assert(sorter.sort(adverts, "title").head.title == "a")
  }
}
