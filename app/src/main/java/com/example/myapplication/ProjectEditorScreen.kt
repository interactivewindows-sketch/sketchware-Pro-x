package com.example.myapplication

import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewQuilt
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import com.example.myapplication.model.*
import com.example.myapplication.ui.theme.MiuiBlue
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.util.UUID

@Composable
fun ProjectEditorScreen(
    projectId: String,
    viewModel: SketchwareViewModel = viewModel(),
    onBack: () -> Unit
) {
    val projects by viewModel.projects.collectAsState()
    val project = projects.find { it.id == projectId } ?: return
    
    val uiComponents = remember { mutableStateListOf<UIComponent>().apply { addAll(project.components) } }
    val swComponents = remember { mutableStateListOf<SWComponent>().apply { addAll(project.nonVisualComponents) } }
    var selectedTab by remember { mutableIntStateOf(0) }
    var isPreviewMode by remember { mutableStateOf(false) }
    var showAddComponentDialog by remember { mutableStateOf(false) }
    var showBuildSettingsDialog by remember { mutableStateOf(false) }
    var showPermissionManager by remember { mutableStateOf(false) }
    
    val context = LocalContext.current
    val tabs = listOf("View", "Event", "Component", "Code")

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            uiComponents.add(UIComponent.ImageView(UUID.randomUUID().toString(), uri))
        }
    }

    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        uris.forEach { uri ->
            uiComponents.add(UIComponent.VideoView(UUID.randomUUID().toString(), uri))
        }
    }
    
    LaunchedEffect(uiComponents.size, swComponents.size) {
        viewModel.updateProjectComponents(projectId, uiComponents.toList())
        viewModel.updateProjectSWComponents(projectId, swComponents.toList())
    }

    if (isPreviewMode) {
        PreviewSystem(uiComponents, onExit = { isPreviewMode = false })
    } else {
        Scaffold(
            containerColor = Color.Black,
            topBar = {
                Column {
                    TopAppBar(
                        title = project.name,
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                            }
                        },
                        actions = {
                            IconButton(onClick = { showPermissionManager = true }) {
                                Icon(Icons.Default.Security, contentDescription = "Permissions", tint = Color.White)
                            }
                            IconButton(onClick = { showBuildSettingsDialog = true }) {
                                Icon(Icons.Default.Tune, contentDescription = "Settings", tint = Color.White)
                            }
                            IconButton(onClick = { 
                                val msg = viewModel.buildApk(projectId)
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }) {
                                Icon(Icons.Default.Build, contentDescription = "Build APK", tint = MiuiBlue)
                            }
                            IconButton(onClick = { isPreviewMode = true }) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "Run", tint = Color.Green)
                            }
                        }
                    )
                    TabRow(
                        tabs = tabs,
                        selectedTabIndex = selectedTab,
                        onTabSelected = { selectedTab = it }
                    )
                }
            },
            floatingActionButton = {
                if (selectedTab == 2) {
                    androidx.compose.material3.FloatingActionButton(onClick = { showAddComponentDialog = true }, containerColor = MiuiBlue) {
                        Icon(Icons.Default.Add, contentDescription = "Add Component", tint = Color.White)
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize().background(Color.Black)) {
                when (selectedTab) {
                    0 -> DesignView(
                        components = uiComponents,
                        onAddImage = { galleryLauncher.launch("image/*") },
                        onAddVideo = { videoLauncher.launch("video/*") }
                    )
                    1 -> EventListView(project.events)
                    2 -> ComponentListView(swComponents)
                    3 -> CodeView(uiComponents)
                }
            }
        }
        
        if (showAddComponentDialog) {
            AddComponentDialog(onDismiss = { showAddComponentDialog = false }) { type ->
                swComponents.add(createComponentByType(type, swComponents.size))
                showAddComponentDialog = false
            }
        }
        
        if (showBuildSettingsDialog) {
            BuildSettingsDialog(project.buildSettings, onDismiss = { showBuildSettingsDialog = false }) { newSettings ->
                viewModel.updateBuildSettings(projectId, newSettings)
                showBuildSettingsDialog = false
            }
        }
        
        if (showPermissionManager) {
            PermissionManagerDialog(project.permissions, onDismiss = { showPermissionManager = false }) { newPerms ->
                viewModel.updateProjectPermissions(projectId, newPerms)
                showPermissionManager = false
            }
        }
    }
}

