Spring Boot Coding Dojo
---

Fork of [marcosbarbero/coding-dojo-spring-boot](https://github.com/marcosbarbero/coding-dojo-spring-boot).

Simple application that requests its data from [OpenWeather](https://openweathermap.org/) and stores the result in a database.

# Architecture

## Application Configuration

[Application Properties](https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html) and its variants have been chosen as they allow easy configuration from within Spring Boot itself, as well as Docker containers or Kubernetes.  As opposed to Environment variables, which works within Kubernetes as well, but is more cumbersome to use and implement.

## Database

## Engine

[Postgres](https://www.postgresql.org) has been chosen as it's mature, highly scalable & performant.  And we're already familiar with it, otherwise there wouldn't be a compelling reason to choose it over MySql.

## Initial Configuration

The database used by this app must be created separately.  To achieve this, connect to the database with admin rights and run the equivalent of this:

```roomsql
create database owmdojo;
```

## Change Management

TBD

# TODOs

1. Fix DB
    1. ✅ Choose a suitable database
    2. ✅ Configure DB into the app
    3. Organize & fix DB code
    4. Add a DB change management tool such as [Liquibase](https://www.liquibase.org)
    5. Create & apply DB Schema
2. Make OWM API work
    1. Add hardcoded OWM API Key
    2. Anything else?
    3. Run a request and see some results
3. ✅ Make the current app & tests work
4. Add deployment and scalability
    1. Choose between Docker-Compose and Kubernetes
    2. Find a test lab
    4. Apply solution
5. Add configurability based on the deployment solution
6. Add automated tests
    1. Unit tests
    2. ✅ System tests - Python
7. Review 1

# Resources

- [1] [Kubernetes ConfigMap via environment variables](https://dzone.com/articles/configuring-java-apps-with-kubernetes-configmaps-a)
- [2] [Kubernetes ConfigMap via properties files](https://dzone.com/articles/inject-kubernetes-configmap-values-with-java-ee-and-wildfly)
- [3] [Liquibase vs Flyway](https://medium.com/@ruxijitianu/database-version-control-liquibase-versus-flyway-9872d43ee5a4)
- [4] [Flyway vs Liquibase](https://dzone.com/articles/flyway-vs-liquibase)
- [5] [Liquibase](https://www.liquibase.org/get-started/how-liquibase-works)
- [6] [OpenWeatherMap API](https://openweathermap.org/api)
- [7] [Postgres vs MySQL](https://www.xplenty.com/blog/postgresql-vs-mysql-which-one-is-better-for-your-use-case/)