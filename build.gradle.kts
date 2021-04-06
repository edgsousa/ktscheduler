
plugins {
    kotlin("multiplatform") version "1.4.32"
    id("maven-publish")
    jacoco
}

jacoco {
    toolVersion = "0.8.6"
}

group = "com.github.edgsousa.ktscheduler" //because of jitpack.io
if (version.toString() == "unspecified") {
    version = "0.0.1-SNAPSHOT"
}

val mockk = "1.11.0"
val coroutines = "1.4.3"

repositories {
    mavenCentral()
}


kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
        withJava()
    }

    js {
        nodejs {
            testTask {
                useMocha {
                    timeout = "60000"// mochaTimeout here as string
                }
            }
        }
    }

    sourceSets {
         commonMain  {
            dependencies {
                implementation(kotlin("stdlib-common"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation("io.mockk:mockk-common:$mockk")
            }
        }

        named("jvmMain") {
            dependencies {
                implementation("org.slf4j:slf4j-api:1.7.30")
            }
        }
        named("jvmTest") {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("io.mockk:mockk:$mockk")
                implementation("ch.qos.logback:logback-core:1.2.3")
                implementation("ch.qos.logback:logback-classic:1.2.3")
            }
        }

        named("jsMain") {
            dependencies {
                implementation(kotlin("stdlib-js"))
                implementation(npm("@js-joda/core", ">=3.2.0 <3.3.0"))
                implementation(npm("@js-joda/timezone", ">=2.5.0"))
            }
        }
        named("jsTest") {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }

    }
}


tasks.named<JacocoReport>("jacocoTestReport")  {
    dependsOn("jvmTest")
    reports {
        csv.isEnabled = false
        xml.isEnabled = true
        html.isEnabled = true
    }
    classDirectories.setFrom(file("${buildDir}/classes/kotlin/jvm/main"))
    sourceDirectories.setFrom(files("src/commonMain/kotlin", "src/jvmMain/kotlin"))
    executionData.setFrom(files("${buildDir}/jacoco/jvmTest.exec"))
}

java {
    withJavadocJar()
    withSourcesJar()
}
