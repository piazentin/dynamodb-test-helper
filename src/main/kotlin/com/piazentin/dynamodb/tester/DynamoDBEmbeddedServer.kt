package com.piazentin.dynamodb.tester

import com.almworks.sqlite4java.SQLite
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.io.File

class DynamoDBEmbeddedServer(val port: String,
                             client: DynamoDbAsyncClient? = null,
                             schemasDir: String? = null) : AutoCloseable {


    private val server: DynamoDBProxyServer

    init {

        // see https://bitbucket.org/almworks/sqlite4java/wiki/UsingWithMavens
        SQLite.setLibraryPath("sqlite-libs")
        server = ServerRunner.createServerFromCommandLineArgs(arrayOf("-inMemory", "-port", port))
        server.start()

        if (client != null && schemasDir != null && schemasDir.dirExists()) {

            createTables(client, schemasDir)
        }
    }

    override fun close() = server.stop()

    private fun String.dirExists() = File(this).let { it.exists() && it.isDirectory }

    private fun createTables(client: DynamoDbAsyncClient, schemasDirName: String) {

        val schemasDir = File(schemasDirName)
        val createTableRequests = schemasDir.walk()
                .filter { it.name.endsWith(".schema.json") }
                .map {
                    ObjectMapper()
                            .registerModule(KotlinModule())
                            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                            .readValue(it, DynamoDBSchema::class.java)
                            .toDynamoDB()
                }
                .map { client.createTable(it) }

        createTableRequests.forEach { it?.join() }
    }
}