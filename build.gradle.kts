plugins {
    kotlin("jvm") version "1.3.21"
    `java-library`
    maven
}

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
}
