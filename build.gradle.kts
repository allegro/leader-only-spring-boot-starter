import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import java.time.Duration

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.springBoot)
    alias(libs.plugins.springDependencyManagement)
    alias(libs.plugins.axionRelease)
    alias(libs.plugins.nexusPublish)
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

    api("org.springframework.boot:spring-boot-starter-aop")
    api("org.springframework.boot:spring-boot-autoconfigure")
    api(libs.bundles.curator)

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    testImplementation(platform(libs.testcontainers))
    testImplementation("org.springframework.boot:spring-boot")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
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
    afterEvaluate {
        publications {
            withType<MavenPublication> {
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
}

nexusPublishing {
    connectTimeout = Duration.ofMinutes(10)
    clientTimeout = Duration.ofMinutes(10)
    repositories {
        sonatype {
            username.set(System.getenv("SONATYPE_USERNAME"))
            password.set(System.getenv("SONATYPE_PASSWORD"))
        }
    }
    transitionCheckOptions {
        maxRetries.set(30)
        delayBetween.set(Duration.ofSeconds(45))
    }
}

if (System.getenv("GPG_KEY_ID") != null) {
    signing {
        useInMemoryPgpKeys(
            System.getenv("GPG_KEY_ID"),
            System.getenv("GPG_PRIVATE_KEY"),
            System.getenv("GPG_PRIVATE_KEY_PASSWORD")
        )
        sign(publishing.publications)
    }
}
