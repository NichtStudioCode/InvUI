plugins {
    id("invui.common-conventions")
    kotlin("jvm") version "2.1.0"
}

dependencies {
    api("org.jetbrains.kotlin:kotlin-stdlib:2.1.0")
    api(project(":invui"))
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
        }
    }
}