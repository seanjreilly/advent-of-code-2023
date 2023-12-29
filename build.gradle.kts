plugins {
    kotlin("jvm") version "1.9.22"
    id("com.bnorm.power.kotlin-power-assert") version "0.13.0"
    `jvm-test-suite`
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
    jvmToolchain(21)
    sourceSets.all {
        languageSettings { languageVersion = "2.0" }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }
    }
}

//tasks {
//    test {
//        testLogging {
//            events("passed", "skipped", "failed")
//        }
//    }
//}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    implementation(kotlin("stdlib-jdk8"))

    implementation("org.jgrapht:jgrapht-core:1.5.2")
}