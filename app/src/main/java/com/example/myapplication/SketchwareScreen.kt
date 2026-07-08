package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.model.Project
import com.example.myapplication.model.ProjectColors
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SketchwareScreen(
    viewModel: SketchwareViewModel = viewModel(),
    onProjectClick: (String) -> Unit,
    onSettingsClick: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val isGenerating by viewModel.isGenerating.collectAsState()
    val errorByIA by viewModel.error.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showAiDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = Color.Black, 
        topBar = {
            TopAppBar(
                title = "Sketchware Pro X",
                color = Color.Black.copy(alpha = 0.6f),
                actions = {
                    IconButton(onClick = { showHelpDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = "Ayuda",
                            tint = Color.White
                        )
                    }

                    IconButton(onClick = { showAiDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Generar con IA",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                FloatingNavigationBar(
                    color = Color(0xFF1A1A1A).copy(alpha = 0.9f)
                ) {
                    FloatingNavigationBarItem(
                        selected = selectedBottomTab == 0,
                        onClick = { selectedBottomTab = 0 },
                        icon = Icons.Default.Folder,
                        label = "Proyectos"
                    )
                    
                    FloatingNavigationBarItem(
                        selected = false,
                        onClick = { showAddDialog = true },
                        icon = Icons.Default.AddCircle,
                        label = "Crear"
                    )

                    FloatingNavigationBarItem(
                        selected = selectedBottomTab == 1,
                        onClick = { selectedBottomTab = 1 },
                        icon = Icons.Default.Extension,
                        label = "Módulos"
                    )
                    FloatingNavigationBarItem(
                        selected = selectedBottomTab == 2,
                        onClick = { onSettingsClick() },
                        icon = Icons.Default.Settings,
                        label = "Ajustes"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (selectedBottomTab == 0) {
                ProjectsListContent(
                    projects = projects, 
                    isGenerating = isGenerating, 
                    errorByIA = errorByIA, 
                    onProjectClick = onProjectClick,
                    onDeleteProject = { viewModel.deleteProject(it) }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    top.yukonga.miuix.kmp.basic.Text("Pestaña de Módulos (HyperOS Style)", color = Color.Gray)
                }
            }
        }

        if (showAddDialog) {
            AddProjectDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { name, pkg, verName, verCode, colors ->
                    viewModel.addProject(name, pkg, verName, verCode, colors)
                    showAddDialog = false
                }
            )
        }

        if (showAiDialog) {
            AiProjectDialog(
                onDismiss = { showAiDialog = false },
                onConfirm = { prompt ->
                    viewModel.generateProjectWithAI(prompt)
                    showAiDialog = false
                }
            )
        }

        if (showHelpDialog) {
            SketchwareHelpDialog(onDismiss = { showHelpDialog = false })
        }
    }
}

@Composable
fun ProjectsListContent(
    projects: List<Project>,
    isGenerating: Boolean,
    errorByIA: String?,
    onProjectClick: (String) -> Unit,
    onDeleteProject: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        if (isGenerating) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            top.yukonga.miuix.kmp.basic.Text(
                "IA pensando...",
                modifier = Modifier.padding(8.dp),
                color = Color(0xFF007AFF)
            )
        }

        errorByIA?.let {
            top.yukonga.miuix.kmp.basic.Text(
                text = "Error de IA: $it",
                color = Color.Red,
                modifier = Modifier.padding(16.dp)
            )
        }

        if (projects.isEmpty() && !isGenerating) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                top.yukonga.miuix.kmp.basic.Text(
                    text = "Vacío. Crea un proyecto para empezar.",
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 120.dp, top = 8.dp)
            ) {
                items(projects) { project ->
                    ProjectItem(
                        project = project, 
                        onClick = { onProjectClick(project.id) },
                        onDelete = { onDeleteProject(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectItem(project: Project, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFF007AFF).copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Code, contentDescription = null, tint = Color(0xFF007AFF))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                top.yukonga.miuix.kmp.basic.Text(text = project.name, style = MiuixTheme.textStyles.title2, color = Color.White)
                top.yukonga.miuix.kmp.basic.Text(text = project.packageName, color = Color.White.copy(alpha = 0.6f), style = MiuixTheme.textStyles.footnote1)
                top.yukonga.miuix.kmp.basic.Text(text = "ID: ${project.projectID}", color = Color.White.copy(alpha = 0.4f), style = MiuixTheme.textStyles.footnote2)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red.copy(alpha = 0.6f))
            }
        }
    }
}

@Composable
fun SketchwareHelpDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                top.yukonga.miuix.kmp.basic.Text("Ayuda Pro X", style = MiuixTheme.textStyles.title1, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                HelpItem("Créditos", "Basado en Sketchware Pro. Créditos a sus creadores originales.")
                HelpItem("Persistencia", "Ahora tus proyectos se guardan automáticamente. No perderás nada al cerrar la app.")
                HelpItem("Build APK", "¡Nuevo! Usa el botón del martillo en el editor para generar un archivo .apk real de tu proyecto.")
                HelpItem("Editor", "Añade videos, imágenes y lógica MIUIX con efecto cristal líquido.")
                
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    top.yukonga.miuix.kmp.basic.Text("Cerrar", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun HelpItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        top.yukonga.miuix.kmp.basic.Text(title, color = Color(0xFF007AFF), style = MiuixTheme.textStyles.title3)
        top.yukonga.miuix.kmp.basic.Text(desc, color = Color.White.copy(alpha = 0.8f), style = MiuixTheme.textStyles.body2)
    }
}

@Composable
fun AiProjectDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var prompt by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                top.yukonga.miuix.kmp.basic.Text("Generar con IA", style = MiuixTheme.textStyles.title1, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = prompt,
                    onValueChange = { prompt = it },
                    label = { Text("Describe tu app...") }
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(text = "Cancelar", onClick = onDismiss)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onConfirm(prompt) }, 
                        enabled = prompt.isNotBlank()
                    ) {
                        top.yukonga.miuix.kmp.basic.Text("Generar", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun AddProjectDialog(onDismiss: () -> Unit, onConfirm: (String, String, String, Int, ProjectColors) -> Unit) {
    var name by remember { mutableStateOf("") }
    var packageName by remember { mutableStateOf("com.my.app") }
    var versionName by remember { mutableStateOf("1.0") }
    var versionCode by remember { mutableStateOf("1") }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                top.yukonga.miuix.kmp.basic.Text("Nuevo Proyecto", style = MiuixTheme.textStyles.title1, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                
                Box(
                    modifier = Modifier.size(80.dp).background(Color(0xFF1A1A1A), RoundedCornerShape(16.dp)).align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Android, contentDescription = "Change Icon", tint = Color.Gray, modifier = Modifier.size(40.dp))
                }
                top.yukonga.miuix.kmp.basic.Text("Tap to change icon", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                
                Spacer(modifier = Modifier.height(16.dp))
                TextField(value = name, onValueChange = { name = it }, label = { Text("App Name") })
                Spacer(modifier = Modifier.height(8.dp))
                TextField(value = packageName, onValueChange = { packageName = it }, label = { Text("Package Name") })
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    TextField(value = versionCode, onValueChange = { versionCode = it }, label = { Text("Version Code") }, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(value = versionName, onValueChange = { versionName = it }, label = { Text("Version Name") }, modifier = Modifier.weight(1f))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                top.yukonga.miuix.kmp.basic.Text("Colors", color = Color.Gray, style = MiuixTheme.textStyles.footnote2)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ColorCircle(Color(0xFF007AFF))
                    ColorCircle(Color(0xFF007AFF))
                    ColorCircle(Color(0xFF0056B3))
                    ColorCircle(Color(0x20007AFF))
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(text = "Cancelar", onClick = onDismiss)
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(name, packageName, versionName, versionCode.toIntOrNull() ?: 1, ProjectColors()) }) {
                        top.yukonga.miuix.kmp.basic.Text("Crear", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun ColorCircle(color: Color) {
    Box(modifier = Modifier.size(24.dp).background(color, RoundedCornerShape(12.dp)))
}
