# CloudEvents Player

It is an application that can send and receive CloudEvents v1. Its purpose is to be deployed on a
KNative Eventing environment so that users can monitor received events in the Activity section and
also send events of the desired type to see if it is being forwarded back to the application through
the broker.

## Build and run the application

It is a Quarkus application with a React frontend. In order to build the application use any of the
following alternatives:

### JVM Build

Build

```shell script
mvn clean package
```

Run

```shell script
$ java -jar target/cloudevent-player-1.0-SNAPSHOT-runner.jar
...
2019-10-24 11:06:33,880 INFO  [io.quarkus] (main) cloudevent-player 1.0-SNAPSHOT (running on Quarkus 0.26.1) started in 1.875s. Listening on: http://0.0.0.0:8080
2019-10-24 11:06:33,881 INFO  [io.quarkus] (main) Profile prod activated.
2019-10-24 11:06:33,882 INFO  [io.quarkus] (main) Installed features: [cdi, hibernate-validator, rest-client, resteasy, resteasy-jackson, servlet, undertow-websockets, vertx]
```

### Quarkus dev mode

```shell script
$ mvn clean compile quarkus:dev
...
Listening for transport dt_socket at address: 5005
...
2019-10-24 11:04:53,877 INFO  [io.quarkus] (main) Quarkus 0.26.1 started in 2.139s. Listening on: http://0.0.0.0:8080
2019-10-24 11:04:53,877 INFO  [io.quarkus] (main) Profile dev activated. Live Coding activated.
2019-10-24 11:04:53,877 INFO  [io.quarkus] (main) Installed features: [cdi, hibernate-validator, rest-client, resteasy, resteasy-jackson, servlet, undertow-websockets, vertx]
```

### Native build

Build

```shell script
mvn clean package -Pnative
```

Run

```shell script
$ ./target/cloudevent-player-1.0-SNAPSHOT-runner
...
2019-10-24 11:15:12,990 INFO  [io.quarkus] (main) cloudevent-player 1.0-SNAPSHOT (running on Quarkus 0.26.1) started in 0.048s. Listening on: http://0.0.0.0:8080
2019-10-24 11:15:12,990 INFO  [io.quarkus] (main) Profile prod activated.
2019-10-24 11:15:12,990 INFO  [io.quarkus] (main) Installed features: [cdi, hibernate-validator, rest-client, resteasy, resteasy-jackson, servlet, undertow-websockets, vertx]
```

## Use the application locally

By default, the application will send events to itself to ensure that both send/receive
work well and send valid CloudEvents.

If needed, the broker endpoint can be configured in the application.properties file.
Create a ./config/application.properties file with the custom endpoint as in the example:

```properties
brokerUrl/mp-rest/url=http://endpoint.example.com
```

You can send a message from inside the application by filling in the form and the activity will show the sent
event and the received event (from the loopback)

You can also simulate the broker with the `curl`:

```shell script
$ curl -v http://localhost:8080 \
   -H "Content-Type: application/json" \
   -H "Ce-Id: foo-1" \
  -H "Ce-Specversion: 1.0" \
  -H "Ce-Type: dev.example.events" \
  -H "Ce-Source: curl-source" \
  -d '{"msg":"Hello team!"}'

> POST / HTTP/1.1
> User-Agent: curl/7.35.0
> Host: localhost:8080
> Accept: */*
> Ce-Id: foo-1
> Ce-Specversion: 1.0
> Ce-Type: dev.example.events
> Ce-Source: curl-source
> Content-Type: application/json
> Content-Length: 21
>
< HTTP/1.1 202 Accepted
< Content-Length: 0
< Date: Thu, 24 Oct 2019 08:27:06 GMT
```

## Build the container image

### JVM version

```shell script
docker build -t ruromero/cloudevents-player-jdk8:latest -f src/main/docker/Dockerfile.jvm .
```

### Native version

```shell script
docker build -t ruromero/cloudevents-player:latest -f src/main/docker/Dockerfile.native .
```

## Running CloudEvents Player on Kubernetes

### Requirements

* Knative serving
* Knative eventing

### Deploy the application

Use [deploy_native.yaml](./src/main/knative/deploy_native.yaml) to create the resources

```shell script
$ kubectl apply -n myproject -f src/main/knative/deploy_native.yaml
service.serving.knative.dev/cloudevents-player created
trigger.eventing.knative.dev/cloudevents-player created
```

The following resources are created:

* KNative Service: Pointing to the image and mounting the volume from the configMap
* Trigger: To subscribe to any message in the broker
