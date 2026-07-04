package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.CRM_guitars.app.SesionActual
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

val AgBgColor = Color(0xFF131313)
val AgTopBarBg = Color(0xFF1E1E1E)
val AgYellow = Color(0xFFE9C349)
val AgYellowBtn = Color(0xFFF1BC2D)
val AgTextLight = Color(0xFFE5E2E1)
val AgTextMuted = Color(0xFFC2C6D6)
val AgInputBg = Color(0xFF201F1F)
val AgPlaceholder = Color(0xFF6B7280)


@Composable
fun AgendarCita(
    onBack: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onSalirClick: () -> Unit = {},
    onCitaEnviada: () -> Unit = {}
) {
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var motivo by remember { mutableStateOf("") }
    var mostrarDialogoEnvio by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var cargando by remember { mutableStateOf(false) }
    var errorEnvio by remember { mutableStateOf<String?>(null) }
    var fotosBase64 by remember { mutableStateOf<List<String>>(emptyList()) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val selectorFotos = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(3)
    ) { uris ->
        val nuevasFotos = uris.mapNotNull { uri ->
            val inputStream = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            bytes?.let { Base64.encodeToString(it, Base64.DEFAULT) }
        }
        fotosBase64 = nuevasFotos
    }
    if (mostrarDialogoEnvio) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("¡Cita enviada!", color = Color(0xFFE5E2E1)) },
            text = { Text("Tu cita ha sido registrada correctamente. Te avisaremos cuando sea confirmada.", color = Color(0xFFC2C6D6)) },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoEnvio = false
                        onCitaEnviada()
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
            .background(AgBgColor)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(AgTopBarBg)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                    Text("‹", color = AgYellow, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Detalles de la Cita",
                    color = AgYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(AgInputBg, RoundedCornerShape(9999.dp)),
                contentAlignment = Alignment.Center
            ) {
                val foto = SesionActual.fotoBase64
                if (foto != null) {
                    val bytes = Base64.decode(foto, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(9999.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                } else {
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = AgTextLight, modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Marca del Instrumento
            Text("Marca del Instrumento", color = AgTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = marca,
                onValueChange = { marca = it },
                placeholder = { Text("Ej. Gibson, Fender...", color = AgPlaceholder) },
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

            // Modelo del Instrumento
            Text("Modelo del Instrumento", color = AgTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = modelo,
                onValueChange = { modelo = it },
                placeholder = { Text("Ej. Les Paul Standard", color = AgPlaceholder) },
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

            // Motivo de la Cita
            Text("Motivo de la Cita", color = AgTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = motivo,
                onValueChange = { motivo = it },
                placeholder = { Text("Describe el problema o servicio que necesitas...", color = AgPlaceholder) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(128.dp),
                maxLines = 6,
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

            // Fotos del Instrumento
            // Fotos del Instrumento
            Text("Fotos del Instrumento (Opcional)", color = AgTextMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(133.dp)
                    .background(AgInputBg, RoundedCornerShape(8.dp))
                    .border(1.dp, AgTextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable {
                        selectorFotos.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                if (fotosBase64.isEmpty()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.PhotoCamera, contentDescription = "Subir foto", tint = AgTextLight, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Seleccionar hasta 3 fotos",
                            color = AgTextLight,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "JPG, PNG hasta 10MB",
                            color = AgTextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyRow(
                        modifier = Modifier.fillMaxSize().padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(fotosBase64) { foto ->
                            val bytes = Base64.decode(foto, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Foto instrumento",
                                    modifier = Modifier
                                        .size(110.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Botón Enviar Cita
            Button(
                onClick = {
                    errorEnvio = null
                    if (marca.isBlank() || modelo.isBlank() || motivo.isBlank()) {
                        errorEnvio = "Por favor, rellena todos los campos"
                    } else {
                        cargando = true
                        scope.launch {
                            val resultado = AuthManager.crearCita(
                                marca = marca,
                                modelo = modelo,
                                motivo = motivo,
                                fecha = CitaTemporal.fecha,
                                hora = CitaTemporal.hora,
                                        fotosBase64 = fotosBase64
                            )
                            cargando = false
                            resultado.onSuccess {
                                mostrarDialogoEnvio = true
                            }.onFailure { e ->
                                errorEnvio = e.message ?: "Error al enviar la cita"
                            }
                        }
                    }
                },
                enabled = !cargando,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
            ) {
                if (cargando) {
                    CircularProgressIndicator(color = Color(0xFF3C2F00), modifier = Modifier.size(20.dp))
                } else {
                    Text(
                        text = "Enviar Cita",
                        color = Color(0xFF3C2F00),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorEnvio != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorEnvio ?: "",
                    color = Color(0xFFEA4335),
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Botón Cancelar
            TextButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancelar",
                    color = AgYellow,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // BOTTOM NAV
        BottomNav(
            itemActivo = NavItem.CITAS,
            onPerfilClick = onPerfilClick,
            onCitasClick = onCitasClick,
            onHistorialClick = onHistorialClick,
            onSalirClick = onSalirClick
        )
    }
}