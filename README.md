# Sketchware Pro X

Sketchware Pro X es una aplicación Android construida con Jetpack Compose y MIUIX para crear prototipos, gestionar proyectos y explorar una experiencia visual inspirada en Sketchware.

## Visión general

La aplicación permite:

- Crear y gestionar proyectos locales
- Diseñar pantallas con componentes visuales
- Añadir componentes no visuales como Intents, timers, diálogos y almacenamiento
- Importar y exportar proyectos en formato `.swx`
- Generar ideas de app con apoyo de IA cuando se configura una clave válida de Gemini
- Explorar configuración, backup y navegación de archivos

## Stack principal

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- MIUIX UI / Theme
- Media3 (video)
- Coil (imágenes)
- Gson (serialización)
- Google Generative AI

## Estructura del proyecto

```text
.
├── app/
│   ├── src/
│   │   ├── androidTest/
│   │   ├── main/
│   │   │   ├── java/com/example/myapplication/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   └── test/
│   └── build.gradle.kts
├── docs/
│   ├── PROJECT_STRUCTURE.md
│   └── DEVELOPMENT.md
├── gradle/
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
├── LICENSE
├── README.md
├── .gitignore
└── .idea/
```

## Requisitos

- Android Studio Narwhal o posterior
- JDK 17
- Android SDK 34
- Conexión a Internet para funciones que usen IA

## Configuración rápida

1. Clona este repositorio.
2. Abre el proyecto en Android Studio.
3. Crea un archivo `local.properties` si no existe.
4. Añade una clave de Gemini para activar la IA:

```properties
GEMINI_API_KEY=tu_clave_aqui
```

5. Sincroniza Gradle y ejecuta la aplicación.

## Uso recomendado

- Empieza creando un proyecto desde la pantalla principal.
- Explora el editor visual para añadir componentes.
- Ajusta permisos y configuración de compilación desde la pantalla de ajustes.
- Usa la generación con IA para proponer nombres de app y paquetes.
- Exporta back-ups y restaura proyectos cuando lo necesites.

## Documentación adicional

- `docs/PROJECT_STRUCTURE.md`: resumen de la arquitectura y organización del proyecto.
- `docs/DEVELOPMENT.md`: práctica recomendada y flujo de trabajo.

## Limitaciones conocidas

- La generación con IA requiere una clave válida de Gemini.
- El sistema de archivos y permisos del dispositivo Android pueden afectar la importación y exportación de archivos.
- El proyecto está orientado a prototipado visual y gestión de proyectos, no a un compilador completo de APK.

## Licencia

Este proyecto se distribuye bajo la licencia Apache 2.0.

## Créditos

Agradecimientos a la comunidad de Sketchware, MIUIX y al ecosistema Android que ha inspirado esta aplicación.

---

Proyecto mantenido por Interactive Proyects.
