package com.piazentin.dynamodb.tester

import com.almworks.sqlite4java.SQLite
import com.almworks.sqlite4java.SQLiteException
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import java.io.File
import java.nio.file.Paths

class DynamoDBEmbeddedServer(val port: Int,
                             client: DynamoDbAsyncClient? = null,
                             schemasDir: String? = null) : AutoCloseable {


    private val server: DynamoDBProxyServer

    init {

        SQLiteHelper.loadLibrary()
        server = ServerRunner.createServerFromCommandLineArgs(arrayOf("-inMemory", "-port", port.toString()))
        server.start()

        val schemas = schemasDir?.toFile()
        if (client != null && schemas != null && schemas.isDirectory) {

            createTables(client, schemas)
        }
    }

    override fun close() = server.stop()

    private fun String.toFile() = File(Thread.currentThread().contextClassLoader.getResource(this).toURI())

    private fun createTables(client: DynamoDbAsyncClient, schemas: File) {

        val createTableRequests = schemas.walk()
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
