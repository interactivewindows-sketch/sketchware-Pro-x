package com.example.myapplication

import android.os.Build
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.random.Random

@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val deviceModel = Build.MODEL
    val androidVersion = Build.VERSION.RELEASE

    // HyperCeiler Style Background Animation - REACTIVE COLORS
    val infiniteTransition = rememberInfiniteTransition(label = "HyperOS3Anim")
    
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF6200EA), 
        targetValue = Color(0xFF00B0FF), 
        animationSpec = infiniteRepeatable(tween(5000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Color1"
    )
    
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFFD500F9), 
        targetValue = Color(0xFF00E676), 
        animationSpec = infiniteRepeatable(tween(7000, easing = LinearEasing), RepeatMode.Reverse),
        label = "Color2"
    )

    val animX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(10000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "PosX"
    )

    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(12000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "PosY"
    )

    val dynamicTextBrush = Brush.linearGradient(
        colors = listOf(color1, color2),
        start = Offset(animX * 400f, animY * 200f),
        end = Offset(800f - animX * 400f, 800f - animY * 200f)
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerX = size.width * (0.2f + animX * 0.6f)
            val centerY = size.height * (0.1f + animY * 0.5f)
            
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(color1.copy(alpha = 0.5f), color2.copy(alpha = 0.3f), Color.Black),
                    center = Offset(centerX, centerY),
                    radius = size.width * 1.8f
                )
            )
            
            repeat(500) {
                drawCircle(
                    color = color1.copy(alpha = 0.04f),
                    radius = Random.nextFloat() * 1.5f,
                    center = Offset(Random.nextFloat() * size.width, Random.nextFloat() * size.height)
                )
            }
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = "Acerca de",
                    color = Color.Transparent,
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = color1)
                        }
                    },
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Refresh, contentDescription = "Canary", tint = color2)
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item {
                        Spacer(modifier = Modifier.height(40.dp))
                        
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .background(
                                    Brush.verticalGradient(listOf(color1, color2)),
                                    RoundedCornerShape(32.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.size(60.dp)) {
                                drawRoundRect(
                                    color = Color.Black.copy(alpha = 0.4f),
                                    size = size / 1.5f,
                                    topLeft = Offset(size.width * 0.1f, size.height * 0.1f),
                                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(15f)
                                )
                                drawCircle(
                                    color = Color.Black.copy(alpha = 0.3f),
                                    radius = size.width / 4,
                                    center = Offset(size.width * 0.8f, size.height * 0.8f)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Interactive ",
                                style = TextStyle(
                                    brush = dynamicTextBrush,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    shadow = Shadow(color1.copy(alpha = 0.5f), Offset(0f, 0f), 15f)
                                )
                            )
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = color2,
                                modifier = Modifier.size(30.dp)
                            )
                            Text(
                                text = " Proyects",
                                style = TextStyle(
                                    brush = dynamicTextBrush,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    shadow = Shadow(color1.copy(alpha = 0.5f), Offset(0f, 0f), 15f)
                                )
                            )
                        }
                        
                        Text(
                            text = "2.5.153_5d1cd665b_r3671 | canary",
                            color = color1.copy(alpha = 0.7f),
                            style = MiuixTheme.textStyles.footnote1,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(modifier = Modifier.height(44.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            colors = CardDefaults.defaultColors(
                                color = color1.copy(alpha = 0.1f)
                            )
                        ) {
                            Column(modifier = Modifier.padding(24.dp)) {
                                Text(
                                    text = deviceModel,
                                    style = MiuixTheme.textStyles.title2,
                                    color = color1,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                
                                InfoRowReactive(deviceModel, "Device", color2)
                                InfoRowReactive(androidVersion, "Android version", color1)
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "OS2.0.20.0.VOBCNXM",
                                    color = color1.copy(alpha = 0.9f),
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = "OS version",
                                    color = color2.copy(alpha = 0.6f),
                                    style = MiuixTheme.textStyles.footnote2
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(modifier = Modifier.fillMaxWidth().padding(start = 24.dp)) {
                            Text(
                                text = "LICENCIA Y CRÉDITOS",
                                color = color1,
                                style = MiuixTheme.textStyles.footnote2
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                            colors = CardDefaults.defaultColors(color = Color.White.copy(alpha = 0.05f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Sketchware Pro X es una versión modificada basada en el código abierto de Sketchware Pro. Créditos totales al equipo de desarrollo original de Sketchware Pro por sus increíbles herramientas.",
                                    color = Color.White.copy(alpha = 0.8f),
                                    style = MiuixTheme.textStyles.body2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "© 2026 Interactive Proyects. Todos los derechos reservados.",
                                    color = color2,
                                    style = MiuixTheme.textStyles.footnote1,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Distribuido bajo la Licencia Apache 2.0. Sketchware es una marca comercial de sus respectivos propietarios.",
                                    color = Color.White.copy(alpha = 0.5f),
                                    style = MiuixTheme.textStyles.footnote2
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(30.dp))
                        Box(modifier = Modifier.fillMaxWidth().padding(start = 24.dp)) {
                            Text(
                                text = "DESARROLLADOR",
                                color = color1,
                                style = MiuixTheme.textStyles.footnote2
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        SocialItemCardReactive("Telegram", "Canal de Proyectos", color1) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/+f_8BfcKX4C0xMzQx"))
                            context.startActivity(intent)
                        }
                        
                        SocialItemCardReactive("TikTok", "@rebzyyx2026", color2) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.tiktok.com/@rebzyyx2026?lang=es-419"))
                            context.startActivity(intent)
                        }
                        
                        Spacer(modifier = Modifier.height(120.dp))
                    }
                }

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(100.dp)
                        .blur(40.dp)
                        .background(color1.copy(alpha = 0.15f))
                )
            }
        }
    }
}

@Composable
fun InfoRowReactive(value: String, label: String, color: Color) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(text = value, color = color.copy(alpha = 0.9f), style = MiuixTheme.textStyles.body1, fontWeight = FontWeight.Bold)
        Text(text = label, color = color.copy(alpha = 0.5f), style = MiuixTheme.textStyles.footnote2)
    }
}

@Composable
fun SocialItemCardReactive(name: String, summary: String, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.defaultColors(
            color = color.copy(alpha = 0.1f)
        )
    ) {
        Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = color, style = MiuixTheme.textStyles.body1, fontWeight = FontWeight.ExtraBold)
                Text(text = summary, color = color.copy(alpha = 0.5f), style = MiuixTheme.textStyles.footnote2)
            }
            Text("→", color = color, fontWeight = FontWeight.Bold)
        }
    }
}
