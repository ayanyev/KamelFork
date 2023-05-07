import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    `nexus-staging`
}

buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath(Dependencies.ComposeGradlePlugin)
        classpath(Dependencies.KotlinGradlePlugin)
        classpath(Dependencies.Android.GradlePlugin)
    }
}

val file = project.file("local.properties")

if (file.exists()) {
    Properties().apply {
        val inputStream = file.inputStream()
        load(inputStream)
        ext {
            set("signing.keyId", getProperty("signing.keyId"))
            set("signing.password", getProperty("signing.password"))
            set("ossrh.username", getProperty("ossrh.username"))
            set("ossrh.password", getProperty("ossrh.password"))
            set("signing.secretKeyRingFile", getProperty("signing.secretKeyRingFile"))
            set("stagingProfileId", getProperty("stagingProfileId"))
        }
        inputStream.close()
    }
} else {
    ext {
        set("signing.keyId", System.getenv("SIGNING_KEY_ID"))
        set("signing.password", System.getenv("SIGNING_PASSWORD"))
        set("ossrh.username", System.getenv("OSSRH_USERNAME"))
        set("ossrh.password", System.getenv("OSSRH_PASSWORD"))
        set("signing.secretKeyRingFile", System.getenv("SIGNING_SECRET_KEY_RING_FILE"))
        set("stagingProfileId", System.getenv("STAGING_PROFILE_ID"))
    }
}

allprojects {

    group = Kamel.Group
    version = Kamel.Version

    repositories {
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            publications.withType<MavenPublication>().configureEach {

                artifact(emptyJavadocJar.get())

                pom {

                    name.set("Kamel")
                    description.set("Kotlin Asynchronous Media Loading Library that supports Compose")
                    url.set("https://github.com/alialbaali/Kamel")

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }

                    developers {
                        developer {
                            id.set("alialbaali")
                            name.set("Ali Albaali")
                        }
                    }

                    scm {
                        connection.set("scm:git:github.com/alialbaali/Kamel.git")
                        developerConnection.set("scm:git:ssh://github.com/alialbaali/Kamel.git")
                        url.set("https://github.com/alialbaali/Kamel/tree/main")
                    }

                }

            }

            repositories {
                maven {

                    name = "MavenCentral"

                    val releasesRepoUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
                    val snapshotsRepoUrl = "https://oss.sonatype.org/content/repositories/snapshots/"

                    url = if (version.toString().endsWith("SNAPSHOT")) uri(snapshotsRepoUrl) else uri(releasesRepoUrl)

                    credentials {
                        username = rootProject.ext["ossrh.username"] as String? ?: ""
                        password = rootProject.ext["ossrh.password"] as String? ?: ""
                    }

                }
            }

            extensions.findByType<SigningExtension>()?.sign(publications)
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xallow-result-return-type")
            jvmTarget = "11"
        }
    }

    // workaround for: https://github.com/icerockdev/moko-resources/issues/421#issuecomment-1484530912
    // remove after https://github.com/icerockdev/moko-resources/issues/477 resolved and moko-resources can
    // be updated
    tasks.matching { it.name == "jvmProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRjvmMain" })
    }
    tasks.matching { it.name == "desktopProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRdesktopMain" })
    }
    tasks.matching { it.name == "iosSimulatorArm64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRiosSimulatorArm64Main" })
    }
    tasks.matching { it.name == "metadataIosMainProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRcommonMain" })
    }
    tasks.matching { it.name == "metadataCommonMainProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRcommonMain" })
    }
    tasks.matching { it.name == "iosX64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRiosX64Main" })
    }
    tasks.matching { it.name == "iosArm64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRiosArm64Main" })
    }
    tasks.matching { it.name == "uikitSimulatorArm64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRuikitSimulatorArm64Main" })
    }
    tasks.matching { it.name == "uikitX64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRuikitX64Main" })
    }
    tasks.matching { it.name == "macosArm64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRmacosArm64Main" })
    }
    tasks.matching { it.name == "macosX64ProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRmacosX64Main" })
    }
    tasks.matching { it.name == "jsProcessResources" }.configureEach {
        dependsOn(tasks.matching { it.name == "generateMRjsMain" })
    }

}

nexusStaging {
    packageGroup = Kamel.Group
    stagingProfileId = rootProject.ext["stagingProfileId"] as String? ?: ""
    username = rootProject.ext["ossrh.username"] as String? ?: ""
    password = rootProject.ext["ossrh.password"] as String? ?: ""
}
