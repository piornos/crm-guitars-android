package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

val RegBgColor = Color(0xFF131313)
val RegTopBarBg = Color(0xFF201F1F)
val RegYellow = Color(0xFFEAB308)
val RegTextLight = Color(0xFFE5E2E1)
val RegTextMuted = Color(0xFFC2C6D6)
val RegInputBg = Color(0xFF201F1F)
val RegPlaceholder = Color(0xFF6B7280)
val RegOverlay = Color(0x1AE9C349)

@Composable
fun Registro(onLoginClick: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var aceptaTerminos by remember { mutableStateOf(false) }
    var mostrarDialogo by remember { mutableStateOf(false) }
    var errorTerminos by remember { mutableStateOf(false) }
    var errorNombre by remember { mutableStateOf(false) }
    var errorTelefono by remember { mutableStateOf(false) }
    var errorEmail by remember { mutableStateOf(false) }
    var errorPassword by remember { mutableStateOf(false) }
    var errorRegistro by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    if (mostrarDialogo) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Registro exitoso!", color = Color(0xFFE5E2E1)) },
            text = { Text("Tu cuenta ha sido creada correctamente.", color = Color(0xFFC2C6D6)) },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogo = false
                        onLoginClick()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    Text("Iniciar sesión", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF201F1F)
        )
    }
    fun esEmailValido(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(RegBgColor)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(RegTopBarBg)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CRM Guitars",
                color = RegYellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 40.dp, vertical = 16.dp)
        ) {
            // HERO SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(RegTopBarBg, RoundedCornerShape(12.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(RegOverlay, RoundedCornerShape(12.dp))
                )
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Crear Cuenta",
                        color = RegTextLight,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Únete a nuestra comunidad exclusiva de músicos.",
                        color = RegTextMuted,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // CAMPO: Nombre Completo
            Text("Nombre Completo", color = RegTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it; errorNombre = false },
                placeholder = { Text("Tu nombre y apellidos", color = RegPlaceholder) },
                singleLine = true,
                isError = errorNombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFE5E2E1),
                    unfocusedTextColor = Color(0xFFE5E2E1),
                    focusedBorderColor = Color(0xFFFACC15),
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color(0xFFEA4335),
                    cursorColor = Color(0xFFFACC15),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            if (errorNombre) {
                Text("El nombre es obligatorio", color = Color(0xFFEA4335), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: Teléfono
            Text("Teléfono", color = RegTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = telefono,
                onValueChange = { telefono = it },
                placeholder = { Text("Tu número de teléfono", color = RegPlaceholder) },
                singleLine = true,
                isError = errorTelefono,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFE5E2E1),
                    unfocusedTextColor = Color(0xFFE5E2E1),
                    focusedBorderColor = Color(0xFFFACC15),
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color(0xFFEA4335),
                    cursorColor = Color(0xFFFACC15),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            if (errorTelefono) {
                Text("El teléfono debe contener solo números", color = Color(0xFFEA4335), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: Email
            Text("Email", color = RegTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Tu correo electrónico", color = RegPlaceholder) },
                singleLine = true,
                isError = errorEmail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFE5E2E1),
                    unfocusedTextColor = Color(0xFFE5E2E1),
                    focusedBorderColor = Color(0xFFFACC15),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFFFACC15),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    errorBorderColor = Color(0xFFEA4335),
                    focusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            if (errorNombre) {
                Text("Introduce un correo electrónico válido", color = Color(0xFFEA4335), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // CAMPO: Contraseña
            Text("Contraseña", color = RegTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Tu contraseña", color = RegPlaceholder) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = errorNombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color(0xFFE5E2E1),
                    unfocusedTextColor = Color(0xFFE5E2E1),
                    focusedBorderColor = Color(0xFFFACC15),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color(0xFFFACC15),
                    unfocusedContainerColor = Color(0xFF2A2A2A),
                    errorBorderColor = Color(0xFFEA4335),
                    focusedContainerColor = Color(0xFF2A2A2A)
                ),
                shape = RoundedCornerShape(16.dp)
            )
            if (errorNombre) {
                Text("La contraseña debe tener al menos 6 caracteres", color = Color(0xFFEA4335), fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))

            // TÉRMINOS Y CONDICIONES
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = aceptaTerminos,
                    onCheckedChange = {
                        aceptaTerminos = it
                        if (it) errorTerminos = false
                    },
                    colors = CheckboxDefaults.colors(
                        checkedColor = RegYellow,
                        uncheckedColor = if (errorTerminos) Color(0xFFEA4335) else RegTextMuted
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Acepto los términos y condiciones y la política de privacidad de CRM Guitars.",
                    color = if (errorTerminos) Color(0xFFEA4335) else RegTextMuted,                    fontSize = 14.sp
                )
            }
            if (errorTerminos) {
                Text(
                    text = "Debes aceptar los términos para continuar",
                    color = Color(0xFFEA4335),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 36.dp, top = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN REGISTRARSE
            Button(
                onClick = {
                    errorNombre = nombre.isBlank()
                    errorTelefono = telefono.isBlank() || !telefono.all { it.isDigit() }
                    errorEmail = email.isBlank() || !esEmailValido(email)
                    errorPassword = password.isBlank() || password.length < 6
                    errorTerminos = !aceptaTerminos
                    errorRegistro = null

                    if (!errorNombre && !errorTelefono && !errorEmail && !errorPassword && !errorTerminos) {
                        cargando = true
                        scope.launch {
                            val resultado = AuthManager.registrarUsuario(nombre, telefono, email, password)
                            cargando = false
                            resultado.onSuccess {
                                mostrarDialogo = true
                            }.onFailure { e ->
                                errorRegistro = e.message ?: "Error al registrar. Inténtalo de nuevo."
                            }
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color(0xFF3C2F00), modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Registrarse",
                        color = Color(0xFF3C2F00),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorRegistro != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorRegistro ?: "",
                    color = Color(0xFFEA4335),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // LINK: Ya tienes cuenta
            TextButton(
                onClick = onLoginClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿Ya tienes una cuenta? ", color = RegTextMuted, fontSize = 14.sp)
                Text("Inicia sesión", color = Color(0xFFFACC15), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}