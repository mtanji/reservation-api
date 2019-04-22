=============
=== NOTES ===
=============


=== Development environment ===

Development environment uses Docker to bootstrap MySql, phpMyAdmin and Memcached containers. They are configured in docker-compose.yml.


=== Application configuration ===

Most application configuration parameters are in application.properties file.
Below properties are highlighted:
spring.datasource.url=jdbc:mysql://192.168.99.101:3306/volcano_visit
- This property has the IP address where MySql database is running. And in this case, database name is "volcano_visit"
reservation.max-visitors: 200
- This property defines the maximum number of visitor allowed in the island.
reservation.memcached.ip: 192.168.99.101
- This property defines the IP address where Memcached is running.


=== Concurrent Request Handling ===

1. ReservationControllerConcurrentTest test class should be validating reservation concurrent execution. While it was not possible to finish writing that test, concurrency can be validated manually by following these steps:
1.1. Add a breakpoint in ReservationService.java on line "reservationDAO.save(reservation)" is called, line 59
1.2. In IDE, start SpringBoot application in debug mode
1.3. Save two reservation calls in Postman to http://localhost:8443/api/reservation POST. Use below json texts as body in the request
1.3.1. body1: { "arrivalDate":"2019-02-25", "departureDate":"2019-02-25", "email":"email1", "fullName":"Name 1", "numberOfPeople":21 }
1.3.2. body2: { "arrivalDate":"2019-02-25", "departureDate":"2019-02-25", "email":"email2", "fullName":"Name 2", "numberOfPeople":22 }
1.4. Send those API calls from Postman to SpringBoot application
1.5. In IDE, continue both threads executions
1.6. Verify in Postman that the response returned by one API call was successful
1.7. Verify in Postman that the response returned by the other API call failed due to simulated race condition


=== Exception Handling ===

Exceptions are handled by RestResponseEntityExceptionHandler so that the user receives a meaningful message.


=== Large Request Volume Handling ===

/api/occupancy/date/yyyy-MM-dd endpoint returns campsite occupancy, which can be updated by changing "reservation.max-visitors" property value.
This endpoint handles large requests volume due to the use of a cache service provided by Memcached.
