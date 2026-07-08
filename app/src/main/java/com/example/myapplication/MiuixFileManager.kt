package com.example.myapplication

import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.File

@Composable
fun MiuixFileManager(
    mode: PickerMode = PickerMode.File,
    onFileSelected: (File) -> Unit = {},
    onFolderSelected: (File) -> Unit = {},
    onBack: () -> Unit
) {
    var currentDir by remember { mutableStateOf(Environment.getExternalStorageDirectory()) }
    val files = currentDir.listFiles()?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() })) ?: emptyList()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = currentDir.name.ifEmpty { "Explorador" },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            if (mode == PickerMode.Folder) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = { onFolderSelected(currentDir) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Seleccionar esta carpeta", color = Color.White)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Text(
                text = currentDir.absolutePath,
                style = MiuixTheme.textStyles.footnote1,
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
            
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                if (currentDir.parentFile != null && currentDir != Environment.getExternalStorageDirectory()) {
                    item {
                        FileItem(name = "..", isDirectory = true) {
                            currentDir = currentDir.parentFile!!
                        }
                    }
                }
                
                items(files) { file ->
                    FileItem(name = file.name, isDirectory = file.isDirectory) {
                        if (file.isDirectory) {
                            currentDir = file
                        } else if (mode == PickerMode.File && file.name.endsWith(".swx")) {
                            onFileSelected(file)
                        }
                    }
                }
            }
        }
    }
}

enum class PickerMode { File, Folder }

@Composable
fun FileItem(name: String, isDirectory: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDirectory) Icons.Default.Folder else Icons.Default.Description,
                contentDescription = null,
                tint = if (isDirectory) Color(0xFF007AFF) else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = name,
                style = MiuixTheme.textStyles.body1,
                color = Color.White
            )
        }
    }
}
