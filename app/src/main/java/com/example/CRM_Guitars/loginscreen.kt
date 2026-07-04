package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.scale
import com.CRM_guitars.app.SesionActual
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

val BgColor = Color(0xFF131313)
val YellowColor = Color(0xFFFACC15)
val TextLight = Color(0xFFE5E2E1)
val TextMuted = Color(0xFFC2C6D6)
val TextLabel = Color(0xFF8C909F)
val InputBg = Color(0xFF2A2A2A)
val GoogleBg = Color(0xFF353534)
val IconBrownColor = Color(0xFF3C2F00)
val BorderColor = Color(0xFF353534)


@Composable
fun LoginScreen(
    onLoginSuccess: (Boolean) -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorLogin by remember { mutableStateOf<String?>(null) }
    var cargando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current
    var mostrarDialogoRecuperar by remember { mutableStateOf(false) }
    var emailRecuperar by remember { mutableStateOf("") }
    var mensajeRecuperar by remember { mutableStateOf<String?>(null) }
    var enviandoRecuperar by remember { mutableStateOf(false) }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.data == null) {
            cargando = false
            return@rememberLauncherForActivityResult
        }
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            val account = task.getResult(ApiException::class.java)
            val idToken = account.idToken ?: return@rememberLauncherForActivityResult
            cargando = true
            scope.launch {
                val resultado = AuthManager.iniciarSesionConGoogle(idToken)
                if (resultado.isSuccess) {
                    val esAdmin = AuthManager.esUsuarioAdmin()
                    val datos = AuthManager.obtenerDatosUsuario()
                    if (datos != null) {
                        SesionActual.nombre = datos["nombre"] as? String ?: ""
                        SesionActual.email = datos["email"] as? String ?: ""
                        SesionActual.fotoBase64 = datos["fotoPerfil"] as? String
                    }
                    cargando = false
                    onLoginSuccess(esAdmin)
                } else {
                    cargando = false
                    errorLogin = "Error al iniciar sesión con Google"
                }
            }
        } catch (e: ApiException) {
            cargando = false
            if (e.statusCode != com.google.android.gms.common.api.CommonStatusCodes.CANCELED) {
                errorLogin = "Error al iniciar sesión con Google"
            }
        }
    }
    if (mostrarDialogoRecuperar) {
        AlertDialog(
            onDismissRequest = {
                mostrarDialogoRecuperar = false
                emailRecuperar = ""
                mensajeRecuperar = null
            },
            title = { Text("Recuperar contraseña", color = Color(0xFFE5E2E1)) },
            text = {
                Column {
                    if (mensajeRecuperar == "Email enviado correctamente") {
                        Text(
                            "✓ Email enviado correctamente",
                            color = Color(0xFF34A853),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Revisa tu bandeja de entrada y sigue las instrucciones para restablecer tu contraseña.",
                            color = Color(0xFFC2C6D6),
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                        LaunchedEffect(mensajeRecuperar) {
                            kotlinx.coroutines.delay(3000)
                            mostrarDialogoRecuperar = false
                            emailRecuperar = ""
                            mensajeRecuperar = null
                        }
                    } else {
                        Text(
                            "Introduce tu email y te enviaremos un enlace para restablecer tu contraseña.",
                            color = Color(0xFFC2C6D6),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = emailRecuperar,
                            onValueChange = { emailRecuperar = it },
                            placeholder = { Text("Tu email", color = Color(0xFF9E9E9E)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color(0xFFE5E2E1),
                                unfocusedTextColor = Color(0xFFE5E2E1),
                                focusedBorderColor = Color(0xFFFACC15),
                                unfocusedBorderColor = Color(0xFF9E9E9E),
                                cursorColor = Color(0xFFFACC15),
                                unfocusedContainerColor = Color(0xFF2A2A2A),
                                focusedContainerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        if (mensajeRecuperar?.startsWith("Error") == true) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                mensajeRecuperar ?: "",
                                color = Color(0xFFEA4335),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (emailRecuperar.isNotBlank()) {
                            enviandoRecuperar = true
                            scope.launch {
                                val resultado = AuthManager.recuperarContrasena(emailRecuperar)
                                enviandoRecuperar = false
                                resultado.onSuccess {
                                    mensajeRecuperar = "Email enviado correctamente"
                                }.onFailure {
                                    mensajeRecuperar = "Error, comprueba el email introducido"
                                }
                            }
                        }
                    },
                    enabled = !enviandoRecuperar,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    if (enviandoRecuperar) {
                        CircularProgressIndicator(color = Color(0xFF3C2F00), modifier = Modifier.size(20.dp))
                    } else {
                        Text("Enviar", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    mostrarDialogoRecuperar = false
                    emailRecuperar = ""
                    mensajeRecuperar = null
                }) {
                    Text("Cancelar", color = Color(0xFFC2C6D6))
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // SECCIÓN: BrandHeader
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, top = 80.dp, end = 40.dp, bottom = 64.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "CRM Guitars",
                color = TextLight,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }

        // SECCIÓN: LoginForm
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 40.dp)
        ) {
            // Campo EMAIL
            Text(
                text = "Email",
                color = Color(0xFFC2C6D6),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("usuario@correo.com", color = TextLabel) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = YellowColor,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = YellowColor,
                    unfocusedContainerColor = InputBg,
                    focusedContainerColor = InputBg
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo CONTRASEÑA
            Text(
                text = "Contraseña",
                color = Color(0xFFC2C6D6),
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.height(5.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("••••••••", color = TextLabel) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextLight,
                    unfocusedTextColor = TextLight,
                    focusedBorderColor = YellowColor,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = YellowColor,
                    unfocusedContainerColor = InputBg,
                    focusedContainerColor = InputBg
                ),
                shape = RoundedCornerShape(16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    errorLogin = null
                    if (email.isBlank() || password.isBlank()) {
                        errorLogin = "Introduce email y contraseña"
                    } else {
                        cargando = true
                        scope.launch {
                            val resultado = AuthManager.iniciarSesion(email, password)
                            if (resultado.isSuccess) {
                                val esAdmin = AuthManager.esUsuarioAdmin()
                                val datos = AuthManager.obtenerDatosUsuario()
                                if (datos != null) {
                                    SesionActual.nombre = datos["nombre"] as? String ?: ""
                                    SesionActual.email = datos["email"] as? String ?: ""
                                    SesionActual.fotoBase64 = datos["fotoPerfil"] as? String
                                }
                                cargando = false
                                onLoginSuccess(esAdmin)
                            } else {
                                cargando = false
                                errorLogin = "Email o contraseña incorrectos"
                            }
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = YellowColor)
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = IconBrownColor, modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Iniciar sesión",
                        color = IconBrownColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            if (errorLogin != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorLogin ?: "",
                    color = Color(0xFFEA4335),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ¿Olvidaste tu contraseña?
            TextButton(
                onClick = { mostrarDialogoRecuperar = true },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿Olvidaste tu contraseña?", color = TextMuted, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Separador "O CONTINÚA CON"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
                Text(
                    text = "  O CONTINÚA CON  ",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BorderColor)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón Google
            Button(
                onClick = {
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken("540884265300-s11kppr0j86rijmtujrjeijou0j4cs3o.apps.googleusercontent.com")                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = GoogleBg)
            ) {
                Text(
                    text = "Continuar con Google",
                    color = TextLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // SECCIÓN: FooterLinks
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 48.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes una cuenta? ", color = TextMuted, fontSize = 14.sp)
            TextButton(
                onClick = onRegisterClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Regístrate",
                    color = YellowColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}