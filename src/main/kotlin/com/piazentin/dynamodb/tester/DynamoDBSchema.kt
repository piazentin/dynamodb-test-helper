package com.piazentin.dynamodb.tester

import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement


data class DynamoDBSchema(var tableName: String?,
                          var keySchema: List<KeySchema>?,
                          var attributeDefinitions: List<AttributeDefinition>?,
                          var provisionedThroughput: ProvisionedThroughput?,
                          var timeToLiveDescription: TimeToLiveDescription?,
                          var timeToLiveSpecification: TimeToLiveSpecification?,
                          var globalSecondaryIndexes: List<GlobalSecondaryIndex>?) {


    fun toDynamoDB() = CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(keySchema?.map { it.toDynamoDB() })
            .attributeDefinitions(attributeDefinitions?.map { it.toDynamoDB() })
            .provisionedThroughput(provisionedThroughput?.toDynamoDB())
            .globalSecondaryIndexes(globalSecondaryIndexes?.map(GlobalSecondaryIndex::toDynamoDB))
            .build()
}


data class KeySchema(var attributeName: String?,
                     var keyType: String?) {

    fun toDynamoDB() = KeySchemaElement.builder().attributeName(attributeName).keyType(keyType).build()
}


data class AttributeDefinition(var attributeName: String?,
                               var attributeType: String?) {

    fun toDynamoDB() = software.amazon.awssdk.services.dynamodb.model.AttributeDefinition
            .builder().attributeName(attributeName).attributeType(attributeType).build()
}


data class ProvisionedThroughput(var readCapacityUnits: Long?,
                                 var writeCapacityUnits: Long?) {

    fun toDynamoDB() = software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput.builder()
            .readCapacityUnits(readCapacityUnits).writeCapacityUnits(writeCapacityUnits).build()
}

data class TimeToLiveSpecification(var attributeName: String?,
                                   var enabled: Boolean?)

data class TimeToLiveDescription(var attributeName: String?,
                                 var timeToLiveStatus: String?)

data class Projection(var projectionType: String?) {

    fun toDynamoDB() = software.amazon.awssdk.services.dynamodb.model.Projection.builder()
            .projectionType(projectionType)
            .build()
}

data class GlobalSecondaryIndex(var indexName: String?,
                                var keySchema: List<KeySchema>?,
                                var projection: Projection?,
                                var provisionedThroughput: ProvisionedThroughput?) {

    fun toDynamoDB() = software.amazon.awssdk.services.dynamodb.model.GlobalSecondaryIndex.builder()
            .indexName(indexName)
            .keySchema(keySchema?.map(KeySchema::toDynamoDB))
            .provisionedThroughput(provisionedThroughput?.toDynamoDB())
            .projection(projection?.toDynamoDB())
            .build()
}
