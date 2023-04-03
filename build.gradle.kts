plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    `java-library`
    `maven-publish`
    signing
    id("com.diffplug.spotless") version "6.8.0"
}

repositories { mavenCentral() }

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

group = "io.github.leonschreuder"

version = "1.0.1"

spotless { kotlin { ktfmt().kotlinlangStyle() } }

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create("mavenJava", MavenPublication::class) {
            artifactId = "kotlin-exec"
            from(components["java"])
            pom {
                name.set(artifactId)
                description.set("A nice kotlin api for Javas ProcessBuilder")
                url.set("https://github.com/leonschreuder/kotlin-exec")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://tldrlegal.com/license/mit-license")
                    }
                }
                developers {
                    developer {
                        id.set("leonschreuder")
                        name.set("Leon Schreuder")
                        email.set("leon.schreuder@proton.me")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/leonschreuder/kotlin-exec.git")
                    connection.set("scm:git:ssh://github.com/leonschreuder/kotlin-exec.git" )
                    url.set("https://github.com/leonschreuder/kotlin-exec")
                }
            }
        }
    }

    repositories {
        val url =
            if (project.version.toString().endsWith("-SNAPSHOT")) {
                "https://s01.oss.sonatype.org/content/repositories/snapshots/"
            } else {
                "https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/"
            }

        maven(url) {
            credentials {
                username = properties["nexusUsername"]?.toString() ?: ""
                password = properties["nexusPassword"]?.toString() ?: ""
            }
        }
    }
}

signing {
    sign(publishing.publications["mavenJava"])
}
