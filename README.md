CRM Guitars 🎸
Aplicación Android de gestión de citas para talleres de guitarras y luthiers. Desarrollada con Jetpack Compose y Firebase.
✨ Características
Para usuarios:

Registro e inicio de sesión (email/contraseña y Google)
Recuperación de contraseña por email
Visualización del calendario con días y horas disponibles
Agendado de citas con fotos del instrumento
Historial de citas con estados en tiempo real
Detalle de cada cita con seguimiento y resolución del técnico
Perfil de usuario con foto

Para el administrador:

Panel de administración diferenciado
Gestión de horarios disponibles en el calendario
Visualización de todas las citas de todos los usuarios
Cambio de estado de las citas (Pendiente, En Proceso, Finalizado)
Añadir resolución técnica a cada cita
Agenda de clientes con perfil de cada usuario

🛠 Tecnologías

Jetpack Compose — UI moderna declarativa
Firebase Authentication — Login y registro
Firebase Firestore — Base de datos en tiempo real
Coil — Carga de imágenes
Navigation Compose — Navegación entre pantallas
Material Icons Extended — Iconografía

📱 Pantallas

Login y Registro
Pantalla principal (Inicio)
Calendario de citas
Formulario de nueva cita
Estado y detalle de cita
Historial de citas
Perfil de usuario
Panel de administrador

⚙️ Configuración

Clona el repositorio
Crea un proyecto en Firebase Console
Activa Authentication (Email/Contraseña y Google)
Activa Firestore Database
Descarga el archivo google-services.json y colócalo en la carpeta app/
Abre el proyecto en Android Studio y sincroniza Gradle
Ejecuta la app

👤 Usuario administrador
Para crear un usuario administrador:

Regístralo en Firebase Authentication
En Firestore, en la colección usuarios, crea un documento con su UID y añade el campo esAdmin: true

📋 Estructura de Firestore
usuarios/
  {uid}/
    nombre, email, telefono, esAdmin, fotoPerfil, tokenFCM

citas/
  {citaId}/
    uidUsuario, nombreUsuario, marca, modelo, motivo,
    fecha, hora, estado, resolucion, fotos, fechaCreacion, fechaFinalizacion

horarios/
  {DD-MM-YYYY}/
    horas: []
📄 Licencia
MIT License — libre para uso y modificación.
