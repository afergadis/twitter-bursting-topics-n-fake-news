# Bursting Topics and Fake News on Twitter

Open `src/main/resources/application.properties` to change database credentials.

To run this application type:
```
$ mvn spring-boot:run
```

To get trends that have a volume increase above a percent value (eg 200%).
```
$ curl localhost:8080/bursting/200
```

To get a trend by its name (eg #UbiE3). Please take care of the [URL Encoding](https://www.w3schools.com/tags/ref_urlencode.asp).
```
$ curl localhost:8080/name/%23UbiE3
```
This will return a json with all the instances of the name in database along with
its volume and percent change (burst).
