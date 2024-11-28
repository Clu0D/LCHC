plugins {
    kotlin("jvm") version "1.9.23"
}

group = "prod.prog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    // graphviz
    implementation("org.graalvm.js:js:24.1.1")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation ("guru.nidi:graphviz-kotlin:0.18.1")

    // some compiler stuff
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.20")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable:1.6.20")
    implementation("com.bennyhuo.kotlin:code-analyzer:1.1")

    // lets-plot
    implementation("org.jetbrains.kotlinx:kandy-lets-plot:0.7.1")

    // prettier tests
    testImplementation("io.kotest:kotest-runner-junit5:5.8.1")
    testImplementation("io.kotest:kotest-assertions-core:5.8.1")
    testImplementation("io.kotest:kotest-property:5.8.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}