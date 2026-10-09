# 📱 Semana 7 — Prototipo 2

Proyecto de Programación Android desarrollado en Java y XML, tomando como base el ejemplo de la Semana 7 trabajado en clases.

La aplicación incluye 5 intents implícitos y 3 explícitos. También mantiene las funciones de ubicación y linterna del proyecto original.

## 🛠️ Versiones utilizadas

- Lenguaje: Java.
- Interfaces: XML.
- Paquete: com.devst.semana7.
- Android mínimo: Android 12, API 31.
- targetSdk: 36.
- compileSdk: 36.1.
- Android Gradle Plugin: 9.0.1.
- Gradle Wrapper: 9.2.1.
- Compatibilidad del código: Java 11.
- Las versiones de las librerías están en gradle/libs.versions.toml.

## 🚀 Cómo ejecutar el proyecto

1. Descargar y descomprimir el proyecto.
2. Abrir la carpeta Semana7 desde Android Studio.
3. Esperar que termine la sincronización de Gradle.
4. Instalar el SDK solicitado si hace falta.
5. Seleccionar un emulador o teléfono con Android 12 o superior.
6. Presionar Run para iniciar la aplicación.

El proyecto se compiló y se ejecutó en Android Studio. Se revisó su funcionamiento en el emulador.

Para probar la linterna se necesita un dispositivo con flash.

### Generar el APK de depuración

En la terminal del proyecto, ejecutar:

Windows:
`gradlew.bat assembleDebug`

Linux o macOS:
`./gradlew assembleDebug`

El APK se genera en:
`app/build/outputs/apk/debug/app-debug.apk`

## 🔀 Intents implementados

### Implícitos

Android busca una aplicación que pueda realizar la acción solicitada.

| Función | Acción utilizada | Cómo probarla |
| --- | --- | --- |
| Mapa | ACTION_VIEW con geo: | Abrir el mapa con la ubicación obtenida. Si no hay ubicación, muestra Santiago como ejemplo. |
| Página web | ACTION_VIEW con https: | Presionar el botón para abrir el sitio de Santo Tomás. |
| Teléfono | ACTION_DIAL con tel: | Abrir el marcador con el número configurado. No realiza la llamada automáticamente. |
| Correo | ACTION_SENDTO con mailto: | Abrir una aplicación de correo con un borrador preparado. |
| Galería | ActivityResultContracts.GetContent | Seleccionar una imagen y comprobar que aparece en la pantalla principal. |

### Explícitos

En estos intents se indica directamente la clase de la pantalla que se quiere abrir.

| Pantalla | Clase | Cómo probarla |
| --- | --- | --- |
| Segunda Vista | SegundaActivity | Comprobar que recibe el nombre y la ubicación desde la pantalla principal. |
| Ajustes | ConfigActivity | Guardar los datos y comprobar que la aplicación los utiliza. |
| Ayuda | AyudaActivity | Abrir la guía y regresar con el botón Volver. |

## ✅ Validaciones

- El nombre no puede quedar vacío y permite hasta 60 caracteres.
- La dirección web debe ser válida y comenzar con HTTPS.
- El teléfono permite entre 3 y 15 dígitos y un signo + al inicio.
- El correo debe tener un formato válido.
- Si no existe una aplicación para abrir un intent, se muestra un mensaje.
- Si se cancela la selección de una imagen, se conserva la anterior.
- La ubicación solicita permisos y tiene un tiempo máximo de espera de 25 segundos.
- La linterna comprueba que exista una cámara con flash y se apaga al salir de la pantalla.
- Segunda Vista usa valores por defecto si no recibe los datos esperados.

Los ajustes se guardan con SharedPreferences.

## 🧵 Uso de hilos

La imagen seleccionada se carga con ExecutorService en un hilo de trabajo.

Después, Handler actualiza la imagen en el hilo principal. Esto ayuda a que la interfaz siga respondiendo mientras se procesa la imagen.

El hilo de trabajo se cierra cuando se destruye la actividad.

## 🖼️ Capturas

### Pantalla principal
![Pantalla principal](docs/capturas/01-inicio.png)

### Segunda Vista
![Segunda Vista](docs/capturas/02-detalle.png)

### Imagen seleccionada
![Imagen seleccionada desde la galería](docs/capturas/03-galeria.png)

### Validación en Ajustes
![Validación del nombre](docs/capturas/04-ajustes.png)

## 📁 Archivos principales

- MainActivity: botones, intents implícitos, navegación, ubicación y linterna.
- SegundaActivity: muestra los datos recibidos mediante putExtra.
- ConfigActivity: valida y guarda los ajustes.
- AyudaActivity: muestra las instrucciones de uso.
- BaseActivity: reúne el comportamiento común de las pantallas internas.
- res/layout: contiene los diseños XML.

## 🐙 Ramas del proyecto

- main: versión integrada del proyecto.
- feature/intents: desarrollo de los intents.

## 📚 Referencias

- Proyecto y material de la Semana 7 del aula virtual.
- [Intents en Android](https://developer.android.com/guide/components/intents-filters)
- [Resultados de actividades](https://developer.android.com/training/basics/intents/result)
- [Permisos de ubicación](https://developer.android.com/develop/sensors-and-location/location/permissions/runtime)

## 👤 Estudiante

Christian Rioseco.