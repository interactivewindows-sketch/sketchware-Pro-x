package com.example.myapplication

import android.content.Context
import android.os.Environment
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class LocalAIManager(private val context: Context) {
    private var llmInference: LlmInference? = null
    
    // Carpeta visible para el usuario en Documentos/gemma_models
    private val modelDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "gemma_models")
    private val modelPath = File(modelDir, "gemma.bin").absolutePath

    fun isModelDownloaded(): Boolean {
        return File(modelPath).exists()
    }

    fun createModelFolder() {
        if (!modelDir.exists()) {
            modelDir.mkdirs()
        }
    }

    fun getModelPathForUser(): String = modelPath

    private fun setupInference() {
        if (llmInference == null && isModelDownloaded()) {
            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(modelPath)
                .setMaxTokens(512)
                .build()
            llmInference = LlmInference.createFromOptions(context, options)
        }
    }

    suspend fun generateResponse(prompt: String): String = withContext(Dispatchers.IO) {
        setupInference()
        val inference = llmInference ?: return@withContext "Error: No se encontró el modelo en: $modelPath. \n\nPor favor, descarga el archivo gemma.bin y colócalo en esa carpeta."
        
        try {
            val fullPrompt = "Instructions: Act as an Android expert. Based on: '$prompt', suggest an app name and package name. Format: JSON {\"name\": \"...\", \"package\": \"...\"}\nResponse:"
            inference.generateResponse(fullPrompt)
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }
}
