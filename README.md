# Spring Boot Demo Project

This is a simple Spring Boot project with:
- Spring Web (REST APIs)
- Spring Data JPA
- H2 Database

## Requirements
- Java 21
- Maven

## How to Run
1. Clone this repository
2. Open in VS Code
3. Run `mvn spring-boot:run` in the terminal
4. Open http://localhost:8080 in your browser

## Features
- REST API endpoint at "/"
- H2 Database Console at http://localhost:8080/h2-console
  - JDBC URL: jdbc:h2:mem:testdb
  - Username: sa
  - Password: (leave empty)
