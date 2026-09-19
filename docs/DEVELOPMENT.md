# Guía de desarrollo

## Prerrequisitos

- Android Studio con soporte para Compose
- JDK 17
- Android SDK 34
- Un archivo `local.properties` con la clave Gemini cuando se quiera usar la generación con IA

## Variables sensibles

No se deben dejar API keys ni secretos en el repositorio. La configuración recomendada es:

```properties
GEMINI_API_KEY=tu_clave_gemini
```

El valor se usa desde Gradle y queda disponible en `BuildConfig` para la app. No se recomienda versionar `local.properties`.

## Flujo de trabajo recomendado

1. Crear una rama desde la rama principal.
2. Trabajar en cambios pequeños y bien descritos.
3. Mantener compatibilidad con la navegación y la lógica actuales.
4. Verificar que la app sigue arrancando correctamente.
5. Documentar cambios importantes en la estructura o en la configuración.

## Buenas prácticas

- Reutilizar la lógica centralizada en `SketchwareViewModel`.
- Mantener nombres claros para pantallas, componentes y funciones.
- Usar `BuildConfig` para valores que no deben quedar hardcodeados.
- Evitar estados forzados que rompan la experiencia del usuario, como pantallas de bienvenida permanentes.

## Problemas frecuentes detectados

- Claves hardcodeadas en código fuente.
- Primer arranque forzado en `MainActivity`.
- `compileSdk` y `targetSdk` no alineados con la versión recomendada para Android.
- Nombre base del proyecto genérico en Gradle.

## Verificación antes de abrir un PR

- Revisar que no se eliminan funciones existentes.
- Confirmar que README y documentación reflejan el estado real.
- Comprobar que no se han introducido secretos en ficheros versionados.
- Revisar el diff final antes de fusionar.
