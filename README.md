Spring Boot Coding Dojo
---

Fork of [marcosbarbero/coding-dojo-spring-boot](https://github.com/marcosbarbero/coding-dojo-spring-boot).

Simple application that requests its data from [OpenWeather](https://openweathermap.org/) and stores the result in a database.



# Usage

## Run in Local Environment

### Initial Configuration

The database used by this app must be created separately.  To achieve this, connect to the database with admin rights and run the equivalent of this:

```shell
create database owmdojo;
```

The app should work with major SQL DB engines available today.  The only one tested was Postgres.

### Run App Locally

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

## Build

To build everything as a Docker container:

```shell
mvn clean install docker:build docker:push
```

## Run in Kubernetes

### Configure

To install or upgrade the release, make sure you have [Helm 3](https://helm.sh) installed

**Note for GKE**: This has been configured for Google Kubernetes Engine, because that's what I had available, not because I like the way it's documented or organized!

Then cd to the root of the repo and run:

```shell
kubectl create -f src/main/postgres/kube/postgres-storage.yaml
```

This will create a PVC for the postgres database the microservice is using.

**For GKE**: Now change the reclaim policy to retain...

```shell
kubectl get pv

kubectl patch pv <pv-name> -p '{"spec":{"persistentVolumeReclaimPolicy":"Retain"}}'
```

**Note for GKE**: in order to make the services work externally, you have to open up a firewall rule!  Like this:

```shell
# For the app
gcloud compute firewall-rules create owm-dojo-node-port --allow tcp:30770

# For postgres
gcloud compute firewall-rules create postgres-node-port --allow tcp:30432
```

See [the guide](https://cloud.google.com/kubernetes-engine/docs/how-to/exposing-apps) for more details.  And also watch how they say nothing about it [in the docs](https://cloud.google.com/kubernetes-engine/docs/concepts/service)!

### Install Postgres

CD to the root of the source repo and run this:

```shell
helm upgrade --install owm-dojo-postgres src/main/postgres/helm
```

### Create the Database

Now connect to Postgres and create the database.  Because you've changed the retain policy for the PV above, the database will be retained until you actually kill the PV.

To do this, first take note of a node's External IP (doesn't matter which node):

```shell
kubectl get nodes --output wide
```

Then use that IP to connect to Postgres:

```shell
psql -h <Node External IP> -p 30432 -U postgres --password
```

And create your database:

```shell
create database owmdojo;
```

### Install the Release

To install the microservice, first make sure you have your OWM Api Key available then type:

```shell
helm upgrade --install owm-dojo src/main/helm --set owm.apiKey=<your api key>
```

### Access the Service

The same as [Create the Database](#Create_the_Database) above, get a node's External IP then say:

```shell
curl -X POST http://<Node External IP>:30770/weather?city=X
```

### Delete the Release

To delete the release:

```shell
helm delete owm-dojo
```

### Delete Postgres

Run this:

```shell
helm delete owm-dojo-postgres
```

## Run as Docker Containers

Kubernetes of course is preferred, as it offers HA and scalability if configured properly.  However if you want to use plain Docker:

There's a `docker-compose` file which can be used to deploy and start the basic Docker container.  To use it:

```shell
OPENWEATHERMAP_APPKEY_VALUE=your-key-value docker-compose -f src/test/python/app/app-with-prerequisites.yml up
```

It will start both the database and the app while passing the api key via a Spring Boot environment property, which in this case is `OPENWEATHERMAP_APPKEY_VALUE`, which is the _upper-case-united-with-underscores_ transformation of `openWeatherMap.appKey.value` (Spring Boot magic).

To operate on individual containers:

```shell
OPENWEATHERMAP_APPKEY_VALUE=your-key-value docker-compose -f src/test/python/app/app-with-prerequisites.yml up -d owm-dojo

docker-compose -f src/test/python/app/app-with-prerequisites.yml stop owm-dojo
docker container prune -f
```

## Run Autotests

Autotests are automatic system tests, in which the app is running in a real environment, and a separate tester app is making requests to it.

In this case the tester app is based on [PyTest](https://docs.pytest.org/en/latest/) and on a helper Python module that I developed and is available [here](https://github.com/sorelmitra/learn/tree/master/verifit).  The module helps with writing quick tests of the form _prepare input data_ ➡ _call something_ ➡ _obtain a file with results_ ➡ _compare the obtained file with an expected one_.  It provides boilerplate code for easy specifying of the input and expected output, as well as for running arbitrary Shell commands.

To run autotests for this app, first install [Verifit](https://github.com/sorelmitra/learn/tree/master/verifit) in your Python path.

Then edit `src/test/python/tests/test_basic.py` and make it point to the running instance of the app by changing the `server` and `port` variables.

Finally CD to the root of the source tree for this app and run:

```shell
pytest .
```


## Run Stress Tests

For stress testing I used [K6](https://k6.io).  The test reads a city list downloaded from OWM, takes one country, and at each iteration gets the weather for one city in the list without doubling the cities.

To run it, first edit `src/test/k6/weather-test.js` and point it to the running instance of the app  by changing the `server` and `port` variables.

Then CD to the root of the repo and run:

```shell
K6_VUS=1 K6_ITERATIONS=10 k6 run --include-system-env-vars src/test/k6/weather-test.js
```

The more `K6_VUS` you pass, the more requests will be done in parallel.  `K6_ITERATIONS` must be at least as big as `K6_VUS` and if bigger, the iterations are split evenly among the VUs.

Although K6 supports various ways of passing in parameters, `VUs` and `ITERATIONs` must be passed via environment variables as the test script relies on them to do its job.



# Architecture

## Overview

This is a single microservice that obtains weather info from OWM and saves it in a local DB for later reference.

## Quality Assurance

QA has been obtained by a combination of **automated** tests:

- Unit tests that exercise the code and are ran as part of each build.
- System tests that exercise the entire app running in a real environment and check the values obtained after calling the app.
- Stress tests that put load and stress performance of the app while also doing some basic checks on the values returned, including large data sets and concurrent access.

## Application Configuration

The application is configured via its `application.yaml`.  As SpringBoot has different [ways](https://docs.spring.io/spring-boot/docs/1.0.1.RELEASE/reference/html/boot-features-external-config.html) to override those values, this is flexible enough to offer the options of:

- Loading an external yaml file that only overrides certain values.  This works well when running locally.
- Defining environment variables that are magically transformed by SpringBoot into properties based on a convention.  This works well with Docker and Kubernetes.


## Database

### Engine

[Postgres](https://www.postgresql.org) has been chosen as it's mature, highly scalable & performant.  And we're already familiar with it, otherwise there wouldn't be a compelling reason to choose it over MySql.

### Change Management

I used [Liquibase](https://www.liquibase.org/get-started/how-liquibase-works) as it's a de facto standard for managing change in databases.  There are other options, but I'm used to Liquibase and found no compelling reason to switch.

## Deployment

At a very basic level, the app is deployed using a Docker container.

For more advanced usage, Kubernetes is preferred.

## Scalability, HA, and Performance

Internally, the app makes use of Spring's async mode which uses a thread pool to serve requests.  This means a single instance of the app can serve multiple requests simultaneously.

The app also supports multiple parallel instances, as long as requests are load-balanced such as no single request reaches more than one instance.

In Kubernetes, basic load balancing is automatically provided.  Scalability is supported both manually and automatically, with the first one being configured by default in this app.

High availability is offered by a combination of Kubernetes and multiple clusters, but this is out of scope of this project and involves configuration on the Kubernetes Engine provider.


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
5. ✅ Add automated tests
   1. ✅ Unit tests
   2. ✅ System tests - Python
   3. ✅ Stress Testing - K6
6. ✅ Add multi-threading
7. ✅ Add deployment, scalability, and HA
    1. ✅ Dockerize the app
    2. ✅ Add Kubernetes
8. ✅ Complete Architecture and Usage sections
9. Data handling 
    1. ✅ Store city ID in the DB, as just country and city name are not enough to uniquely identify a place
    2. Handle Hibernate errors like "trying to update a record that has changed in the meantime"



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

## Docker Configuration

- [7] [Using Environment Vars in Compose](https://docs.docker.com/compose/environment-variables/#the-env-file)
- [8] [Exposing Env Vars from Kubernetes](https://dzone.com/articles/configuring-java-apps-with-kubernetes-configmaps-a)
