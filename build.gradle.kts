import io.github.liplum.mindustry.importMindustry
import io.github.liplum.mindustry.mindustry
import io.github.liplum.mindustry.mindustryAssets
import io.github.liplum.mindustry.mindustryRepo

plugins {
    kotlin("jvm") version "1.7.0"
    id("io.github.liplum.mgpp") version "1.1.7"
}

sourceSets {
    main {
        java.srcDirs("src")
    }
    test {
        java.srcDir("test")
    }
}
group = "org.example"
version = "1.0"
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
repositories {
    mavenCentral()
    mindustryRepo()
}
dependencies {
    importMindustry()
    implementation("com.github.plumygame.mkutils:texture:05d1b361d6")
    testImplementation("com.github.plumygame.mkutils:texture:05d1b361d6")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
}
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showStandardStreams = true
    }
}
mindustry {
    dependency {
        mindustry on "v136"
        arc on "v136.1"
    }
    client {
        mindustry official "v136.1"
    }
    server {
        mindustry official "v136.1"
    }
    deploy {
        baseName = project.name
    }
}
mindustryAssets {
    root at "$projectDir/assets"
}