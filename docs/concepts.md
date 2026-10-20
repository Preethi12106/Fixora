# Spring, Hibernate, and project concepts

These notes describe the concepts used or discussed by this educational project.

## Spring MVC

Spring MVC handles browser requests through controllers. A controller receives form data, calls a service, and selects a Thymeleaf page. The MVC flow is browser → controller → service → repository → database.

## JPA, Hibernate, and ORM

JPA is the Java persistence specification. Hibernate is the ORM provider used by Spring Data JPA here. ORM maps Java entity objects and fields to relational tables and columns. `Complaint` maps to `complaints`; `Feedback` maps to `feedbacks`.

## ID generation and timestamps

`BaseRecord` declares a `Long` primary key using `@Id` and `@GeneratedValue(strategy = GenerationType.IDENTITY)`. MySQL generates the value when a row is inserted. A JPA `@PrePersist` callback fills the creation time before the insert.

## CRUD

CRUD means Create, Read, Update, and Delete. `ComplaintService` and `ComplaintRepository` implement these operations for complaints. Feedback is created and retrieved through its service and repository.

## HQL

Hibernate Query Language uses entity names and Java properties rather than table and column names. `ComplaintRepository` contains two `@Query` methods:

- `findByStatusWithHql` searches by the `Complaint.status` property.
- `findByCategoryWithHql` searches by the `Complaint.category` property.

Both queries are HQL and run through Hibernate.

## Inheritance mapping

`BaseRecord` uses `@MappedSuperclass`. Its ID and `createdAt` fields are inherited as persistent fields by `Complaint` and `Feedback`. The superclass is not an entity and does not have its own table.

## Dependency injection

Spring creates and manages controllers, services, repositories, and other beans. Constructor injection passes dependencies into a class, making those dependencies clear and allowing tests to replace them with mocks.

## BeanFactory and ApplicationContext

`BeanFactory` is Spring's basic container for creating and managing beans. `ApplicationContext` builds on it and adds features such as application events, message resolution, and integration with Spring facilities. Spring Boot initializes and uses an `ApplicationContext` when the application starts; this project does not create a second container manually.

## SessionFactory, Session, and Transaction

In native Hibernate:

- A `SessionFactory` is a heavyweight factory that creates Hibernate `Session` objects.
- A `Session` represents a unit of interaction with the database and provides persistence operations.
- A `Transaction` groups database work so it can commit as a unit or roll back on failure.

Spring Data JPA and Spring's transaction support manage these Hibernate resources for this application. The application does not manually open sessions for ordinary CRUD.

## AOP

Aspect-oriented programming separates cross-cutting behavior from business code. `ServiceLoggingAspect` uses Spring AOP around service methods to log method names and elapsed time. It deliberately does not log method arguments or complaint descriptions.

## REST and SOAP

REST is an architectural style commonly used with HTTP resources and JSON. This project exposes REST endpoints such as `/api/complaints`. SOAP is a separate XML-based messaging protocol with a formal service contract, commonly described using WSDL. This project uses REST and does not implement SOAP.

## Monolith and microservices

A monolith is one deployable application containing related features. The main complaint site and API are a small monolith. A microservice is a separately runnable service with its own process and API. The Notification Service is a minimal second Spring Boot application on port `8081`.

## Spring Cloud

Spring Cloud provides optional tools for distributed systems, including service discovery, centralized configuration, and API gateway integration. These are useful in larger systems but intentionally not added to this simple local project.

## Maven, Git, GitHub, and DevOps

- **Maven** manages Java dependencies and builds each Spring Boot application. The included Maven Wrapper runs Maven without a global Maven installation.
- **Git** records source changes as commits.
- **GitHub** hosts a Git repository for sharing and collaboration.
- **DevOps** is a set of practices that connects software development and delivery operations, often using automation for building, testing, and deploying. This repository demonstrates local Maven builds and Git-based version control; it does not configure a deployment pipeline.
