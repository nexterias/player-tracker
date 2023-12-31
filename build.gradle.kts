import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.10"
    id("io.papermc.paperweight.userdev") version "1.5.7"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "dev.nexterias.folia-plugin"
version = "1.0.0"
description = "Assist in player tracking."

repositories {
    mavenCentral()
}

dependencies {
    paperweight.foliaDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name() // We want UTF-8 for everything

        val props = mapOf(
            "name" to project.name,
            "version" to project.version,
            "description" to project.description,
            "apiVersion" to "1.20"
        )

        inputs.properties(props)

        filesMatching("plugin.yml") {
            expand(props)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}
