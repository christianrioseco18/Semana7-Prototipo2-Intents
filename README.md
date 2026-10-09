# 📱 Semana 7 Prototipo 2

Aplicación académica en **Java y XML**, basada en el material de Semana 7 del aula virtual. Implementa **5 intents implícitos y 3 explícitos**. Conserva linterna y ubicación como funciones del dispositivo, que no se cuentan como intents.

## 🛠️ Entorno del proyecto
- Namespace y applicationId: `com.devst.semana7`.
- minSdk: 31 (Android 12). targetSdk: 36. compileSdk: 36.1.
- Android Gradle Plugin: 9.0.1. Gradle wrapper: 9.2.1.
- Compatibilidad del código Java: Java 11. Para Gradle, selecciona el JDK integrado compatible de Android Studio.
- Versiones de dependencias conservadas del material del aula en `gradle/libs.versions.toml`.

## 🚀 Abrir y compilar
1. Extrae el ZIP en una carpeta normal. No abras el proyecto dentro del ZIP.
2. En Android Studio usa **Open** y selecciona `Semana7`, donde está `settings.gradle.kts`.
3. Espera la sincronización. Si solicita SDK 36.1, instálalo desde SDK Manager.
4. En Settings > Build, Execution, Deployment > Build Tools > Gradle, selecciona el JDK integrado de Android Studio.
5. Ejecuta con Run en un dispositivo Android 12 o superior. Para flash, usa un teléfono con flash.
6. Genera el APK debug con `gradlew.bat assembleDebug` en Windows, o `./gradlew assembleDebug` en Linux/macOS.
7. Salida prevista: `app/build/outputs/apk/debug/app-debug.apk`.

**Estado de verificación:** se revisaron XML, IDs y declaraciones de Activities. La compilación no se pudo ejecutar en el entorno de preparación porque no había SDK ni acceso de red a Gradle. Falta compilar y ejecutar en Android Studio. Este paquete no incluye un APK nuevo verificado.

## 🔀 Intents y pasos de prueba

| Tipo | Acción | Implementación | Prueba |
|---|---|---|---|
| Implícito | Mapa | `ACTION_VIEW + geo:` | Abrir mapa; usar ubicación real si existe o Santiago como ejemplo. |
| Implícito | Web | `ACTION_VIEW + https:` | Abrir Santo Tomás o la URL guardada en Ajustes. |
| Implícito | Teléfono | `ACTION_DIAL + tel:` | Ver el número en el marcador sin llamar automáticamente. |
| Implícito | Correo | `ACTION_SENDTO + mailto:` | Ver destinatario, asunto y cuerpo en un borrador. |
| Implícito | Galería | `GetContent con image/*` | Seleccionar una imagen y comprobar que aparece en ImageView. |
| Explícito | Segunda Vista | `Intent con SegundaActivity.class` | Ver nombre y ubicación recibidos mediante extras. |
| Explícito | Ajustes | `Intent con ConfigActivity.class` | Guardar datos válidos y comprobar su uso desde el inicio. |
| Explícito | Ayuda | `Intent con AyudaActivity.class` | Leer la guía y regresar con el botón Volver. |

## ✅ Validaciones y resultados
- Ajustes: nombre obligatorio, URL HTTPS válida, teléfono de 3 a 15 dígitos con + opcional, correo válido.
- Aplicaciones externas: captura de ActivityNotFoundException y SecurityException.
- Galería: cancelación, URI nula, imagen ilegible y limitación del tamaño decodificado.
- Ubicación: permisos preciso y aproximado, proveedor disponible, tiempo máximo de 25 segundos y cancelación al salir.
- Linterna: selección de una cámara con flash, solicitud de permiso y apagado al salir.
- Segunda Vista: valores por defecto si faltan extras.
- Rotación: Bundle conserva coordenadas y URI. La URI de GetContent es acceso temporal, no una copia permanente.

## 🧵 Threads
`ExecutorService` decodifica la imagen en un hilo de trabajo. `Handler` con el Looper principal actualiza ImageView. El ejecutor termina en onDestroy y se descartan resultados de solicitudes anteriores. La ubicación usa callbacks, sin un bucle bloqueante en el hilo principal.

## 🖼️ Capturas de ejecución pendientes
Antes de entregar, agrega **mínimo 4 capturas reales** en `docs/capturas/`. Consulta `docs/capturas/INSTRUCCIONES.md`. Las capturas deben corresponder al proyecto ejecutado, no a diseños o al aula virtual. Al agregarlas, inserta aquí sus enlaces Markdown.

## 📁 Organización
- `MainActivity`: acciones externas, navegación, ubicación y linterna.
- `SegundaActivity`: detalle con datos extras.
- `ConfigActivity`: validación y persistencia de ajustes mediante SharedPreferences.
- `AyudaActivity`: guía de uso.
- `BaseActivity`: comportamiento común de vistas internas.
- `app/src/main/res/layout/`: interfaces XML.

## 🐙 GitHub
El paquete conserva historial local y ramas `main` y `feature/intents`. La autoría inicial referencia el material del aula. Sustituye la identidad de Git por la de tu cuenta antes de nuevos commits.
Publica un repositorio vacío en GitHub, sin generar un README adicional, y desde esta carpeta ejecuta:

```bash
git remote add origin https://github.com/TU_USUARIO/TU_REPOSITORIO.git
git push -u origin main
git push -u origin feature/intents
```

Agrega las capturas y cualquier corrección con commits descriptivos. Si usas GitHub Desktop, usa File > Add Local Repository y selecciona esta carpeta; publica y sube ambas ramas. Un repositorio privado requiere conceder acceso al docente. Este proyecto todavía no está publicado en GitHub.

## 📚 Referencias
- Material del aula: Semana7, Prueba 15% Android y Escala de valoración, entregados para la actividad.
- https://developer.android.com/guide/components/intents-filters
- https://developer.android.com/training/basics/intents/result
- https://developer.android.com/develop/sensors-and-location/location/permissions/runtime

## 👤 Equipo
Christian Rioseco. Agregar los demás integrantes si corresponde antes de entregar.
