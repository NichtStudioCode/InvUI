import org.gradle.kotlin.dsl.libs
import org.gradle.kotlin.dsl.paperweight

plugins {
    `maven-publish`
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paperweight)
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    api(libs.kotlin.stdlib)
    api(project(":invui-core"))
}

// remove "dev" classifier set by paperweight-userdev
afterEvaluate {
    tasks.getByName<Jar>("jar") {
        archiveClassifier = ""
    }
}