package com.example.CRM_Guitars

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

object AuthManager {

    suspend fun recuperarContrasena(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesionConGoogle(idToken: String): Result<Unit> {
        return try {
            val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
            auth.signInWithCredential(credential).await()

            // Si es usuario nuevo, crear su documento en Firestore
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("Error"))
            val documento = db.collection("usuarios").document(uid).get().await()
            if (!documento.exists()) {
                val datosUsuario = hashMapOf(
                    "nombre" to (auth.currentUser?.displayName ?: ""),
                    "email" to (auth.currentUser?.email ?: ""),
                    "telefono" to "",
                    "esAdmin" to false
                )
                db.collection("usuarios").document(uid).set(datosUsuario).await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun obtenerTodosLosUsuarios(): List<Map<String, Any>> {
        return try {
            val resultado = db.collection("usuarios")
                .whereEqualTo("esAdmin", false)
                .get()
                .await()
            resultado.documents.mapNotNull { doc ->
                doc.data?.plus("uid" to doc.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun obtenerDatosUsuarioPorId(uid: String): Map<String, Any>? {
        return try {
            val documento = db.collection("usuarios").document(uid).get().await()
            documento.data
        } catch (e: Exception) {
            null
        }
    }
    suspend fun guardarFotoPerfil(base64: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No hay usuario"))
            db.collection("usuarios").document(uid).set(
                mapOf("fotoPerfil" to base64),
                com.google.firebase.firestore.SetOptions.merge()
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerFotoPerfil(): String? {
        return try {
            val uid = auth.currentUser?.uid ?: return null
            val documento = db.collection("usuarios").document(uid).get().await()
            documento.getString("fotoPerfil")
        } catch (e: Exception) {
            null
        }
    }

    suspend fun enviarNotificacion(
        externalUserId: String,
        titulo: String,
        mensaje: String
    ): Result<Unit> {
        return try {
            val url = java.net.URL("https://onesignal.com/api/v1/notifications")
            val connection = url.openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.setRequestProperty("Authorization", "Basic 6c7f6bf1-8c2a-4ca9-a9f6-cd43f17b19df")
            connection.doOutput = true

            val body = """
            {
                "app_id": "6c7f6bf1-8c2a-4ca9-a9f6-cd43f17b19df",
                "include_aliases": {"external_id": ["$externalUserId"]},
                "target_channel": "push",
                "headings": {"en": "$titulo"},
                "contents": {"en": "$mensaje"}
            }
        """.trimIndent()

            connection.outputStream.write(body.toByteArray())
            connection.responseCode
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    suspend fun obtenerDiasDisponiblesDelMes(anio: Int, mes: Int): List<String> {
        return try {
            val resultado = db.collection("horarios").get().await()
            resultado.documents
                .mapNotNull { it.id }
                .filter { fecha ->
                    val partes = fecha.split("-")
                    partes.size == 3 && partes[2].toIntOrNull() == anio && partes[1].toIntOrNull() == mes
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun guardarHorasDisponibles(fecha: String, horas: List<String>): Result<Unit> {
        return try {
            db.collection("horarios").document(fecha).set(
                mapOf("horas" to horas)
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerHorasDisponibles(fecha: String): List<String> {
        return try {
            val documento = db.collection("horarios").document(fecha).get().await()
            @Suppress("UNCHECKED_CAST")
            documento.get("horas") as? List<String> ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun actualizarCita(
        citaId: String,
        nuevoEstado: String,
        nuevaResolucion: String
    ): Result<Unit> {
        return try {
            val datosActualizados = mutableMapOf<String, Any>(
                "estado" to nuevoEstado,
                "resolucion" to nuevaResolucion
            )
            if (nuevoEstado == "FINALIZADO") {
                val fechaHoy = java.time.LocalDate.now()
                val formato = "${fechaHoy.dayOfMonth.toString().padStart(2, '0')}-${fechaHoy.monthValue.toString().padStart(2, '0')}-${fechaHoy.year}"
                datosActualizados["fechaFinalizacion"] = formato
            }
            db.collection("citas").document(citaId).update(datosActualizados).await()
            if (nuevoEstado == "FINALIZADO") {
                val cita = db.collection("citas").document(citaId).get().await()
                val uidUsuario = cita.getString("uidUsuario") ?: ""
                if (uidUsuario.isNotEmpty()) {

                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun obtenerTodasLasCitas(): List<Map<String, Any>> {
        return try {
            val resultado = db.collection("citas")
                .orderBy("fechaCreacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.data?.plus("id" to it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun crearCita(
        marca: String,
        modelo: String,
        motivo: String,
        fecha: String,
        hora: String,
        fotosBase64: List<String> = emptyList()
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid ?: return Result.failure(Exception("No hay usuario autenticado"))
            val datosUsuario = obtenerDatosUsuario()
            val nombreUsuario = datosUsuario?.get("nombre") as? String ?: ""

            val cita = hashMapOf(
                "uidUsuario" to uid,
                "nombreUsuario" to nombreUsuario,
                "marca" to marca,
                "modelo" to modelo,
                "motivo" to motivo,
                "fecha" to fecha,
                "hora" to hora,
                "estado" to "Pendiente",
                "resolucion" to "",
                "fechaCreacion" to System.currentTimeMillis(),
                "fotos" to fotosBase64
            )

            db.collection("citas").add(cita).await()
            // Notificar al admin
            val adminUid = db.collection("usuarios")
                .whereEqualTo("esAdmin", true)
                .get()
                .await()
                .documents
                .firstOrNull()
                ?.id ?: ""

            if (adminUid.isNotEmpty()) {
                enviarNotificacion(
                    externalUserId = adminUid,
                    titulo = "Nueva cita agendada",
                    mensaje = "$nombreUsuario ha agendado una cita para el $fecha a las $hora"
                )
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun obtenerCitasUsuario(): List<Map<String, Any>> {
        val uid = auth.currentUser?.uid ?: return emptyList()
        return try {
            val resultado = db.collection("citas")
                .whereEqualTo("uidUsuario", uid)
                .orderBy("fechaCreacion", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            resultado.documents.mapNotNull { it.data?.plus("id" to it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }
    suspend fun obtenerDatosUsuario(): Map<String, Any>? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val documento = db.collection("usuarios").document(uid).get().await()
            documento.data
        } catch (e: Exception) {
            null
        }
    }
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun registrarUsuario(
        nombre: String,
        telefono: String,
        email: String,
        password: String
    ): Result<Unit> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = resultado.user?.uid ?: return Result.failure(Exception("Error al crear usuario"))

            val datosUsuario = hashMapOf(
                "nombre" to nombre,
                "telefono" to telefono,
                "email" to email,
                "esAdmin" to false
            )
            db.collection("usuarios").document(uid).set(datosUsuario).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun iniciarSesion(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    suspend fun esUsuarioAdmin(): Boolean {
        val datos = obtenerDatosUsuario()
        return datos?.get("esAdmin") as? Boolean ?: false
    }
    fun cerrarSesion() {
        auth.signOut()
    }

    fun usuarioActual() = auth.currentUser
}

