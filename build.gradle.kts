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

version = "1.0.0"

spotless { kotlin { ktfmt().kotlinlangStyle() } }

java {
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

/*
 publishing {
     publications {
         mavenJava(MavenPublication) {
                     artifactId = '<libraryname>'
                                 from components.java

             pom {
                 name = '<libraryname>'
                 description = '<description>'
                 url = '<library/project url>'
                 licenses {
                     license {
                         name = '<Your license>'
                         url = '<license-url>'
                     }
                 }
                 developers {
                     developer {
                         id = '<developerID>'
                         name = 'Developer Name'
                         email = 'Developer email'
                     }
                 }
                 scm {
                 connection = 'scm:git:git://github.com/CuriousNikhil/simplepoller.git'
                     developerConnection = 'scm:git:ssh://github.com/CuriousNikhil/simplepoller.git'
                     url = 'https://github.com/CuriousNikhil/simplepoller'
                 }
             }
         }
     }    repositories {
         maven {

             credentials {
                 username = "$NEXUS_USERNAME"
                 password = "$NEXUS_PASSWORD"
             }

             name = "<name anything>"
             url = 'https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/'
         }
     }
 }
  */
