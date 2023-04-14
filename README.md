# Pure Functional HTTP APIs

Converting from akka-http & slick (impure) to http4s & doobie (pure)

From the Pure Functional HTTP APIs in Scala book:
https://leanpub.com/pfhais/read

## Build
### Impure Service
```
cd impure
sbt run
```

### Pure Service
```
cd pure
sbt run
```

### Usage

Create product
```
curl -d '{"id": "43992e94-8007-11eb-9439-0242ac130002", "names": [{"name":"A non empty string", "lang": "en"}]}' -H 'Content-Type: application/json' localhost:49152/products
```

Update product
```
curl -d '{"id": "43992e94-8007-11eb-9439-0242ac130002", "names": [{"name":"A non empty string", "lang": "es"}]}' -H 'Content-Type: application/json' -X PUT localhost:49152/product/43992e94-8007-11eb-9439-0242ac130002
```

Get all products
```
curl localhost:49152/products
```

Get one product
```
curl localhost:49152/product/{ProductId}
```