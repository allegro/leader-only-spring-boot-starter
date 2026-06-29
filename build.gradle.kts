import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.axionRelease)
    alias(libs.plugins.nexus.publish)
    signing
}

repositories {
    mavenCentral()
}

group = "pl.allegro.tech.boot"
version = scmVersion.version

java {
    withSourcesJar()
    withJavadocJar()
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    api("org.springframework.boot:spring-boot-starter-aspectj")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api(libs.bundles.curator)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation("org.springframework.boot:spring-boot")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(libs.bundles.testcontainers)
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation(libs.awaitility)
    testImplementation(libs.zookeeper)
}

tasks.bootJar {
    enabled = false
}

tasks.jar {
    enabled = true
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

publishing {
    publications {
        create<MavenPublication>("sonatype") {
            from(components["java"])
            pom {
                name = "leader-only-spring-boot-starter"
                description = "Spring Boot starter for leader only processing"
                url = "https://github.com/allegro/leader-only-spring-boot-starter"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = "wpanas"
                        name = "Waldemar Panas"
                    }
                }
                scm {
                    connection = "scm:git@github.com:allegro/leader-only-spring-boot-starter.git"
                    developerConnection = "scm:git@github.com:allegro/leader-only-spring-boot-starter.git"
                    url = "https://github.com/allegro/leader-only-spring-boot-starter"
                }
            }
        }
    }
}

nexusPublishing {
    repositories {
        sonatype {
            nexusUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/")
            snapshotRepositoryUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            username = System.getenv("SONATYPE_USERNAME")
            password = System.getenv("SONATYPE_PASSWORD")
        }
    }
}

signing {
    val gpgKeyId = System.getenv("GPG_KEY_ID")
    val gpgPrivateKey = System.getenv("GPG_PRIVATE_KEY")
    val gpgPrivateKeyPassword = System.getenv("GPG_PRIVATE_KEY_PASSWORD")

    if (gpgKeyId != null && gpgPrivateKey != null && gpgPrivateKeyPassword != null) {
        useInMemoryPgpKeys(gpgKeyId, gpgPrivateKey, gpgPrivateKeyPassword)
        sign(publishing.publications["sonatype"])
    }
}
