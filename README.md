# dynamodb-test-helper

Provides simple to integrate Embedded DynamoDB testing tools 

[![](https://jitpack.io/v/piazentin/dynamodb-test-helper.svg)](https://jitpack.io/#piazentin/dynamodb-test-helper)

For a working example see [the testing class in Kotlin Test](/src/test/kotlin/com/piazentin/dynamodb/tester/DynamoDBEmbeddedServerTest.kt)

## How to use

To start a local embedded Dynamodb server, run:

```
this.server = DynamoDBEmbeddedServer(randomLocalPort)
```

To start and initialize the server with some tables, run with a `DynamoDBAsyncClient` instance and the path to a local directory with the schemas in Json format. The server will read all file ending with `schemas.json` and use then to create local tables.
```
this.server = DynamoDBEmbeddedServer(randomLocalPort, client, schemasDir = "schemas")
```

You can get a random unused port for your tests with:

```
val randomLocalPort = ServerSocket(0).use { it.localPort }
```

Don't forget to configure your client to the endpoint to "http://localhost:$randomLocalPort".

After executing the tests, you should also close the resource with:

```
server.close()
```

## Spring Reactive

There is a Known issue with the usage of this lib and the spring reactive stack. The problem happen because the embedded dynamodb depends on Jetty and the reactive Spring defaults to Netty.

To solve the issue, create a bean that explicitly instantiate a `NettyReactiveWebServerFactory` and forces Spring into reactive mode:

```
@Configuration
open class SpringTestConfiguration {

    @Bean
    open fun nettyReactiveWebServerFactory() = NettyReactiveWebServerFactory()
}
```

