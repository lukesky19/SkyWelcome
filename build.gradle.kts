plugins {
    java
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.github.lukesky19"
version = "1.3.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
    maven("https://jitpack.io") {
        name = "jitpack"
    }

    mavenLocal()
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("com.arcaniax:HeadDatabase-API:1.3.2")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.github.lukesky19:SkyLib:1.0.0")
    implementation("com.github.stefvanschie.inventoryframework:IF:0.10.18")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.shadowJar {
    manifest {
        attributes["paperweight-mappings-namespace"] = "mojang"
    }
    archiveClassifier.set("")
    relocate("com.github.stefvanschie.inventoryframework", "com.github.lukesky19.skywelcome.libs.inventoryframework")
}