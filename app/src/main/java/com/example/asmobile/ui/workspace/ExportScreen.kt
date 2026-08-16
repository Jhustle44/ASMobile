package com.example.asmobile.ui.workspace

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ExportScreen(
    viewModel: BuildToolsViewModel,
    onBack: () -> Unit
) {
    var alias by remember { mutableStateOf("release") }
    var password by remember { mutableStateOf("") }
    val isProcessing by viewModel.isProcessing

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, null)
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Export & Sign APK", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                }
            }

            Column(modifier = Modifier.padding(24.dp)) {
                Text("Keystore Settings", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Key Alias") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(12.dp))
                
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Key Password") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation()
                )
                
                Spacer(Modifier.height(24.dp))
                
                Button(
                    onClick = { viewModel.generateKeystore(alias, password) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing && password.isNotBlank()
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp, color = Color.White)
                    } else {
                        Icon(Icons.Rounded.VpnKey, null)
                        Spacer(Modifier.width(12.dp))
                        Text("Generate New Key")
                    }
                }
                
                Spacer(Modifier.height(32.dp))
                HorizontalDivider()
                Spacer(Modifier.height(32.dp))
                
                Text("Optimization", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.zipalignApk("app-release.apk") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Rounded.Bolt, null)
                    Spacer(Modifier.width(12.dp))
                    Text("ZipAlign & Optimize APK")
                }
                
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = { /* Simulated export */ },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Rounded.IosShare, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Export Signed APK")
                }
            }
        }
    }
}
