package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.runtime.LaunchedEffect
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.foundation.Image
import kotlinx.coroutines.launch
import androidx.compose.ui.draw.clip
import com.CRM_guitars.app.SesionActual

val ProfBgColor = Color(0xFF131313)
val ProfTopBarBg = Color(0xFF201F1F)
val ProfYellow = Color(0xFFF1B916)
val ProfYellowBtn = Color(0xFFEAB308)
val ProfTextLight = Color(0xFFE5E2E1)
val ProfTextMuted = Color(0xFFC2C6D6)
val ProfInputBg = Color(0xFF1E1E1E)
val ProfBtnDark = Color(0xFF342800)

@Composable
fun Perfil(
    onBack: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onSalirClick: () -> Unit = {},
    onGuardarExitoso: () -> Unit = {}
) {
    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fotoBase64 by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        val datos = AuthManager.obtenerDatosUsuario()
        if (datos != null) {
            nombre = datos["nombre"] as? String ?: ""
            telefono = datos["telefono"] as? String ?: ""
            email = datos["email"] as? String ?: ""
            fotoBase64 = datos["fotoPerfil"] as? String
        }
    }
    var imagenUri by remember { mutableStateOf<Uri?>(null) }
    var mostrarDialogoGuardado by remember { mutableStateOf(false) }
    val selectorImagen = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val inputStream = context.contentResolver.openInputStream(it)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)
                fotoBase64 = base64
                SesionActual.fotoBase64 = base64
                scope.launch {
                    AuthManager.guardarFotoPerfil(base64)
                }
            }
        }
    }
    if (mostrarDialogoGuardado) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Cambios guardados!", color = Color(0xFFE5E2E1)) },
            text = { Text("Tu perfil ha sido actualizado correctamente.", color = Color(0xFFC2C6D6)) },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoGuardado = false
                        onGuardarExitoso()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    Text("Aceptar", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF201F1F)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ProfBgColor)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(ProfTopBarBg)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Perfil",
                color = ProfYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            // PROFILE HEADER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(ProfBgColor),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(112.dp)
                            .background(ProfTopBarBg, CircleShape)
                            .clickable {
                                selectorImagen.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoBase64 != null) {
                            val bytes = Base64.decode(fotoBase64, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(112.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        } else {
                            Text("👤", fontSize = 48.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = nombre,
                        color = ProfTextLight,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Botón cambiar foto
                    Box(
                        modifier = Modifier
                            .background(Color.Transparent, RoundedCornerShape(9999.dp))
                            .clickable {
                                selectorImagen.launch(
                                    androidx.activity.result.PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                    )
                                )
                            }
                            .padding(horizontal = 24.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Cambiar foto",
                            color = Color(0xFFE9C349),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // FORM SECTION
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
            ) {
                // Nombre Completo
                Text("Nombre Completo", color = ProfTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    singleLine = true,
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
                        focusedContainerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Teléfono
                Text("Teléfono", color = ProfTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = telefono,
                    onValueChange = { telefono = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
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
                        focusedContainerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Email
                Text("Email", color = ProfTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
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
                        focusedContainerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contraseña
                Text("Contraseña", color = ProfTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
                        focusedContainerColor = Color(0xFF2A2A2A)
                    ),
                    shape = RoundedCornerShape(16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón Guardar Cambios
                Button(
                    onClick = { mostrarDialogoGuardado = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    Text(
                        text = "Guardar Cambios",
                        color = Color(0xFF3C2F00),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // BOTTOM NAV
        BottomNav(
            itemActivo = NavItem.PERFIL,
            onPerfilClick = onPerfilClick,
            onCitasClick = onCitasClick,
            onHistorialClick = onHistorialClick,
            onSalirClick = onSalirClick
        )
    }
}