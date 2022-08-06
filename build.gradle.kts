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
    implementation("com.github.plumygame.mkutils:texture:c1a1b4fca1")
    testImplementation("com.github.plumygame.mkutils:texture:c1a1b4fca1")
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
        mindustry mirror "1a64344e5a"
        arc on "v137"
    }
    client {
        mindustry official "v137"
    }
    server {
        mindustry official "v137"
    }
    deploy {
        baseName = project.name
    }
}
mindustryAssets {
    root at "$projectDir/assets"
}