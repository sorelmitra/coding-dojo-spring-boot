Spring Boot Coding Dojo
---

Fork of [marcosbarbero/coding-dojo-spring-boot](https://github.com/marcosbarbero/coding-dojo-spring-boot).

Simple application that requests its data from [OpenWeather](https://openweathermap.org/) and stores the result in a database.



# Usage

## Initial Configuration

The database used by this app must be created separately.  To achieve this, connect to the database with admin rights and run the equivalent of this:

```roomsql
create database owmdojo;
```

The app should work with major SQL DB engines available today.  The only one tested was Postgres.

## Run App Locally

To run the app in your local environment, you need to first create a file like this:

`application-dev.yaml`
```yaml
openWeatherMap:
  appKey:
    value: your-owm-api-key
spring:
   datasource:
      url: DB URL of choice
      username: ...
      password: ...
```

Place it somewhere on your local disk.  Do **not** add it to GIT, as it contains your OWM api key.
Then add the following parameter to the application (not to Java itself) when running it:

```shell
--spring.config.additional-location=application-dev.yaml
```

This will overwrite just the `appKey` value from the app configuration, as well as the DB URL and user.  You can add other values in your local file if you desire, take a look at `src/main/resources/application.yaml` for more values to config.

## Run Autotests

Autotests are automatic system tests, in which the app is running in a real environment, and a separate tester app is making requests to it.

In this case the tester app is based on [PyTest](https://docs.pytest.org/en/latest/) and on a helper Python module that I developed and is available [here](https://github.com/sorelmitra/learn/tree/master/verifit).  The module helps with writing quick tests of the form _prepare input data_ ➡ _call something_ ➡ _obtain a file with results_ ➡ _compare the obtained file with an expected one_.  It provides boilerplate code for easy specifying of the input and expected output, as well as for running arbitrary Shell commands.

To run autotests for this app, install [Verifit](https://github.com/sorelmitra/learn/tree/master/verifit) in your Python path and then CD to the root of the source tree for this app and run:

```shell
pytest .
```



# Architecture

## Application Configuration

[Application Properties](https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html) and its variants have been chosen as they allow easy configuration from within Spring Boot itself, as well as Docker containers or Kubernetes.  As opposed to Environment variables, which works within Kubernetes as well, but is more cumbersome to use and implement.

## Database

## Engine

[Postgres](https://www.postgresql.org) has been chosen as it's mature, highly scalable & performant.  And we're already familiar with it, otherwise there wouldn't be a compelling reason to choose it over MySql.

## Change Management

TBD



# TODOs

1. ✅ Fix DB
    1. ✅ Choose a suitable database
    2. ✅ Configure DB into the app
    3. ✅ Organize & fix DB code
    4. ✅ Add a DB change management tool - Liquibase
    5. ✅ Create & apply DB Schema
2. ✅ Make OWM API work
3. ✅ Make the current app & tests work
4. ✅ Add configurability based on application properties file
5. Add automated tests
   1. ✅ Unit tests
   2. ✅ Autotests - Python
   3. Stress Testing - K6
6. Add multi-threading
7. Add deployment and scalability
    1. Choose between Docker-Compose and Kubernetes
    2. Find a test lab
    3. Apply solution
8. Add HA
9. Review 1
10. Complete Architecture and Usage sections



# Resources

## General

- [1] [OpenWeatherMap API](https://openweathermap.org/api)

## Configuration

- [2] [Kubernetes ConfigMap via environment variables](https://dzone.com/articles/configuring-java-apps-with-kubernetes-configmaps-a)
- [3] [Kubernetes ConfigMap via properties files](https://dzone.com/articles/inject-kubernetes-configmap-values-with-java-ee-and-wildfly)

## Database Engines

- [4] [Postgres vs MySQL](https://www.xplenty.com/blog/postgresql-vs-mysql-which-one-is-better-for-your-use-case/)

## Database Change Management

- [5] [Liquibase vs Flyway](https://medium.com/@ruxijitianu/database-version-control-liquibase-versus-flyway-9872d43ee5a4)
- [6] [Flyway vs Liquibase](https://dzone.com/articles/flyway-vs-liquibase)
- [7] [Liquibase](https://www.liquibase.org/get-started/how-liquibase-works)
