# Pure Functional HTTP APIs

Converting from akka-http & slick (impure) to http4s & doobie (pure)

From the Pure Functional HTTP APIs in Scala book:
https://leanpub.com/pfhais/read

If you would like to do the refactoring step then you can copy the impure folder to a new directory and refactor
from there, using the `pure` folder as a reference

## Build
### Impure Service
```
sbt impure/run
```

### Pure Service
```
sbt pure/run
```

### Usage

Start PostgreSQL
```
docker-compose up -d
```

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