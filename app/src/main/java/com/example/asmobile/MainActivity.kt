package com.example.asmobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.asmobile.ui.theme.ASMobileTheme
import com.example.asmobile.ui.workspace.WorkspaceScreen
import java.io.File

class MainActivity : ComponentActivity() {
    private var keepSplashScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        
        // Keep splash screen on for 1.5 seconds
        splashScreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(1500)
            keepSplashScreen = false
        }

        // Subtle exit animation
        splashScreen.setOnExitAnimationListener { splashScreenProvider ->
            val iconView = splashScreenProvider.iconView
            iconView.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .alpha(0f)
                .setDuration(400L)
                .withEndAction { splashScreenProvider.remove() }
                .start()
        }

        seedSampleFiles(this)
        enableEdgeToEdge()
        setContent {
            ASMobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    WorkspaceScreen()
                }
            }
        }
    }

    private fun seedSampleFiles(context: Context) {
        val filesDir = context.filesDir
        
        // Sample Kotlin file
        val ktFile = File(filesDir, "MainActivity.kt")
        if (!ktFile.exists()) {
            ktFile.writeText(
                """
                package com.example.asmobile

                import android.os.Bundle
                import androidx.activity.ComponentActivity
                import androidx.activity.compose.setContent

                class MainActivity : ComponentActivity() {
                    override fun onCreate(savedInstanceState: Bundle?) {
                        super.onCreate(savedInstanceState)
                        setContent {
                            // Hello World
                        }
                    }
                }
            """.trimIndent(),
            )
        }

        // Sample Java file
        val javaFile = File(filesDir, "Utils.java")
        if (!javaFile.exists()) {
            javaFile.writeText("""
                package com.example.asmobile;

                public class Utils {
                    public static void log(String message) {
                        System.out.println("LOG: " + message);
                    }
                }
            """.trimIndent(),
            )
        }

        // Sample XML file
        val xmlFile = File(filesDir, "main_layout.xml")
        if (!xmlFile.exists()) {
            xmlFile.writeText("""
                <?xml version="1.0" encoding="utf-8"?>
                <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:orientation="vertical">

                    <TextView
                        android:id="@+id/textView"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        android:text="Hello Android!" />

                </LinearLayout>
            """.trimIndent(),
            )
        }

        // Sample directory
        val subDir = File(filesDir, "components")
        if (!subDir.exists()) {
            subDir.mkdir()
            File(subDir, "Button.kt").writeText("""
                package com.example.asmobile.components

                import androidx.compose.material3.Button
                import androidx.compose.material3.Text
                import androidx.compose.runtime.Composable

                @Composable
                fun CustomButton(onClick: () -> Unit) {
                    Button(onClick = onClick) {
                        Text("Click Me")
                    }
                }
            """.trimIndent(),
            )
        }
        // Sample build.gradle.kts
        val gradleFile = File(filesDir, "build.gradle.kts")
        if (!gradleFile.exists()) {
            gradleFile.writeText(
                """
                plugins {
                    id("com.android.application")
                    kotlin("android")
                }

                android {
                    namespace = "com.example.asmobile"
                    compileSdk = 34
                }
                """.trimIndent(),
            )
        }

        // Sample AndroidManifest.xml
        val manifestFile = File(filesDir, "AndroidManifest.xml")
        if (!manifestFile.exists()) {
            manifestFile.writeText(
                """
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android">
                    <application
                        android:allowBackup="true"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name">
                    </application>
                </manifest>
                """.trimIndent(),
            )
        }
    }
}