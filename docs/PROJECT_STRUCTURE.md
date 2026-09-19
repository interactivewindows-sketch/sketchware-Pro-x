# Proyecto: Sketchware Pro X

## Descripción general

Sketchware Pro X es una aplicación Android diseñada para gestionar proyectos visuales, crear prototipos y explorar una experiencia inspirada en Sketchware con componentes Compose y MIUIX.

## Capas principales

### 1. UI y navegación

- `MainActivity.kt`: entrada principal de la app y configuración de navegación.
- `WelcomeScreen.kt`: pantalla de bienvenida y vídeo de introducción.
- `SketchwareScreen.kt`: pantalla principal de proyectos y generación asistida.
- `ProjectEditorScreen.kt`: editor visual del proyecto.
- `SettingsScreen.kt`: configuración general.
- `AboutScreen.kt`: pantalla informativa del proyecto.
- `MiuixFileManager.kt`: explorador de archivos y selección de carpeta.

### 2. Modelo de datos

- `model/Project.kt`: estructuras principales como `Project`, `UIComponent`, `SWComponent`, `BuildSettings` y eventos.

### 3. Lógica y persistencia

- `SketchwareViewModel.kt`: gestión de proyectos, persistencia, importación/exportación y uso de IA.
- `LocalAIManager.kt`: soporte para inferencia local con modelos Gemma.

### 4. Recursos y configuración

- `res/`: recursos visuales, iconos y XML.
- `AndroidManifest.xml`: permisos y actividad principal.
- `gradle.properties`: configuración global de Gradle.

## Convenciones

- Mantener los nombres de paquetes actuales para evitar roturas de navegación.
- Evitar secretos en el código fuente.
- Usar `local.properties` para propiedades locales sensibles.
- Mantener cambios compatibles con la funcionalidad existente.

## Recomendaciones de contribución

- Trabaja en cambios pequeños y verificables.
- Prueba cada flujo clave antes de fusionarlo.
- Documenta cambios de estructura, permisos o configuración.
- Mantén una política clara para secretos y claves API.

## Documentación relacionada

- `README.md`: guía de uso del proyecto.
- `docs/DEVELOPMENT.md`: buenas prácticas y flujo de trabajo.