@Composable
fun DesignView(
    components: MutableList<UIComponent>,
    onAddImage: () -> Unit,
    onAddVideo: () -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .width(140.dp)
                .fillMaxHeight()
                .background(Color(0xFF111111))
                .verticalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            PaletteCategory("Layouts")
            PaletteItem("Linear(H)", Icons.Default.ViewStream) { components.add(UIComponent.LinearH(UUID.randomUUID().toString())) }
            PaletteItem("Linear(V)", Icons.Default.ViewWeek) { components.add(UIComponent.LinearV(UUID.randomUUID().toString())) }
            PaletteItem("Scroll(H)", Icons.Default.SwapHoriz) { components.add(UIComponent.ScrollH(UUID.randomUUID().toString())) }
            PaletteItem("Scroll(V)", Icons.Default.SwapVert) { components.add(UIComponent.ScrollV(UUID.randomUUID().toString())) }
            
            PaletteCategory("Widgets")
            PaletteItem("TextView", Icons.Default.TextFields) { components.add(UIComponent.TextView(UUID.randomUUID().toString(), "TextView")) }
            PaletteItem("Button", Icons.Default.SmartButton) { components.add(UIComponent.Button(UUID.randomUUID().toString(), "Button")) }
            PaletteItem("Input", Icons.Default.Input) { components.add(UIComponent.EditText(UUID.randomUUID().toString(), "Escribe...")) }
            PaletteItem("ImageView", Icons.Default.Image) { onAddImage() }
            PaletteItem("Video+", Icons.Default.VideoLibrary) { onAddVideo() }
            PaletteItem("WebView", Icons.Default.Language) { components.add(UIComponent.WebView(UUID.randomUUID().toString(), "https://google.com")) }
            
            PaletteCategory("AndroidX")
            PaletteItem("CardView", Icons.Default.CropSquare) { components.add(UIComponent.CardView(UUID.randomUUID().toString())) }
            PaletteItem("TabLayout", Icons.Default.Tab) { components.add(UIComponent.TabLayout(UUID.randomUUID().toString())) }
        }

        Box(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
            Card(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    itemsIndexed(components) { index, component ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            ComponentRenderer(component)
                            Row(modifier = Modifier.align(Alignment.TopEnd).padding(4.dp)) {
                                if (index > 0) IconButton(onClick = { 
                                    val item = components.removeAt(index)
                                    components.add(index - 1, item)
                                }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) }
                                if (index < components.size - 1) IconButton(onClick = {
                                    val item = components.removeAt(index)
                                    components.add(index + 1, item)
                                }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp)) }
                                IconButton(onClick = { components.removeAt(index) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red.copy(alpha = 0.5f), modifier = Modifier.size(16.dp)) }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PaletteCategory(name: String) {
    Text(name, color = Color.Gray, style = MiuixTheme.textStyles.footnote1, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
fun EventListView(events: List<ProjectEvent>) {
    var selectedCategory by remember { mutableStateOf("Activity") }
    val filteredEvents = events.filter { it.category == selectedCategory }

    Row(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.width(100.dp).fillMaxHeight().background(Color(0xFF111111))) {
            EventCategoryItem("Activity", Icons.Default.Autorenew, selectedCategory == "Activity") { selectedCategory = "Activity" }
            EventCategoryItem("View", Icons.AutoMirrored.Filled.ViewQuilt, selectedCategory == "View") { selectedCategory = "View" }
            EventCategoryItem("Component", Icons.Default.Extension, selectedCategory == "Component") { selectedCategory = "Component" }
            EventCategoryItem("Drawer", Icons.Default.Menu, selectedCategory == "Drawer") { selectedCategory = "Drawer" }
        }
        Column(modifier = Modifier.weight(1f).fillMaxHeight().padding(16.dp)) {
            if (filteredEvents.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No events to display in $selectedCategory", color = Color.Gray)
                }
            } else {
                LazyColumn {
                    items(filteredEvents) { event ->
                        Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Code, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(event.name, color = Color.White, style = MiuixTheme.textStyles.body1)
                                    Text("On activity create", color = Color.Gray, style = MiuixTheme.textStyles.footnote2)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EventCategoryItem(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(40.dp).background(if (selected) MiuiBlue.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) MiuiBlue else Color.Gray, modifier = Modifier.size(24.dp))
        }
        Text(name, color = if (selected) Color.White else Color.Gray, fontSize = 10.sp)
    }
}

@Composable
fun ComponentListView(components: List<SWComponent>) {
    if (components.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No components to display", color = Color.Gray)
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(components) { comp ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(getIconForComponent(comp.type), contentDescription = null, tint = MiuiBlue)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(comp.id, color = Color.White, style = MiuixTheme.textStyles.body1)
                    }
                }
            }
        }
    }
}

@Composable
fun AddComponentDialog(onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    val components = listOf(
        "Intent", "SharedPreferences", "FilePicker", "Calendar", "Vibrator", "Timer", "Dialog", "MediaPlayer",
        "SoundPool", "ObjectAnimator", "Camera", "Gyroscope", "RequestNetwork"
    )
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().height(500.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add component", style = MiuixTheme.textStyles.title1, color = Color.White, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                LazyVerticalGrid(columns = GridCells.Fixed(4), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(components) { type ->
                        Column(
                            modifier = Modifier.clickable { onSelect(type) },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(modifier = Modifier.size(50.dp).background(Color(0xFF222222), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                Icon(getIconForComponent(type), contentDescription = null, tint = Color.White)
                            }
                            Text(type, color = Color.White, fontSize = 9.sp, maxLines = 1, textAlign = TextAlign.Center)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BuildSettingsDialog(settings: BuildSettings, onDismiss: () -> Unit, onSave: (BuildSettings) -> Unit) {
    var androidJar by remember { mutableStateOf(settings.androidJarPath) }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Build Settings", style = MiuixTheme.textStyles.title2, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                androidx.compose.material3.TextField(value = androidJar, onValueChange = { androidJar = it }, label = { androidx.compose.material3.Text("Custom android.jar") })
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss, text = "Cancel")
                    Button(onClick = { onSave(settings.copy(androidJarPath = androidJar)) }) { Text("Save", color = Color.White) }
                }
            }
        }
    }
}

