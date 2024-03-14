import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/central")
    }
    maven {
        url = uri("https://maven.citizensnpcs.co/repo")
    }
    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
    maven {
        url = uri("https://jitpack.io")
    }
    maven {
        url = uri("https://repo.alessiodp.com/releases")
    }
    maven {
        url = uri("https://repo.dmulloy2.net/repository/public/")
    }
    maven {
        url = uri("https://repo.codemc.io/repository/maven-public/")
    }
}

dependencies {
    implementation("com.github.ForestTechMC:ForestColorAPI:1.4")
    compileOnly("org.spigotmc:spigot:1.20.4-R0.1-SNAPSHOT:remapped-mojang")
    compileOnly("net.citizensnpcs:citizens-main:2.0.33-SNAPSHOT")
    compileOnly("net.byteflux:libby-bukkit:1.1.5")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.1.0")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("de.tr7zw:item-nbt-api-plugin:2.12.2")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
}

group = "dev.ua.ikeepcalm"
version = "1.0-SNAPSHOT"
description = "LordOfTheMinecraft"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<ShadowJar> {
    archiveFileName.set("LordOfTheMinecraft-SNAPSHOT.jar")
    relocate("net.byteflux.libby", "dev.ua.ikeepcalm.libby")
    relocate("cz.foresttech.api", "dev.ua.ikeepcalm.colorapi")
}
tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}