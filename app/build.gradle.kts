plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.mantelgroup.appfunctionsdemo"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.mantelgroup.appfunctionsdemo"
        minSdk = 33
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

// AGP 9's built-in Kotlin + KSP does not register KSP's generated `resources/assets`
// directory as Android assets, so the AppFunctions registration XML (app_functions_v2.xml)
// never reaches the APK. Wire it in per-variant, with an explicit task dependency.
androidComponents {
    onVariants { variant ->
        val kspAssets = layout.buildDirectory
            .dir("generated/ksp/${variant.name}/resources/assets")
        kspAssets.get().asFile.mkdirs()
        variant.sources.assets?.addStaticSourceDirectory(kspAssets.get().asFile.absolutePath)
    }
}

// Ensure KSP (which produces app_functions_v2.xml) runs before assets are merged.
tasks.matching { it.name.startsWith("merge") && it.name.endsWith("Assets") }.configureEach {
    val capital = name.removePrefix("merge").removeSuffix("Assets")
    dependsOn(tasks.matching { it.name == "ksp${capital}Kotlin" })
}

ksp {
    arg("appfunctions:aggregateAppFunctions", "true")
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.navigation.compose)

    // AppFunctions
    implementation(libs.androidx.appfunctions)
    implementation(libs.androidx.appfunctions.service)
    ksp(libs.androidx.appfunctions.compiler)

    // Firebase AI Logic (cloud Gemini + function calling)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.ai)
    implementation(libs.firebase.appcheck.debug)
    // JSON types used by firebase-ai's function-call args / responses.
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
}