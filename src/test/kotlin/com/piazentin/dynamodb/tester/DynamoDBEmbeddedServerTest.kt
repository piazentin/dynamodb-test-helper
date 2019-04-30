package com.piazentin.dynamodb.tester

import io.kotlintest.TestCase
import io.kotlintest.TestResult
import io.kotlintest.specs.BehaviorSpec

class DynamoDBEmbeddedServerTest : BehaviorSpec() {

    private lateinit var dynamoDBEmbeddedServer: DynamoDBEmbeddedServer

    override fun beforeTest(testCase: TestCase) {

        this.dynamoDBEmbeddedServer = DynamoDBEmbeddedServer("18018")
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {

        this.dynamoDBEmbeddedServer.close()
    }

    init {

        Given("a dynamodb put request for an entity") {

            When("the entity is saved") {

                Then("I should be able to retrieve the entity from dynamo") {


                }
            }
        }
    }
}