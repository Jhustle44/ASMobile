package com.example.asmobile.navigation

import kotlinx.serialization.Serializable
import androidx.navigation3.runtime.NavKey

@Serializable
data object FileListKey : NavKey

@Serializable
data class FileDetailKey(val filePath: String) : NavKey
