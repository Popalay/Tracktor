plugins {
    id("com.android.application")
    kotlin("android")
}

val isCI = System.getenv("CI") == "true"
println("Is CI environment: $isCI")

android {
    defaultConfig {
        applicationId = "com.popalay.tracktor"
        resConfigs("en")

        signingConfigs {
            getByName("debug") {
                storeFile = file("../release/debug.keystore")
            }
            register("release") {
                storeFile = file("../release/release.keystore")
                keyAlias = "tracktor"
                storePassword = System.getenv("ANDROID_RELEASE_KEYSTORE_PWD").orEmpty()
                keyPassword = System.getenv("ANDROID_RELEASE_KEY_PWD").orEmpty()
            }
        }

        buildTypes {
            getByName("debug") {
                signingConfig = signingConfigs.getByName("debug")
                versionNameSuffix = "-dev"
                applicationIdSuffix = ".debug"
            }

            getByName("release") {
                signingConfig = if (isCI) signingConfigs.getByName("release") else signingConfigs.getByName("debug")
                isMinifyEnabled = true
                proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            }
        }

        lintOptions {
            isCheckReleaseBuilds = false
            isCheckDependencies = true
            isIgnoreTestSources = true
        }
    }

    buildFeatures {
        compose = true
    }

    dexOptions {
        // Don't pre-dex on CI
        preDexLibraries = !isCI
    }

    composeOptions {
        kotlinCompilerVersion = Version.kotlin
        kotlinCompilerExtensionVersion = Version.compose
    }

    packagingOptions {
        excludes.addAll(
            setOf(
                "META-INF/*.version",
                "META-INF/proguard/*",
                "/*.properties",
                "fabric/*.properties",
                "META-INF/*.properties",
                "META-INF/*.kotlin_module"
            )
        )
    }
}

dependencies {
    implementation(project(":data"))
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(Libs.kotlinStd)
    implementation(Libs.materialDesign)
    implementation(Libs.androidXCore)
    implementation(Libs.Kotlinx.datetime)
    implementation(Libs.Kotlinx.serialization)
    implementation(Libs.Compose.animation)
    implementation(Libs.Compose.foundation)
    implementation(Libs.Compose.foundationLayout)
    implementation(Libs.Compose.material)
    implementation(Libs.Compose.runtime)
    implementation(Libs.Compose.ui)
    implementation(Libs.Koin.core)
    implementation(Libs.Koin.android)
    implementation(Libs.Workflow.runtime) {
        version { branch = "popalay/multiplatform" }
    }
    implementation(Libs.Workflow.compose) {
        version { branch = "popalay/multiplatform" }
    }
    implementation(Libs.Workflow.composeTooling) {
        version { branch = "popalay/multiplatform" }
    }
}