package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import top.yukonga.miuix.kmp.theme.ColorSchemeMode
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.ThemeController
import top.yukonga.miuix.kmp.theme.darkColorScheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val sharedPrefs = getSharedPreferences("sketchware_pro_x_prefs", Context.MODE_PRIVATE)
        val isFirstRun = sharedPrefs.getBoolean("is_first_run", true)

        setContent {
            val themeController = remember {
                ThemeController(
                    colorSchemeMode = ColorSchemeMode.Dark,
                    isDark = true,
                    darkColors = darkColorScheme(
                        primary = Color(0xFF007AFF),
                        background = Color.Transparent,
                        surface = Color(0xCC000000),
                        surfaceVariant = Color(0x66111111),
                        onBackground = Color.White,
                        onSurface = Color.White,
                        onSurfaceSecondary = Color.Gray
                    )
                )
            }

            val infiniteTransition = rememberInfiniteTransition(label = "GlobalAnim")
            val color1 by infiniteTransition.animateColor(
                initialValue = Color(0xFF1A0B2E),
                targetValue = Color(0xFF0D1B2A),
                animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
                label = "C1"
            )
            val color2 by infiniteTransition.animateColor(
                initialValue = Color(0xFF2C1B4E),
                targetValue = Color(0xFF001F3F),
                animationSpec = infiniteRepeatable(tween(12000), RepeatMode.Reverse),
                label = "C2"
            )
            val animX by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(15000), RepeatMode.Reverse),
                label = "X"
            )

            MiuixTheme(controller = themeController) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            brush = Brush.radialGradient(
                                colors = listOf(color1, color2, Color.Black),
                                center = Offset(size.width * animX, size.height / 3),
                                radius = size.width * 1.5f
                            )
                        )
                    }

                    val navController = rememberNavController()
                    val startDestination = if (isFirstRun) "welcome" else "home"

                    NavHost(navController = navController, startDestination = startDestination) {
                        composable("welcome") {
                            WelcomeScreen(onStart = {
                                sharedPrefs.edit().putBoolean("is_first_run", false).apply()
                                navController.navigate("home") {
                                    popUpTo("welcome") { inclusive = true }
                                }
                            })
                        }
                        composable("home") {
                            SketchwareScreen(
                                onProjectClick = { projectId ->
                                    navController.navigate("editor/$projectId")
                                },
                                onSettingsClick = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("editor/{projectId}") { backStackEntry ->
                            val projectId = backStackEntry.arguments?.getString("projectId") ?: ""
                            ProjectEditorScreen(
                                projectId = projectId,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            val viewModel: SketchwareViewModel = viewModel()
                            SettingsScreen(
                                onBack = { navController.popBackStack() },
                                onImportClick = { navController.navigate("file_manager/file") },
                                onChangePathClick = { navController.navigate("file_manager/folder") },
                                onAboutClick = { navController.navigate("about") },
                                viewModel = viewModel
                            )
                        }
                        composable("file_manager/{mode}") { backStackEntry ->
                            val modeStr = backStackEntry.arguments?.getString("mode") ?: "file"
                            val mode = if (modeStr == "folder") PickerMode.Folder else PickerMode.File
                            val viewModel: SketchwareViewModel = viewModel()
                            MiuixFileManager(
                                mode = mode,
                                onFileSelected = { file ->
                                    val msg = viewModel.importProjectsFromFile(file)
                                    Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                                    navController.popBackStack()
                                },
                                onFolderSelected = { folder ->
                                    viewModel.setBasePath(folder.absolutePath)
                                    Toast.makeText(this@MainActivity, "Carpeta cambiada", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                },
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("about") {
                            AboutScreen(onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
