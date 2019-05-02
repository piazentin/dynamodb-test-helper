package com.piazentin.dynamodb.tester

import io.kotlintest.Spec
import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.BehaviorSpec
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest
import java.net.ServerSocket
import java.net.URI
import java.util.*

class DynamoDBEmbeddedServerTest : BehaviorSpec() {

    private lateinit var server: DynamoDBEmbeddedServer

    private lateinit var client: DynamoDbAsyncClient

    override fun beforeSpec(spec: Spec) {

        val randomLocalPort = findRandomFreePort()
        val credentials = StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"))
        this.client = DynamoDbAsyncClient.builder()
                .region(Region.of("us-east-1"))
                .endpointOverride(URI.create("http://localhost:$randomLocalPort"))
                .credentialsProvider(credentials)
                .build()
        this.server = DynamoDBEmbeddedServer(randomLocalPort, client, schemasDir = "schemas")
    }

    private fun findRandomFreePort() = ServerSocket(0).use { it.localPort }

    override fun afterSpec(spec: Spec) {

        this.server.close()
    }

    init {

        Given("a dynamodb put request for an entity") {
            val item = mapOf<String, AttributeValue>(
                    "id" to AttributeValue.builder().s(UUID.randomUUID().toString()).build(),
                    "attr1" to AttributeValue.builder().s("value1").build(),
                    "attr2" to AttributeValue.builder().n("100.1").build()
            )
            val putItemRequest = PutItemRequest.builder().tableName("testTable").item(item).build()

            When("the entity is saved") {
                client.putItem(putItemRequest).get()

                Then("I should be able to retrieve the entity from dynamo") {
                    val getItemRequest = GetItemRequest.builder()
                            .tableName("testTable")
                            .key(mapOf("id" to item["id"]))
                            .build()
                    val response = client.getItem(getItemRequest).get().item()
                    response["id"]?.s() shouldBe item["id"]?.s()
                    response["attr1"]?.s() shouldBe item["attr1"]?.s()
                    response["attr2"]?.n() shouldBe item["attr2"]?.n()
                }
            }
        }
    }
}
