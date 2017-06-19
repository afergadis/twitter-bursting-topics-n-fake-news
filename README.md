# Bursting Topics and Fake News on Twitter

Open `src/main/resources/application.properties` to change database credentials.

To run this application type:
```
$ mvn spring-boot:run
```

To get trends that have a volume increase above a percent value (eg 200%).
```
$ curl 'localhost:8080/trends/bursting/200'
```

You can also specify optional query parameters `from` and `to` in order to define
the starting and ending time span. You can give either `from` or `to` or both of them.
```
$ curl 'localhost:8080/trends/bursting/200?from=10'
$ curl 'localhost:8080/trends/bursting/200?to=10'
$ curl 'localhost:8080/trends/bursting/200from=5?to=15'
```

You can also use negative value for `from`. For example, setting `from=-2` means from 
minus 2 timespans to current. Negative values to `to` are ignored.
```
$ curl 'localhost:8080/trends/bursting/200?from=-2'
```

To get a trend by its name (eg #UbiE3). Please take care of the [URL Encoding](https://www.w3schools.com/tags/ref_urlencode.asp).
```
$ curl 'localhost:8080/trends/name/%23UbiE3'
```
This will return a json with all the instances of the name in database along with
its volume and percent change (burst).
