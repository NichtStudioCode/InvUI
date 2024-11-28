import org.gradle.kotlin.dsl.libs

plugins {
    id("invui.common-conventions")
    alias(libs.plugins.kotlin)
}

dependencies {
    api(libs.kotlin.stdlib)
    api(project(":invui"))
}