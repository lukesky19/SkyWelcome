rootProject.name = "SkyWelcome"

include(":SkyWelcome-Common")
project(":SkyWelcome-Common").projectDir = file("common")
include(":SkyWelcome-Paper")
project(":SkyWelcome-Paper").projectDir = file("paper")
include(":SkyWelcome-Velocity")
project(":SkyWelcome-Velocity").projectDir = file("velocity")

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        mavenCentral()
        mavenLocal()

        // Paper
        maven("https://repo.papermc.io/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://jitpack.io")
        maven("https://repo.essentialsx.net/releases/")
        maven("https://repo.rosewooddev.io/repository/public/")

        // Velocity
        maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    }
}