@Composable
fun PermissionManagerDialog(permissions: List<String>, onDismiss: () -> Unit, onSave: (List<String>) -> Unit) {
    var currentPerms by remember { mutableStateOf(permissions.toMutableList()) }
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().height(600.dp)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Permissions Manager", style = MiuixTheme.textStyles.title2, color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(listOf("INTERNET", "READ_EXTERNAL_STORAGE", "CAMERA", "LOCATION", "BLUETOOTH", "VIBRATE")) { perm ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().clickable {
                            if (currentPerms.contains(perm)) currentPerms.remove(perm) else currentPerms.add(perm)
                        }) {
                            Checkbox(checked = currentPerms.contains(perm), onCheckedChange = {
                                if (it) currentPerms.add(perm) else currentPerms.remove(perm)
                            })
                            Text(perm, color = Color.White)
                        }
                    }
                }
                Button(onClick = { onSave(currentPerms.toList()) }) { Text("Save", color = Color.White) }
            }
        }
    }
}

fun getIconForComponent(type: String) = when(type) {
    "Intent" -> Icons.Default.Route
    "SharedPreferences" -> Icons.Default.Save
    "Timer" -> Icons.Default.Timer
    "Dialog" -> Icons.Default.ChatBubble
    "Vibrator" -> Icons.Default.Vibration
    "Camera" -> Icons.Default.CameraAlt
    "MediaPlayer" -> Icons.Default.PlayCircle
    "FilePicker" -> Icons.Default.FolderOpen
    "Calendar" -> Icons.Default.CalendarMonth
    else -> Icons.Default.Extension
}

