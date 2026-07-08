package com.example.myapplication

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onImportClick: () -> Unit,
    onChangePathClick: () -> Unit,
    onAboutClick: () -> Unit,
    viewModel: SketchwareViewModel
) {
    var isDarkTheme by remember { mutableStateOf(true) }
    val context = LocalContext.current
    val currentPath by viewModel.basePath.collectAsState()

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = "Configuración MIUIX",
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            item {
                PreferenceHeader(title = "Apariencia Pro")
            }
            item {
                MiuixPreferenceItem(
                    title = "Modo Oscuro HyperOS",
                    summary = "Negro AMOLED para ahorro de batería",
                    icon = Icons.Default.Palette,
                    action = {
                        Switch(checked = isDarkTheme, onCheckedChange = { isDarkTheme = it })
                    }
                )
            }
            item {
                PreferenceHeader(title = "Almacenamiento")
            }
            item {
                MiuixPreferenceItem(
                    title = "Carpeta de Guardado",
                    summary = currentPath,
                    icon = Icons.Default.Folder,
                    action = {
                        Button(onClick = onChangePathClick) {
                            Text("Cambiar", color = Color.White)
                        }
                    }
                )
            }
            item {
                PreferenceHeader(title = "Seguridad y Datos")
            }
            item {
                MiuixPreferenceItem(
                    title = "Copia de Seguridad",
                    summary = "Exportar proyectos a la carpeta actual",
                    icon = Icons.Default.Backup,
                    action = {
                        Button(onClick = { 
                            val msg = viewModel.exportAllProjects()
                            Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                        }) {
                            Text("Exportar", color = Color.White)
                        }
                    }
                )
            }
            item {
                MiuixPreferenceItem(
                    title = "Restaurar Proyectos",
                    summary = "Importar archivos (.swx) desde el explorador",
                    icon = Icons.Default.Restore,
                    action = {
                        Button(onClick = onImportClick) {
                            Text("Importar", color = Color.White)
                        }
                    }
                )
            }
            item {
                PreferenceHeader(title = "Información")
            }
            item {
                MiuixPreferenceItem(
                    title = "Acerca de",
                    summary = "Versión, dispositivo y redes sociales",
                    icon = Icons.Default.Info,
                    action = {
                        Button(onClick = onAboutClick) {
                            Text("Ver", color = Color.White)
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun PreferenceHeader(title: String) {
    Text(
        text = title.uppercase(),
        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp),
        color = Color(0xFF007AFF),
        style = MiuixTheme.textStyles.footnote2
    )
}

@Composable
fun MiuixPreferenceItem(title: String, summary: String, icon: androidx.compose.ui.graphics.vector.ImageVector, action: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF007AFF).copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF007AFF))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MiuixTheme.textStyles.title3, color = Color.White)
                Text(text = summary, color = Color.Gray, style = MiuixTheme.textStyles.footnote1, maxLines = 1)
            }
            action()
        }
    }
}
