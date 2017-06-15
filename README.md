# Bursting Topics and Fake News on Twitter

Open `src/main/resources/application.properties` to change database credentials.
To run this application type:
```
$ mvn spring-boot:run
```

To add a demo user
```
$ curl 'localhost:8080/demo/add?name=First&email=someemail@someemailprovider.com'
```
The reply should by
```
Saved
```

To get all users
```
$ curl 'localhost:8080/demo/all'
```

The reply should be
```
[{"timespanId":1,"name":"First","email":"someemail@someemailprovider.com"}]
```