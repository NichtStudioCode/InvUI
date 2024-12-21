group = "xyz.xenondevs.invui"
version = "2.0.0-alpha.6"

plugins {
    `java-library`
    `maven-publish`
    id("io.papermc.paperweight.userdev")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/maven-public/")
}

dependencies {
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT")
    implementation("org.jetbrains:annotations:26.0.1")
    implementation("org.jspecify:jspecify:1.0.0")
    
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.20.0")
    configurations.getByName("testImplementation").exclude("io.papermc.paper", "paper-server")
    testImplementation("ch.qos.logback:logback-classic:1.5.13")
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