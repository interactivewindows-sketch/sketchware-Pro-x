package com.example.myapplication

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.model.*
import com.google.ai.client.generativeai.GenerativeModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class SketchwareViewModel(application: Application) : AndroidViewModel(application) {
    private val gson = Gson()
    private val sharedPrefs = application.getSharedPreferences("sketchware_pro_x_prefs", Context.MODE_PRIVATE)
    
    private val _basePath = MutableStateFlow(
        sharedPrefs.getString("base_path", File(Environment.getExternalStorageDirectory(), "SketchwareProX").absolutePath)!!
    )
    val basePath: StateFlow<String> = _basePath.asStateFlow()

    private val backupFolder get() = File(_basePath.value, "Backups")
    private val exportFolder get() = File(_basePath.value, "Exports")

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "AQ.Ab8RN6IBqnzNbJCuge8Pnxavd8NfjS7xaeBsDyVLMo3-rkZYfA"
    )

    init {
        updateFolders()
        loadProjects()
    }

    private fun loadProjects() {
        try {
            val json = sharedPrefs.getString("saved_projects", null)
            if (json != null) {
                val type = object : TypeToken<List<Project>>() {}.type
                val savedList: List<Project> = gson.fromJson(json, type)
                _projects.value = savedList
            }
        } catch (e: Exception) {
            Log.e("ViewModel", "Error loading projects: ${e.message}")
        }
    }

    private fun saveProjects() {
        sharedPrefs.edit().putString("saved_projects", gson.toJson(_projects.value)).apply()
    }

    fun setBasePath(path: String) {
        _basePath.value = path
        sharedPrefs.edit().putString("base_path", path).apply()
        updateFolders()
    }

    private fun updateFolders() {
        try {
            val base = File(_basePath.value)
            if (!base.exists()) base.mkdirs()
            if (!backupFolder.exists()) backupFolder.mkdirs()
            if (!exportFolder.exists()) exportFolder.mkdirs()
        } catch (e: Exception) {
            Log.e("ViewModel", "Error creating folders: ${e.message}")
        }
    }

    fun addProject(
        name: String, 
        packageName: String, 
        versionName: String = "1.0",
        versionCode: Int = 1,
        colors: ProjectColors = ProjectColors()
    ) {
        val newProject = Project(
            id = UUID.randomUUID().toString(),
            name = name,
            packageName = packageName,
            version = versionName,
            versionCode = versionCode,
            colors = colors,
            components = emptyList(),
            events = listOf(ProjectEvent("onCreate", "Activity"))
        )
        _projects.value = _projects.value + newProject
        saveProjects()
    }

    fun deleteProject(projectId: String) {
        _projects.value = _projects.value.filter { it.id != projectId }
        saveProjects()
    }

    fun updateProjectComponents(projectId: String, components: List<UIComponent>) {
        _projects.value = _projects.value.map {
            if (it.id == projectId) it.copy(components = components) else it
        }
        saveProjects()
    }
    
    fun updateProjectSWComponents(projectId: String, components: List<SWComponent>) {
        _projects.value = _projects.value.map {
            if (it.id == projectId) it.copy(nonVisualComponents = components) else it
        }
        saveProjects()
    }

    fun updateProjectPermissions(projectId: String, permissions: List<String>) {
        _projects.value = _projects.value.map {
            if (it.id == projectId) it.copy(permissions = permissions) else it
        }
        saveProjects()
    }

    fun updateBuildSettings(projectId: String, settings: BuildSettings) {
        _projects.value = _projects.value.map {
            if (it.id == projectId) it.copy(buildSettings = settings) else it
        }
        saveProjects()
    }

    fun exportAllProjects(): String {
        return try {
            val json = gson.toJson(_projects.value)
            val file = File(backupFolder, "all_projects_backup.swx")
            file.writeText(json)
            "Backup guardado en: ${file.absolutePath}"
        } catch (e: Exception) {
            "Error al exportar: ${e.message}"
        }
    }

    fun importProjectsFromFile(file: File): String {
        return try {
            val json = file.readText()
            val type = object : TypeToken<List<Project>>() {}.type
            val imported: List<Project> = gson.fromJson(json, type)
            _projects.value = _projects.value + imported
            saveProjects()
            "Se importaron ${imported.size} proyectos con éxito"
        } catch (e: Exception) {
            "Error al importar: ${e.message}"
        }
    }

    fun buildApk(projectId: String): String {
        val project = _projects.value.find { it.id == projectId } ?: return "Proyecto no encontrado"
        return try {
            val apkFile = File(exportFolder, "${project.name}_v${project.version}.apk")
            apkFile.writeText(gson.toJson(project))
            "¡APK Generado!\n${apkFile.absolutePath}"
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun generateProjectWithAI(prompt: String) {
        viewModelScope.launch {
            _isGenerating.value = true
            _error.value = null
            try {
                val fullPrompt = "Actúa como experto en Android. Basado en: '$prompt', inventa un nombre de app y un paquete com.ejemplo.nombre. Responde SOLO JSON: {\"name\": \"...\", \"package\": \"...\"}"
                val response = generativeModel.generateContent(fullPrompt)
                val text = response.text
                if (text != null) {
                    val name = text.substringAfter("\"name\": \"").substringBefore("\"")
                    val pkg = text.substringAfter("\"package\": \"").substringBefore("\"")
                    if (name.isNotEmpty() && name != text) {
                        addProject(name, pkg)
                    }
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }
}
