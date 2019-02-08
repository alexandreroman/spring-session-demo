# Spring Session Demo

This project shows how to use [Spring Session](https://spring.io/projects/spring-session)
with [Spring Boot](https://spring.io/projects/spring-boot) to replicate Servlet
[HttpSession](https://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpSession.html)
attributes across many application instances, using a Redis datastore.

The application is deployed as a WAR file: you can deploy it in any
Servlet compatible containers.

## How to use it?

Compile this project using a JDK 8:
```bash
$ ./mvnw clean package
```

You need a Redis instance to store session attributes:
```bash
$ docker run --rm --name redis -p "6379:6379/tcp" redis:5
```

You are now ready to run this app:
```bash
$ ./mvnw spring-boot:run
```

You can also deploy the WAR file located in the `target` directory
to your Servlet engine (such as Apache Tomcat or Jetty).

Hit http://localhost:1234 with your browser
(replace `1234` with the allocated dynamic port):

<img src="https://i.imgur.com/dKF9Big.png"/>

Each time you reload this page, a counter is incremented: its value
is stored as a session attribute.

You can start more application instances on your workstation:
the same session is shared across application instances.

## Deploy to Cloud Foundry

This project includes a manifest to deploy this application to
Cloud Foundry. Prior to deploying this application, you need to
create a Redis service instance named `redis`.

From now on, just push this application:
```bash
$ cf push
```

Feel free to add more application instances: Spring Session will
automatically replicate session attributes using the Redis service instance.

## Deploy to Kubernetes

This project also includes a Kubernetes descriptor, allowing you to
deploy this application with two instances and a Redis datastore instance:
```bash
$ kubectl apply -f k8s.yml
```

All objects are created within the namespace `spring-session`.
A Redis service instance is shared across application instances
running in pods.

A `LoadBalancer` object is created to access the application:
```bash
$ kubectl -n spring-session get svc
NAME                TYPE           CLUSTER-IP      EXTERNAL-IP   PORT(S)          AGE
redis               ClusterIP      10.96.43.20     <none>        6379/TCP         4m
spring-session-lb   LoadBalancer   10.101.221.81   34.95.34.12   8080:31954/TCP   4m
```

In this deployment, hit http://34.95.34.12:8080 to access the application.

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2019 [Pivotal Software, Inc.](https:/pivotal.io)

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
