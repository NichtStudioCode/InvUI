plugins {
    id("invui.common-conventions")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.dokka)
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
    testImplementation(libs.kotlin.test.junit)
}

kotlin {
    compilerOptions {
        optIn.addAll(
            "kotlin.experimental.ExperimentalTypeInference",
            "kotlin.contracts.ExperimentalContracts",
        )
    }
}

val dokkaGenerateHtmlIntoJavadoc by tasks.registering(Jar::class) {
    group = "dokka"
    archiveClassifier = "javadoc"
    from(tasks.dokkaGeneratePublicationHtml.flatMap { it.outputDirectory })
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            artifact(dokkaGenerateHtmlIntoJavadoc)
        }
    }
}