fun createComponentByType(type: String, count: Int) = when(type) {
    "Intent" -> SWComponent.Intent("intent_$count")
    "SharedPreferences" -> SWComponent.SharedPreferences("pref_$count", "data")
    "Timer" -> SWComponent.Timer("timer_$count")
    "Dialog" -> SWComponent.Dialog("dialog_$count")
    "Vibrator" -> SWComponent.Vibrator("vibrator_$count")
    "FilePicker" -> SWComponent.FilePicker("picker_$count")
    "Calendar" -> SWComponent.Calendar("cal_$count")
    "MediaPlayer" -> SWComponent.MediaPlayer("player_$count")
    else -> SWComponent.Intent("comp_$count")
}

@Composable
fun CodeView(components: List<UIComponent>) {
    val generatedCode = remember(components) {
        val sb = StringBuilder()
        sb.append("// settings.gradle.kts\n")
        sb.append("dependencyResolutionManagement {\n")
        sb.append("    repositories {\n")
        sb.append("        google()\n")
        sb.append("        mavenCentral()\n")
        sb.append("    }\n")
        sb.append("}\n\n")
        sb.append("// build.gradle.kts\n")
        sb.append("dependencies {\n")
        sb.append("    val miuix = \"0.9.3\"\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-ui-android:\$miuix\")\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-preference-android:\$miuix\")\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-blur-android:\$miuix\")\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-icons-android:\$miuix\")\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-squircle-android:\$miuix\")\n")
        sb.append("    implementation(\"top.yukonga.miuix.kmp:miuix-navigation3-ui-android:\$miuix\")\n")
        components.forEach { comp ->
            sb.append("    // UIComponent: ${comp::class.simpleName}\n")
        }
        sb.append("}\n\n")
        sb.append("@Composable\n")
        sb.append("fun MainActivity() {\n")
        sb.append("    MiuixTheme {\n")
        sb.append("        Scaffold {\n")
        sb.append("            // UI Logic...\n")
        sb.append("        }\n")
        sb.append("    }\n")
        sb.append("}")
        sb.toString()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Text("Código del Proyecto", color = MiuiBlue, style = MiuixTheme.textStyles.title3)
        Card(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Box(modifier = Modifier.background(Color(0xFF1E1E1E)).padding(16.dp)) {
                Text(
                    text = generatedCode,
                    color = Color(0xFF9CDCFE),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ComponentRenderer(component: UIComponent) {
    val context = LocalContext.current
    Box(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        when (component) {
            is UIComponent.TextView -> Text(component.text, color = Color.White)
            is UIComponent.Button -> Button(onClick = {}) { Text(component.text, color = Color.White) }
            is UIComponent.EditText -> {
                var text by remember { mutableStateOf("") }
                androidx.compose.material3.TextField(
                    value = text, 
                    onValueChange = { text = it }, 
                    label = { androidx.compose.material3.Text(component.hint) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            is UIComponent.ImageView -> AsyncImage(model = component.imageUri, contentDescription = null, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)))
            is UIComponent.VideoView -> VideoRenderer(videoUri = component.videoUri)
            is UIComponent.WebView -> AndroidView(factory = { WebView(it).apply { webViewClient = WebViewClient(); loadUrl(component.url) } }, modifier = Modifier.fillMaxWidth().height(300.dp).clip(RoundedCornerShape(16.dp)))
            else -> Text("UI: ${component::class.simpleName}", color = Color.Gray)
        }
    }
}

@Composable
fun VideoRenderer(videoUri: Uri?) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            videoUri?.let { setMediaItem(MediaItem.fromUri(it)) }
            prepare()
        }
    }
    DisposableEffect(Unit) { onDispose { exoPlayer.release() } }
    AndroidView(factory = { PlayerView(it).apply { player = exoPlayer; useController = true } }, modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(16.dp)))
}

@Composable
fun PaletteItem(name: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    TextButton(onClick = onClick, text = name)
}

@Composable
fun PreviewSystem(components: List<UIComponent>, onExit: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF111111)).padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("HyperOS Preview", color = Color.White, modifier = Modifier.weight(1f))
                IconButton(onClick = onExit) { Icon(Icons.Default.Close, contentDescription = "Exit", tint = Color.White) }
            }
            LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                items(components) { ComponentRenderer(it) }
            }
        }
    }
}
