plugins {
    id("invui.common-conventions")
    kotlin("jvm") version "2.1.0"
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
    api("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    api(project(":invui"))
    api("xyz.xenondevs.commons:commons-provider:1.23")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}