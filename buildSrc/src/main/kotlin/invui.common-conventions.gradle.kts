import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev")
}

val libs = the<LibrariesForLibs>()

group = "xyz.xenondevs.invui"
version = "2.0.0-alpha.6"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/maven-public/")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.api.get())
    implementation(libs.jetbrains.annotations)
    implementation(libs.jspecify)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platformLauncher)
    testImplementation(libs.mockbukkit)
    configurations.getByName("testImplementation").exclude("io.papermc.paper", "paper-server")
    testImplementation(libs.logback.classic)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}

publishing {
    repositories {
        maven {
            credentials {
                name = "xenondevs"
                url = uri { "https://repo.xenondevs.xyz/releases/" }
                credentials(PasswordCredentials::class)
            }
        }
    }
}

// remove "dev" classifier set by paperweight-userdev
afterEvaluate {
    tasks.getByName<Jar>("jar") {
        archiveClassifier = ""
    }
}