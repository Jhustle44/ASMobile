package com.example.asmobile.project

import java.io.File

object ProjectManager {
    fun createNewProject(baseDir: File, projectName: String, packageName: String, template: ProjectTemplate = ProjectTemplate.EmptyCompose) {
        val projectDir = File(baseDir, projectName)
        if (!projectDir.exists()) {
            projectDir.mkdirs()
        }

        // Create basic structure
        val appDir = File(projectDir, "app/src/main/java/${packageName.replace(".", "/")}")
        appDir.mkdirs()
        File(projectDir, "app/src/main/res/values").mkdirs()
        File(projectDir, "app/src/main/res/layout").mkdirs()

        // Create build.gradle.kts
        File(projectDir, "build.gradle.kts").writeText("""
            plugins {
                id("com.android.application") version "8.2.0" apply false
                id("org.jetbrains.kotlin.android") version "1.9.0" apply false
            }
        """.trimIndent())

        File(projectDir, "app/build.gradle.kts").writeText("""
            plugins {
                id("com.android.application")
                id("org.jetbrains.kotlin.android")
            }

            android {
                namespace = "$packageName"
                compileSdk = 34

                defaultConfig {
                    applicationId = "$packageName"
                    minSdk = 24
                    targetSdk = 34
                    versionCode = 1
                    versionName = "1.0"
                }
            }
        """.trimIndent())

        // Template specific files
        when (template) {
            ProjectTemplate.EmptyCompose -> createEmptyCompose(appDir, packageName, projectName)
            ProjectTemplate.BottomNav -> createBottomNav(appDir, packageName, projectName)
            ProjectTemplate.LoginFlow -> createLoginFlow(appDir, packageName, projectName)
        }

        // Create AndroidManifest.xml
        File(projectDir, "app/src/main/AndroidManifest.xml").writeText("""
            <?xml version="1.0" encoding="utf-8"?>
            <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                <application
                    android:allowBackup="true"
                    android:label="$projectName"
                    android:theme="@android:style/Theme.Material.Light.NoActionBar">
                    <activity
                        android:name=".MainActivity"
                        android:exported="true">
                        <intent-filter>
                            <action android:name="android.intent.action.MAIN" />
                            <category android:name="android.intent.category.LAUNCHER" />
                        </intent-filter>
                    </activity>
                </application>
            </manifest>
        """.trimIndent())
    }

    private fun createEmptyCompose(appDir: File, packageName: String, projectName: String) {
        File(appDir, "MainActivity.kt").writeText("""
            package $packageName

            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.material3.Text

            class MainActivity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        Text("Hello from $projectName!")
                    }
                }
            }
        """.trimIndent())
    }

    private fun createBottomNav(appDir: File, packageName: String, projectName: String) {
        File(appDir, "MainActivity.kt").writeText("""
            package $packageName

            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.material.icons.Icons
            import androidx.compose.material.icons.filled.*

            class MainActivity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        var selectedItem by remember { mutableIntStateOf(0) }
                        Scaffold(
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") }, selected = selectedItem == 0, onClick = { selectedItem = 0 })
                                    NavigationBarItem(icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") }, selected = selectedItem == 1, onClick = { selectedItem = 1 })
                                }
                            }
                        ) { padding ->
                            Text("Page " + selectedItem, modifier = androidx.compose.ui.Modifier.padding(padding))
                        }
                    }
                }
            }
        """.trimIndent())
    }

    private fun createLoginFlow(appDir: File, packageName: String, projectName: String) {
        File(appDir, "MainActivity.kt").writeText("""
            package $packageName

            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.foundation.layout.*
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.unit.dp

            class MainActivity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            Text("Login to $projectName", style = MaterialTheme.typography.headlineMedium)
                            Spacer(Modifier.height(16.dp))
                            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Username") }, modifier = Modifier.fillMaxWidth())
                            OutlinedTextField(value = "", onValueChange = {}, label = { Text("Password") }, modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(24.dp))
                            Button(onClick = {}, modifier = Modifier.fillMaxWidth()) { Text("Sign In") }
                        }
                    }
                }
            }
        """.trimIndent())
    }
}

enum class ProjectTemplate(val label: String, val description: String) {
    EmptyCompose("Empty Compose", "A basic activity with a single Text element."),
    BottomNav("Bottom Navigation", "An app with Home and Profile tabs."),
    LoginFlow("Login Flow", "A standard login screen layout.")
}
