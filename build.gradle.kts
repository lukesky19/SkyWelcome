plugins {
    `java-library`
    id("com.gradleup.shadow") version "9.4.1"
}

group = "com.github.lukesky19"
version = "2.0.0.0"

subprojects {
    apply(plugin = "java-library")

    group = "com.github.lukesky19"
    version = "2.0.0.0"

    dependencies {
        // Annotations
        compileOnly("org.jspecify:jspecify:1.0.0")

        // SkyLib
        compileOnly("com.github.lukesky19:SkyLib:2.0.1.0")

        // Google
        compileOnly("com.google.code.gson:gson:2.13.2")
        compileOnly("com.google.guava:guava:33.5.0-jre")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(25))
        withSourcesJar()
        withJavadocJar()
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

        // This allows usage of @apiNode in javadocs
        javadoc {
            (options as StandardJavadocDocletOptions).tags("apiNote:a:API Note:")
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
}

dependencies {
    implementation(project(":SkyWelcome-Common"))
    implementation(project(":SkyWelcome-Paper"))
    implementation(project(":SkyWelcome-Velocity"))
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
    withSourcesJar()
    withJavadocJar()
}

tasks {
    shadowJar {
        manifest {
            attributes["paperweight-mappings-namespace"] = "mojang"
        }

        archiveClassifier.set("")
    }

    build {
        dependsOn(shadowJar)
    }
}