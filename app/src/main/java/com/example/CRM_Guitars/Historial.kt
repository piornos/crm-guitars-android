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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.LaunchedEffect

val HistBgColor = Color(0xFF131313)
val HistYellow = Color(0xFFF1BC2D)
val HistTextLight = Color(0xFFE5E2E1)
val HistTextMuted = Color(0xFFC2C6D6)
val HistInputBg = Color(0xFF2A2A2A)
val HistCardBg = Color(0xFF1C1B1B)
val HistBadgeBg = Color(0x33AF8D11)
val HistBadgeText = Color(0xFFE9C349)
val HistHeader = Color(0xFFADC6FF)

data class CitaHistorial(
    val id: String = "",
    val fecha: String,
    val instrumento: String,
    val servicio: String,
    val estado: String,
    val hora: String = "",
    val marca: String = "",
    val resolucion: String = "",
    val fotos: List<String> = emptyList(),
    val nombreUsuario: String = "",
    val uidUsuario: String = ""
)

@Composable
fun Historial(
    onBack: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onSalirClick: () -> Unit = {},
    onVerEstadoCita: () -> Unit = {}
)
 {
     var busqueda by remember { mutableStateOf("") }
     var citas by remember { mutableStateOf<List<CitaHistorial>>(emptyList()) }
     var cargando by remember { mutableStateOf(true) }

     LaunchedEffect(Unit) {
         val datosCitas = AuthManager.obtenerCitasUsuario()
         citas = datosCitas.map { datos ->
             CitaHistorial(
                 id = datos["id"] as? String ?: "",
                 fecha = datos["fecha"] as? String ?: "",
                 instrumento = datos["modelo"] as? String ?: "",
                 servicio = datos["motivo"] as? String ?: "",
                 estado = (datos["estado"] as? String ?: "Pendiente").uppercase(),
                 hora = datos["hora"] as? String ?: "",
                 marca = datos["marca"] as? String ?: "",
                 resolucion = datos["resolucion"] as? String ?: "",
                 fotos = (datos["fotos"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
             )
         }
         cargando = false
     }

    val citasFiltradas = citas.filter {
        it.instrumento.contains(busqueda, ignoreCase = true) ||
                it.servicio.contains(busqueda, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HistBgColor)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("🔍  Buscar por modelo o servicio...", color = HistTextMuted) },
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

            Text(
                text = "HISTORIAL DE CITAS",
                color = Color(0xFFFACC15),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (cargando) {
                CircularProgressIndicator(color = Color(0xFFFACC15))
            } else if (citasFiltradas.isEmpty()) {
                Text("Aún no tienes citas agendadas", color = HistTextMuted, fontSize = 14.sp)
            } else {
                citasFiltradas.forEach { cita ->
                    TarjetaCita(cita, onDetalleClick = {
                        CitaSeleccionada.id = cita.id
                        CitaSeleccionada.fecha = cita.fecha
                        CitaSeleccionada.hora = cita.hora
                        CitaSeleccionada.marca = cita.marca
                        CitaSeleccionada.modelo = cita.instrumento
                        CitaSeleccionada.motivo = cita.servicio
                        CitaSeleccionada.estado = cita.estado
                        CitaSeleccionada.resolucion = cita.resolucion
                        CitaSeleccionada.fotos = cita.fotos
                        onVerEstadoCita()
                    })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // BOTTOM NAV
        BottomNav(
            itemActivo = NavItem.HISTORIAL,
            onPerfilClick = onPerfilClick,
            onCitasClick = onCitasClick,
            onHistorialClick = onHistorialClick,
            onSalirClick = onSalirClick
        )
    }
}

@Composable
fun TarjetaCita(cita: CitaHistorial, onDetalleClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HistCardBg, RoundedCornerShape(12.dp))
            .clickable { onDetalleClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(cita.fecha, color = HistTextMuted, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(cita.instrumento, color = HistTextLight, fontSize = 20.sp)
            }
            val (badgeBg, badgeText) = when (cita.estado.uppercase()) {
                "FINALIZADO" -> Color(0x1A34A853) to Color(0xFF34A853)
                "PENDIENTE" -> Color(0x1AF1BC2D) to Color(0xFFFACC15)
                "EN PROCESO" -> Color(0x1AE5E2E1) to Color(0xFFE5E2E1)
                else -> Color(0x1AF1BC2D) to Color(0xFFFACC15)
            }

            Box(
                modifier = Modifier
                    .background(badgeBg, RoundedCornerShape(9999.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text(cita.estado, color = badgeText, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row {
            Text("🔧  ", fontSize = 14.sp)
            Text(cita.servicio, color = HistTextMuted, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onDetalleClick() },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Detalles y Resolución", color = HistBadgeText, fontSize = 14.sp)
            Text("›", color = HistBadgeText, fontSize = 16.sp)
        }
    }
}