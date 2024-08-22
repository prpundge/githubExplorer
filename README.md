GitHub Repository Explorer API
======================

A RESTful API for exploring GitHub repositories.

Overview
-----------

This API provides a simple way to retrieve information about GitHub repositories. It handles exceptions and returns error responses in a standardized format.

This code uses the following technologies:

* Java 21
* Spring Boot 3
* Spring Data JPA
* H2 in-memory database

Endpoints
------------

* `GET /repositories/{owner}/{repo}`: Retrieve information about a specific repository.

Error Handling
-----------------

The API uses a global exception handler to catch and handle unexpected errors. Error responses are returned in the following format:

```json
{
  "statusCode": 500,
  "message": "An unexpected error occurred."
}
```
Curl Command
-----------------
```bash
curl --silent --location --request GET 'http://localhost:8080/repositories/black-forest-labs/flux'
```
```bash
curl --silent --location --request GET 'http://localhost:8080/repositories/no_one/Angular_Practice'
```