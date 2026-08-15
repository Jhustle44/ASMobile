package com.example.asmobile

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.asmobile.ui.theme.ASMobileTheme
import com.example.asmobile.ui.workspace.WorkspaceScreen
import java.io.File

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
    }
}