plugins {
    id("invui.common-conventions")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

java {
    withJavadocJar()
}

tasks.getByName<Javadoc>("javadoc") {
    exclude("**/internal/**")
}