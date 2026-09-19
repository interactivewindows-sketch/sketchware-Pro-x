<div align="center">
  <h1>Sketchware Pro X</h1>
  <p><strong>Prototipado visual de proyectos Android con Jetpack Compose y MIUIX</strong></p>
  <p>Una experiencia inspirada en Sketchware para crear, organizar y explorar proyectos Android desde el dispositivo.</p>
</div>

<p align="center">
  <!-- Coloca aquí docs/images/app-icon.png cuando proporciones el icono oficial. -->
  <img src="docs/images/app-icon.png" width="120" alt="Icono de Sketchware Pro X">
</p>

<p align="center">
  <em>Las imágenes del README son marcadores de posición. Sustituye los archivos indicados por capturas propias del proyecto.</em>
</p>

---

## Presentación

Sketchware Pro X es una aplicación Android construida con Kotlin, Jetpack Compose y MIUIX. Su objetivo actual es ofrecer un espacio de prototipado visual y gestión local de proyectos, manteniendo una interfaz inspirada en Sketchware.

El proyecto combina una interfaz visual, persistencia local, importación y exportación de proyectos `.swx`, reproducción multimedia y herramientas opcionales de asistencia con IA.

## Vista previa

> **Cómo usar esta sección:** coloca tus capturas en `docs/images/` usando exactamente los nombres indicados. No se incluyen imágenes externas ni capturas generadas automáticamente.

<table>
  <tr>
    <td align="center" width="50%">
      <img src="docs/images/home-screen.png" alt="Captura pendiente: pantalla principal" width="360">
      <br><sub><strong>Pantalla principal</strong><br><code>docs/images/home-screen.png</code></sub>
    </td>
    <td align="center" width="50%">
      <img src="docs/images/project-editor.png" alt="Captura pendiente: editor de proyectos" width="360">
      <br><sub><strong>Editor de proyectos</strong><br><code>docs/images/project-editor.png</code></sub>
    </td>
  </tr>
  <tr>
    <td align="center" width="50%">
      <img src="docs/images/miuix-hyperos-interface.png" alt="Captura pendiente: interfaz inspirada en HyperOS y MIUIX" width="360">
      <br><sub><strong>Interfaz inspirada en HyperOS/MIUIX</strong><br><code>docs/images/miuix-hyperos-interface.png</code></sub>
    </td>
    <td align="center" width="50%">
      <img src="docs/images/project-setup-ai.png" alt="Captura pendiente: generación o configuración de proyectos" width="360">
      <br><sub><strong>Generación o configuración de proyectos</strong><br><code>docs/images/project-setup-ai.png</code></sub>
    </td>
  </tr>
</table>

### Otras pantallas

Puedes añadir capturas adicionales en la misma carpeta. Estas rutas quedan preparadas para futuras imágenes:

- `docs/images/welcome-screen.png` — pantalla de bienvenida.
- `docs/images/settings-screen.png` — ajustes y almacenamiento.
- `docs/images/about-screen.png` — pantalla de información.
- `docs/images/file-manager.png` — gestor de archivos.
- `docs/images/project-preview.png` — vista previa del proyecto.

Ejemplo para añadir una nueva imagen:

```markdown
<p align="center">
  <img src="docs/images/settings-screen.png" width="360" alt="Pantalla de ajustes de Sketchware Pro X">
</p>
```

## Funcionalidades actuales

- Gestión local de proyectos.
- Editor visual de componentes de UI.
- Componentes no visuales para prototipado.
- Persistencia local de proyectos.
- Importación y exportación de proyectos en formato `.swx`.
- Copias de seguridad desde la pantalla de ajustes.
- Navegación de archivos y selección de carpetas.
- Pantalla de bienvenida, pantalla de información y configuración visual.

## Funciones opcionales y experimentales

Estas funciones existen en el código, pero no deben interpretarse como características terminadas o equivalentes a un flujo de producción:

- **Gemini:** generación opcional de nombres y paquetes a partir de un prompt. Requiere configurar `GEMINI_API_KEY` localmente y una conexión de red.
- **Inferencia local con Gemma:** integración experimental que requiere que el usuario proporcione un modelo local compatible.
- **Build APK:** la acción actual guarda una representación serializada del proyecto con extensión `.apk`; no genera un APK Android instalable.
- **Código del proyecto:** el editor muestra una representación de prototipo; no genera necesariamente un proyecto Kotlin/Compose completo y compilable.

## Stack técnico

- Kotlin
- Jetpack Compose
- AndroidX Navigation
- MIUIX UI / Theme
- Media3 para vídeo
- Coil para imágenes
- Gson para serialización
- Google Generative AI para la integración opcional de Gemini
- MediaPipe GenAI para la integración experimental local

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
│   ├── images/
│   │   └── README.md
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

- Android Studio con soporte para el Android Gradle Plugin del proyecto.
- JDK 17 para el toolchain de compilación.
- Android SDK 34.
- Conexión a Internet para descargar dependencias y usar Gemini.

## Configuración rápida

1. Clona el repositorio.
2. Abre el proyecto en Android Studio.
3. Configura el Gradle JDK como JDK 17.
4. Si vas a usar Gemini, crea `local.properties` en la raíz del repositorio y añade tu configuración local:

```properties
sdk.dir=/ruta/a/tu/Android/Sdk
GEMINI_API_KEY=tu_clave_local
```

5. Sincroniza Gradle y ejecuta la aplicación.

`local.properties` está excluido por Git. No añadas claves reales a archivos versionados. Consulta [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md) para conocer la configuración segura y las limitaciones de las claves incluidas en una aplicación distribuida.

## Limitaciones conocidas

- Gemini es opcional y requiere una clave válida configurada localmente.
- La integración Gemma local es experimental y requiere un modelo proporcionado por el usuario.
- La acción “Build APK” no produce actualmente un APK instalable.
- No todos los componentes del editor generan código Android completo.
- La importación y exportación dependen de los permisos y del sistema de archivos del dispositivo Android.

## Documentación

- [`docs/PROJECT_STRUCTURE.md`](docs/PROJECT_STRUCTURE.md): arquitectura y organización del proyecto.
- [`docs/DEVELOPMENT.md`](docs/DEVELOPMENT.md): configuración, seguridad y flujo de desarrollo.
- [`docs/images/README.md`](docs/images/README.md): inventario de capturas que puedes proporcionar.

## Licencia

Este proyecto se distribuye bajo la licencia Apache 2.0.

## Créditos

Agradecimientos a la comunidad de Sketchware, MIUIX y al ecosistema Android que ha inspirado esta aplicación.

---

<div align="center">
  <sub>Proyecto mantenido por Interactive Proyects.</sub>
</div>
