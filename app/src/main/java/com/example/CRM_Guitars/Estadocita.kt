package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.clickable
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import com.CRM_guitars.app.SesionActual
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.clickable

val EstBgColor = Color(0xFF131313)
val EstHeaderBg = Color(0xFF1E1E1E)
val EstYellow = Color(0xFFF1BC2D)
val EstTextLight = Color(0xFFE5E2E1)
val EstTextMuted = Color(0xFFC2C6D6)
val EstCardBg = Color(0xFF1E1E1E)
val EstInnerBg = Color(0xFF2A2A2A)
val EstDivider = Color(0xFF424754)
val EstStatusBg = Color(0x1AF1BC2D)

@Composable
fun EstadoCita(
    onBack: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onSalirClick: () -> Unit = {},
    onVerPerfilUsuario: () -> Unit = {},
    onGuardadoExitoso: () -> Unit = {},
    esAdmin: Boolean = false
) {
    var estadoEditable by remember { mutableStateOf(CitaSeleccionada.estado) }
    var resolucionEditable by remember { mutableStateOf(CitaSeleccionada.resolucion) }
    var guardando by remember { mutableStateOf(false) }
    var guardadoExitoso by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val estadosDisponibles = listOf("PENDIENTE", "EN PROCESO", "FINALIZADO")
    var menuEstadoAbierto by remember { mutableStateOf(false) }
    var fotoAmpliada by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoGuardado by remember { mutableStateOf(false) }
    if (mostrarDialogoGuardado) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoGuardado = false },
            title = { Text("¡Cambios guardados!", color = Color(0xFFE5E2E1)) },
            text = { Text("El estado y la resolución han sido actualizados correctamente.", color = Color(0xFFC2C6D6)) },
            confirmButton = {
                Button(
                    onClick = { mostrarDialogoGuardado = false
                        onGuardadoExitoso()
                              },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    Text("Aceptar", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
    if (fotoAmpliada != null) {
        AlertDialog(
            onDismissRequest = { fotoAmpliada = null },
            confirmButton = {
                TextButton(onClick = { fotoAmpliada = null }) {
                    Text("Cerrar", color = EstYellow)
                }
            },
            text = {
                val bytes = Base64.decode(fotoAmpliada, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Foto ampliada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.FillWidth
                    )
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(EstBgColor)
            .statusBarsPadding()
    ) {

        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(EstHeaderBg)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                    Text("‹", color = EstYellow, fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Estado de la Cita",
                    color = EstYellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(EstInnerBg, RoundedCornerShape(9999.dp)),
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
                    Text("👤", fontSize = 18.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // TARJETA PRINCIPAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EstCardBg, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Detalles de tu cita", color = EstTextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${CitaSeleccionada.fecha}\n${CitaSeleccionada.hora}",
                        color = EstTextLight,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (esAdmin && CitaSeleccionada.nombreUsuario.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "👤 ${CitaSeleccionada.nombreUsuario}",
                            color = EstYellow,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onVerPerfilUsuario() }
                        )
                    }
                }

                val (colorFondo, colorTexto) = when (estadoEditable.uppercase()) {
                    "PENDIENTE" -> Color(0x1AF1BC2D) to Color(0xFFFACC15)
                    "EN PROCESO" -> Color(0x1AE5E2E1) to Color(0xFFE5E2E1)
                    "FINALIZADO" -> Color(0x1A34A853) to Color(0xFF34A853)
                    else -> Color(0x1AF1BC2D) to Color(0xFFFACC15)
                }

                Box {
                    Box(
                        modifier = Modifier
                            .background(colorFondo, RoundedCornerShape(9999.dp))
                            .let { if (esAdmin) it.clickable { menuEstadoAbierto = true } else it }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(estadoEditable, color = colorTexto, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            if (esAdmin) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("▾", color = colorTexto, fontSize = 12.sp)
                            }
                        }
                    }
                    if (esAdmin) {
                        DropdownMenu(
                            expanded = menuEstadoAbierto,
                            onDismissRequest = { menuEstadoAbierto = false }
                        ) {
                            estadosDisponibles.forEach { estado ->
                                DropdownMenuItem(
                                    text = { Text(estado) },
                                    onClick = {
                                        estadoEditable = estado
                                        menuEstadoAbierto = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // SEGUIMIENTO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EstCardBg, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Seguimiento", color = EstYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))

                val pasos = when (estadoEditable.uppercase()) {
                    "PENDIENTE" -> listOf(
                        Triple("✓", "Instrumento Recibido", ""),
                        Triple("◉", "Pendiente de revisión", "En espera")
                    )
                    "EN PROCESO" -> listOf(
                        Triple("✓", "Instrumento Recibido", ""),
                        Triple("◉", "En Proceso", "En progreso")
                    )
                    "FINALIZADO" -> listOf(
                        Triple("✓", "Instrumento Recibido", ""),
                        Triple("✓", "Finalizado", CitaSeleccionada.fechaFinalizacion.ifBlank { "Completado" })
                    )
                    else -> listOf(
                        Triple("✓", "Instrumento Recibido", ""),
                        Triple("◉", "Pendiente", "En espera")
                    )
                }

                pasos.forEachIndexed { index, (icono, titulo, hora) ->
                    Row(verticalAlignment = Alignment.Top) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(EstYellow, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(icono, color = Color.Black, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            if (index < pasos.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(40.dp)
                                        .background(EstDivider)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(titulo, color = EstTextLight, fontSize = 14.sp)
                            Text(hora, color = EstTextMuted, fontSize = 12.sp)
                        }
                    }
                }
            }

            // DETALLES DEL INSTRUMENTO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EstCardBg, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Instrumento", color = EstYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row {
                    Text("🎸  ", fontSize = 16.sp)
                    Text("${CitaSeleccionada.marca} ${CitaSeleccionada.modelo}", color = EstTextLight, fontSize = 16.sp)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Motivo", color = EstYellow, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text("🔧  ", fontSize = 16.sp)
                    Text(CitaSeleccionada.motivo, color = EstTextLight, fontSize = 16.sp)
                }
                if (CitaSeleccionada.fotos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Fotos", color = EstYellow, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(CitaSeleccionada.fotos) { foto ->
                            val bytes = Base64.decode(foto, Base64.DEFAULT)
                            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            bitmap?.let {
                                Image(
                                    bitmap = it.asImageBitmap(),
                                    contentDescription = "Foto instrumento",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { fotoAmpliada = foto },
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }

            }

            // RESOLUCIÓN DEL TÉCNICO
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EstCardBg, RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Text("Resolución", color = EstYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EstInnerBg, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (CitaSeleccionada.resolucion.isNotBlank()) CitaSeleccionada.resolucion else "Aún no hay resolución disponible. Te avisaremos cuando el técnico complete el diagnóstico.",
                        color = EstTextLight,
                        fontSize = 16.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(EstInnerBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✍", fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Firmado por: Master Luthier Carlos P.", color = EstTextMuted, fontSize = 12.sp)
                }
            }

            // PANEL DE EDICIÓN (SOLO ADMIN)
            if (esAdmin) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EstCardBg, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text("Editar Cita (Admin)", color = EstYellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))



                    Text("Resolución", color = EstTextMuted, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = resolucionEditable,
                        onValueChange = { resolucionEditable = it },
                        placeholder = { Text("Escribe la resolución del técnico...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFFE5E2E1),
                            unfocusedTextColor = Color(0xFFE5E2E1),
                            focusedBorderColor = Color(0xFFFACC15),
                            unfocusedBorderColor = Color.Transparent,
                            cursorColor = Color(0xFFFACC15),
                            unfocusedContainerColor = Color(0xFF2A2A2A),
                            focusedContainerColor = Color(0xFF2A2A2A)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            guardando = true
                            scope.launch {
                                val resultado = AuthManager.actualizarCita(
                                    CitaSeleccionada.id,
                                    estadoEditable,
                                    resolucionEditable
                                )
                                guardando = false
                                resultado.onSuccess {
                                    CitaSeleccionada.estado = estadoEditable
                                    CitaSeleccionada.resolucion = resolucionEditable
                                    mostrarDialogoGuardado = true
                                }
                            }
                        },
                        enabled = !guardando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                    ) {
                        if (guardando) {
                            CircularProgressIndicator(color = Color(0xFF3C2F00), modifier = Modifier.size(20.dp))
                        } else {
                            Text("Guardar Cambios", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

// BOTTOM NAV
        if (!esAdmin) {
            BottomNav(
                itemActivo = NavItem.HISTORIAL,
                onPerfilClick = onPerfilClick,
                onCitasClick = onCitasClick,
                onHistorialClick = onHistorialClick,
                onSalirClick = onSalirClick
            )
        }
    }
}