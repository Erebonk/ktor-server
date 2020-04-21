import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    application
    kotlin("jvm") version "1.3.71"
}

val ktor_version = "1.3.2"

group = "com.ere"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.litote.kmongo:kmongo-coroutine:4.0.0")
    compile("io.ktor:ktor-server-netty:$ktor_version")
    compile("io.ktor:ktor-gson:$ktor_version")
    compile("ch.qos.logback:logback-classic:1.2.3")
    testCompile("junit", "junit", "4.12")
}

application {
    mainClassName = "Main"
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}