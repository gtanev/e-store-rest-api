# eStore REST API example

This is a complete, production-ready example of a **hypermedia driven RESTful web service written in Java** with Spring Boot 2, 
Spring Data REST, JPA/Hibernate, and MySQL.
The included unit and integration tests provide full code coverage and are written with JUnit 5, Mockito 2, and TestRestTemplate.

The service has two endpoints for performing CRUD operations on `product` and `order` entities, which are documented with Swagger and SpringFox.
Basic HTTP Authentication is provided via Spring Security. 

## Specification

<details>
    <summary><i>Click to expand</i></summary> 
    
<br/>  

The data model complies with the following requirements:
- A product has a `name` and a `price`.
- An order contains a `list of products`, a `customer email`, a `creation timestamp`, and a `total` amount reflecting 
the sum of the prices of all products it contains.
- A product's price can be updated, but this does not affect the pre-calculated totals of existing orders containing that product.

The API exposes endpoints for the following operations:
* Creating a new product
* Retrieving a list of all products
* Updating an existing product
* Finding a product by id
* Finding a product by name
* Making an order
* Retrieving a list of all orders
* Finding an order by id
* Deleting an order by id
* Retrieving a list of orders made within a specific time period

</details>



## Setting up and running the application locally

1. Make sure you have `JDK 1.8+` installed.
2. Make sure you have `MySQL Server 5.1+` installed and running.
3. Open `/src/main/resources/application.properties` and adjust your database connection settings as necessary.
4. Compile and run the source code in your IDE.

### Notes

* Hibernate creates the database automatically if it doesn't exist, so no DDL needs to be executed manually.
* The embedded web server is configured to run on port 8443 by default, with SLL enabled. 
A PKCS12 keystore with a self-signed certificate is provided.
* Default authentication credentials are set to `admin` and `pass` for the username and password respectively.
* To view the API documentation, you can import [swagger.json](swagger.json) into https://editor.swagger.io/ 
or navigate to `https://localhost:8443/swagger-ui.html` while the application is running.
