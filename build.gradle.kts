plugins {
    kotlin("jvm") version "1.3.21"
    `java-library`
    maven
}

repositories {
    jcenter()
    mavenCentral()
    // Repository for DynamoDBLocal dependency
    maven(url="https://repository.mulesoft.org/nexus/content/repositories/public/")
}

group = "com.piazentin"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {
    implementation(kotlin("stdlib"))

    implementation("software.amazon.awssdk:dynamodb:2.5.1")
    implementation("com.amazonaws:DynamoDBLocal:1.11.119")
    implementation("com.almworks.sqlite4java:sqlite4java:1.0.392")
    implementation("com.amazonaws:aws-java-sdk-dynamodb:1.+")
    implementation("org.eclipse.jetty:jetty-server:8.2.0.+")
    implementation("commons-cli:commons-cli:1.3")
    implementation("org.antlr:antlr4-runtime:4.7.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    implementation("org.apache.logging.log4j:log4j-jcl:2.11.0")

    testImplementation("io.kotlintest:kotlintest-runner-junit5:3.3.2")
}

val test by tasks.getting(Test::class) {
    useJUnit()
}