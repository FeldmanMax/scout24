Hello Scout24

This is a demo application for adding a new advert (vehicle) to the system.

You are been given the following RESTfull API:
    
    # Get All adverts
    GET     /api/adverts/vehicle            @controllers.VehicleAdvertsController.index(sort_by: String)
    
    # Get advert by id
    GET     /api/adverts/vehicle/:id        @controllers.VehicleAdvertsController.advertById(id: String)
    
    # Add new advert
    POST    /api/adverts/vehicle/add        @controllers.VehicleAdvertsController.add()
    
    # Update
    PUT     /api/adverts/vehicle/:id        @controllers.VehicleAdvertsController.update(id: String)
    
    # Delete an advert
    DELETE  /api/adverts/vehicle/:id        @controllers.VehicleAdvertsController.deleteAdvertById(id: String)

Request:
    
    Add new advert
    
    {
    	"advert": {
    		"id": "",
    		"title": "Mazeratti",
    		"price": 12,
            "isNew": false,
            "metadata": {
            	"fuel_type": "Gasoline"
            },
            "mileage": 100,
            "firstRegistration": "2010-12-10"
    	}
    }
    
Response:
    
    {
        "success": "true",
        "data": {
            "id": "4e7f7889-8c28-483e-b75a-3434eadecf70",
            "title": "Mazeratti_1",
            "price": 12,
            "metadata": {
                "fuel_type": "Gasoline"
            },
            "isNew": false,
            "mileage": 100,
            "firstRegistration": "2010-12-10"
        }
    }
    
    The next requests you would be able to executed with the id (hash) which was returned


Assumptions
    1.  I attached Guava in memory repository which is a persistent (temporary) cache
    2.  Sorting of the get all vehicles at the moment is been done for 2 additional fields (id by default):
        a.  title
        b.  price
        
Beta Version
    1.  I began to write a second repository - SqlAdvertRepository
        I decided to use H2Database because it's the easiest one to change. 
        The only thing that will change is the connection string.
        I am on the early stages of beginning testing the component.
        When this class is going to be finished and you would like to use it the please go to ConfigModule
        and change the named "vehicle_advert_repository" to the required class