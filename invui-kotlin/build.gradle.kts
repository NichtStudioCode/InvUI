plugins {
    id("invui.common-conventions")
    alias(libs.plugins.kotlin)
}

repositories {
    mavenLocal {
        content {
            @Suppress("UnstableApiUsage")
            includeGroupAndSubgroups("xyz.xenondevs")
        }
    }
    maven("https://repo.xenondevs.xyz/releases/")
}

dependencies {
    api(project(":invui"))
    api(libs.kotlin.stdlib)
    api(libs.commons.provider)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}