plugins {
    kotlin("jvm") version "1.8.0"
}

group = "me.santio"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("io.minio:minio:8.5.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}