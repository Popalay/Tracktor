plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
}

android {
    defaultConfig {
        javaCompileOptions {
            annotationProcessorOptions {
                arguments.putAll(
                    setOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true"
                    )
                )
            }
        }
    }
}

dependencies {
    implementation(Libs.kotlinStd)
    implementation(Libs.preferenceKtx)

    implementation(Libs.Room.runtime)
    implementation(Libs.Room.ktx)
    kapt(Libs.Room.compiler)

    implementation(Libs.Koin.core)
    implementation(Libs.Koin.android)

    implementation(Libs.Moshi.core)
    kapt(Libs.Moshi.codegen)
}