package com.example.myapplication.model

import android.net.Uri

data class Project(
    val id: String,
    val name: String,
    val packageName: String,
    val version: String,
    val versionCode: Int = 1,
    val projectID: String = "601",
    val colors: ProjectColors = ProjectColors(),
    val iconUri: Uri? = null,
    val components: List<UIComponent> = emptyList(),
    val nonVisualComponents: List<SWComponent> = emptyList(),
    val events: List<ProjectEvent> = emptyList(),
    val permissions: List<String> = emptyList(),
    val buildSettings: BuildSettings = BuildSettings()
)

data class ProjectColors(
    val colorPrimary: String = "#007AFF",
    val colorPrimaryDark: String = "#0056B3",
    val colorAccent: String = "#007AFF",
    val colorControlHighlight: String = "#20007AFF"
)

data class BuildSettings(
    val androidJarPath: String = "/sdcard/android-34/android.jar",
    val dexer: String = "D8",
    val javaVersion: String = "11",
    val hideWarnings: Boolean = true,
    val includeHttpLegacy: Boolean = false,
    val enableDebugLogcat: Boolean = true
)

sealed class UIComponent {
    abstract val id: String
    
    // Layouts
    data class LinearH(override val id: String) : UIComponent()
    data class LinearV(override val id: String) : UIComponent()
    data class ScrollH(override val id: String) : UIComponent()
    data class ScrollV(override val id: String) : UIComponent()
    
    // Widgets
    data class Button(override val id: String, val text: String) : UIComponent()
    data class TextView(override val id: String, val text: String) : UIComponent()
    data class EditText(override val id: String, val hint: String) : UIComponent()
    data class ImageView(override val id: String, val imageUri: Uri? = null) : UIComponent()
    data class VideoView(override val id: String, val videoUri: Uri? = null) : UIComponent()
    data class WebView(override val id: String, val url: String) : UIComponent()
    data class ListView(override val id: String) : UIComponent()
    data class ProgressBar(override val id: String, val progress: Int = 50) : UIComponent()
    
    // AndroidX & Library
    data class CardView(override val id: String) : UIComponent()
    data class TabLayout(override val id: String) : UIComponent()
    data class LottieAnimation(override val id: String) : UIComponent()
}

sealed class SWComponent {
    abstract val id: String
    abstract val type: String
    
    data class Intent(override val id: String) : SWComponent() { override val type = "Intent" }
    data class SharedPreferences(override val id: String, val name: String) : SWComponent() { override val type = "SharedPreferences" }
    data class FilePicker(override val id: String) : SWComponent() { override val type = "FilePicker" }
    data class Calendar(override val id: String) : SWComponent() { override val type = "Calendar" }
    data class Vibrator(override val id: String) : SWComponent() { override val type = "Vibrator" }
    data class Timer(override val id: String) : SWComponent() { override val type = "Timer" }
    data class Dialog(override val id: String) : SWComponent() { override val type = "Dialog" }
    data class MediaPlayer(override val id: String) : SWComponent() { override val type = "MediaPlayer" }
    data class RequestNetwork(override val id: String) : SWComponent() { override val type = "RequestNetwork" }
}

data class ProjectEvent(
    val name: String,
    val category: String, // Activity, View, Component, Drawer, Moreblock
    val blocks: List<LogicBlock> = emptyList()
)

sealed class LogicBlock {
    data class Control(val type: String) : LogicBlock()
    data class Variable(val name: String, val value: String) : LogicBlock()
    data class ViewLogic(val componentId: String, val action: String) : LogicBlock()
}
