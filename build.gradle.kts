plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.github.lukesky19"
version = "1.5.0.0"

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.7-R0.1-SNAPSHOT")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.lukesky19:SkyLib:1.3.0.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("plugin.yml") {
            expand(props)
        }
    }

    jar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")
    }

    build {
        dependsOn(javadoc)
    }
}