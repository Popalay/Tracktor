import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin

plugins {
    id("com.android.application") version Version.androidGradlePlugin apply false
    kotlin("android") version Version.kotlin apply false
}

allprojects {
    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap/")
        maven(url = "https://jitpack.io")
        jcenter()
        google()
    }
}

subprojects {
    // Accessing the `PluginContainer` in order to use `whenPluginAdded` function
    project.plugins.configure(project = project)

    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf(
                "-Xallow-jvm-ir-dependencies",
                "-Xskip-prerelease-check",
                "-Xskip-metadata-version-check",
                "-Xopt-in=kotlin.RequiresOptIn"
            )
        }
    }

    configurations.all {
        resolutionStrategy {
            force(
                "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.7",
                "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1-native-mt"
            )
        }
    }
}

// Extension function on `PluginContainer`
fun PluginContainer.configure(project: Project) {
    whenPluginAdded {
        when (this) {
            is AppPlugin -> project.extensions
                .getByType<AppExtension>()
                .apply { applyCommons() }
            is LibraryPlugin -> project.extensions
                .getByType<LibraryExtension>()
                .apply { applyCommons() }
        }
    }
}

// Extension function on `AppExtension`
fun AppExtension.applyCommons() {
    compileSdkVersion(AndroidConfig.compileSdk)
    defaultConfig.apply {
        minSdkVersion(AndroidConfig.minSdk)
        targetSdkVersion(AndroidConfig.targetSdk)
        versionCode = properties.getOrDefault("tracktor.versioncode", 1).toString().toInt()
        versionName = AndroidConfig.versionName
    }

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

// Extension function on `LibraryExtension`
fun LibraryExtension.applyCommons() {
    compileSdkVersion(AndroidConfig.compileSdk)
    defaultConfig.apply {
        minSdkVersion(AndroidConfig.minSdk)
        targetSdkVersion(AndroidConfig.targetSdk)
    }

    compileOptions.apply {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}