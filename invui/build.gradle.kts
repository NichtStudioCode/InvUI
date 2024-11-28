import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.paperweight

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.paperweight)
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    implementation(libs.jetbrains.annotations)
    testImplementation(libs.junit.jupiter)
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

// remove "dev" classifier set by paperweight-userdev
afterEvaluate {
    tasks.getByName<Jar>("jar") {
        archiveClassifier = ""
    }
}