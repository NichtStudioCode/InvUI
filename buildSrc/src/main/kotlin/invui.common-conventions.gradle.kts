import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev")
}

val libs = the<LibrariesForLibs>()

group = "xyz.xenondevs.invui"
version = "2.0.0-alpha.19"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper.get())
    implementation(libs.jetbrains.annotations)
    implementation(libs.jspecify)
    
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platformLauncher)
    testImplementation(libs.mockbukkit)
    testImplementation(libs.logback.classic)
    testImplementation(libs.paper.api)
}

java {
    withSourcesJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

paperweight {
    addServerDependencyTo.add(configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME))
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