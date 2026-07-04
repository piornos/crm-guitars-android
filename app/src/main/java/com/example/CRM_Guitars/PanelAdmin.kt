package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun PanelAdmin(
    onSalirClick: () -> Unit,
    onVerDetalleCita: () -> Unit = {},
    onVerAgenda: () -> Unit = {}
) {
    var pestanaActiva by remember { mutableStateOf(0) }
    var citas by remember { mutableStateOf<List<CitaHistorial>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val datosCitas = AuthManager.obtenerTodasLasCitas()
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
                nombreUsuario = datos["nombreUsuario"] as? String ?: "",
                uidUsuario = datos["uidUsuario"] as? String ?: "",
                fotos = (datos["fotos"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
        }
        cargando = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Panel de Administrador",
                color = Color(0xFFF1BC2D),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Salir",
                color = Color(0xFFF6B2AC),
                fontSize = 14.sp,
                modifier = Modifier.clickable { onSalirClick() }
            )
        }

        // PESTAÑAS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF1E1E1E))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Citas", "Calendario", "Agenda").forEachIndexed { index, titulo ->
                Box(
                    modifier = Modifier
                        .background(
                            if (pestanaActiva == index) Color(0xFFFACC15) else Color(0xFF2A2A2A),
                            RoundedCornerShape(20.dp)
                        )
                        .clickable {
                            if (index == 2) {
                                onVerAgenda()
                            } else {
                                pestanaActiva = index
                            }
                        }                        .padding(horizontal = 20.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = titulo,
                        color = if (pestanaActiva == index) Color(0xFF3C2F00) else Color(0xFFC2C6D6),
                        fontSize = 14.sp,
                        fontWeight = if (pestanaActiva == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (pestanaActiva) {
                0 -> {
                    Text(
                        text = "TODAS LAS CITAS",
                        color = Color(0xFFFACC15),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    if (cargando) {
                        CircularProgressIndicator(color = Color(0xFFFACC15))
                    } else if (citas.isEmpty()) {
                        Text("No hay citas agendadas todavía", color = Color(0xFFC2C6D6), fontSize = 14.sp)
                    } else {
                        citas.forEach { cita ->
                            TarjetaCita(cita, onDetalleClick = {
                                CitaSeleccionada.id = cita.id
                                CitaSeleccionada.fecha = cita.fecha
                                CitaSeleccionada.hora = cita.hora
                                CitaSeleccionada.marca = cita.marca
                                CitaSeleccionada.modelo = cita.instrumento
                                CitaSeleccionada.motivo = cita.servicio
                                CitaSeleccionada.estado = cita.estado
                                CitaSeleccionada.resolucion = cita.resolucion
                                CitaSeleccionada.nombreUsuario = cita.nombreUsuario
                                CitaSeleccionada.uidUsuario = cita.uidUsuario
                                CitaSeleccionada.fotos = cita.fotos
                                onVerDetalleCita()
                            })
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
                1 -> {
                    GestorHorarios()
                }
                2 -> {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onVerAgenda,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                        ) {
                            Text("Ver Agenda de Clientes", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                        }
                    }
                }               }
            }
        }
    }
@Composable
fun GestorHorarios() {
    val scope = rememberCoroutineScope()
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var diasSeleccionados by remember { mutableStateOf<Set<LocalDate>>(emptySet()) }
    var diaActivo by remember { mutableStateOf<LocalDate?>(null) }
    var horasPorDia by remember { mutableStateOf<Map<LocalDate, Set<String>>>(emptyMap()) }
    var guardando by remember { mutableStateOf(false) }
    var guardadoExitoso by remember { mutableStateOf(false) }

    // Cargar días con horarios del mes actual
    LaunchedEffect(currentMonth) {
        val diasConHorarios = AuthManager.obtenerDiasDisponiblesDelMes(
            currentMonth.year,
            currentMonth.monthValue
        )
        // Convertir las fechas DD-MM-YYYY a LocalDate y cargar sus horas
        val nuevoHorasPorDia = mutableMapOf<LocalDate, Set<String>>()
        diasConHorarios.forEach { fechaStr ->
            val partes = fechaStr.split("-")
            if (partes.size == 3) {
                val dia = partes[0].toIntOrNull() ?: return@forEach
                val mes = partes[1].toIntOrNull() ?: return@forEach
                val anio = partes[2].toIntOrNull() ?: return@forEach
                val date = LocalDate.of(anio, mes, dia)
                val horas = AuthManager.obtenerHorasDisponibles(fechaStr)
                nuevoHorasPorDia[date] = horas.toSet()
            }
        }
        horasPorDia = nuevoHorasPorDia
        diasSeleccionados = nuevoHorasPorDia.keys.toSet()
    }
    val todasLasHoras = listOf(
        "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
        "12:00", "12:30", "16:00", "16:30", "17:00", "17:30",
        "18:00", "18:30", "19:00", "19:30"
    )

    val diasSemana = listOf("LU", "MA", "MI", "JU", "VI", "SA", "DO")

    Text("GESTIÓN DE HORARIOS", color = Color(0xFFFACC15), fontSize = 14.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(12.dp))

    // CALENDARIO
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                color = Color(0xFFE5E2E1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row {
                Text("‹", color = Color(0xFFE5E2E1), fontSize = 20.sp,
                    modifier = Modifier.clickable { currentMonth = currentMonth.minusMonths(1) }.padding(horizontal = 8.dp))
                Text("›", color = Color(0xFFE5E2E1), fontSize = 20.sp,
                    modifier = Modifier.clickable { currentMonth = currentMonth.plusMonths(1) }.padding(horizontal = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth()) {
            diasSemana.forEach { dia ->
                Text(dia, color = Color(0xFFC2C6D6), fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstDayOfMonth = currentMonth.dayOfWeek.value - 1
        val daysInMonth = currentMonth.lengthOfMonth()
        val rows = (firstDayOfMonth + daysInMonth + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val dayIndex = row * 7 + col - firstDayOfMonth + 1
                    if (dayIndex < 1 || dayIndex > daysInMonth) {
                        Box(modifier = Modifier.weight(1f).height(36.dp))
                    } else {
                        val date = currentMonth.withDayOfMonth(dayIndex)
                        val isActivo = date == diaActivo
                        val isSeleccionado = diasSeleccionados.contains(date)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .padding(2.dp)
                                .background(
                                    when {
                                        isActivo -> Color(0xFFFACC15)
                                        isSeleccionado -> Color(0xFFF1BC2D).copy(alpha = 0.5f)
                                        else -> Color.Transparent
                                    },
                                    CircleShape
                                )
                                .clickable {
                                    if (diasSeleccionados.contains(date)) {
                                        diasSeleccionados = diasSeleccionados - date
                                        if (diaActivo == date) {
                                            diaActivo = diasSeleccionados.firstOrNull()
                                        }
                                    } else {
                                        diasSeleccionados = diasSeleccionados + date
                                        diaActivo = date
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = dayIndex.toString(),
                                color = when {
                                    isActivo -> Color(0xFF3C2F00)
                                    isSeleccionado -> Color(0xFFF1BC2D)
                                    else -> Color(0xFFE5E2E1)
                                },
                                fontSize = 14.sp,
                                fontWeight = if (isActivo || isSeleccionado) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = when {
            diasSeleccionados.isEmpty() -> "Selecciona uno o varios días"
            diaActivo != null -> "${diasSeleccionados.size} día(s) seleccionado(s) — editando: ${diaActivo!!.dayOfMonth}/${diaActivo!!.monthValue}/${diaActivo!!.year}"
            else -> "${diasSeleccionados.size} día(s) seleccionado(s)"
        },
        color = Color(0xFFC2C6D6),
        fontSize = 13.sp
    )

    Spacer(modifier = Modifier.height(8.dp))

    val horasSeleccionadasActivo = horasPorDia[diaActivo] ?: emptySet()
    val horasMañana = todasLasHoras.filter { it < "14:00" }
    val horasTarde = todasLasHoras.filter { it >= "14:00" }

    Text("MAÑANA", color = Color(0xFFE5E2E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    HorasGrid(horas = horasMañana, horasSeleccionadas = horasSeleccionadasActivo) { hora ->
        diaActivo?.let { dia ->
            val horasActuales = horasPorDia[dia] ?: emptySet()
            horasPorDia = horasPorDia + (dia to (
                    if (horasActuales.contains(hora)) horasActuales - hora else horasActuales + hora
                    ))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Text("TARDE", color = Color(0xFFE5E2E1), fontSize = 12.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(8.dp))
    HorasGrid(horas = horasTarde, horasSeleccionadas = horasSeleccionadasActivo) { hora ->
        diaActivo?.let { dia ->
            val horasActuales = horasPorDia[dia] ?: emptySet()
            horasPorDia = horasPorDia + (dia to (
                    if (horasActuales.contains(hora)) horasActuales - hora else horasActuales + hora
                    ))
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    Button(
        onClick = {
            guardando = true
            guardadoExitoso = false
            scope.launch {
                diasSeleccionados.forEach { date ->
                    val fecha = "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthValue.toString().padStart(2, '0')}-${date.year}"
                    val horas = horasPorDia[date] ?: emptySet()
                    AuthManager.guardarHorasDisponibles(fecha, horas.sorted())
                }
                guardando = false
                guardadoExitoso = true
            }
        },
        enabled = !guardando && diasSeleccionados.isNotEmpty(),
        modifier = Modifier.fillMaxWidth().height(52.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
    ) {
        if (guardando) {
            CircularProgressIndicator(color = Color(0xFF3C2F00), modifier = Modifier.size(20.dp))
        } else {
            Text("Guardar Horarios", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
        }
    }

    if (guardadoExitoso) {
        AlertDialog(
            onDismissRequest = { guardadoExitoso = false },
            title = { Text("¡Horarios guardados!", color = Color(0xFFE5E2E1)) },
            text = { Text("Los horarios se han guardado correctamente.", color = Color(0xFFC2C6D6)) },
            confirmButton = {
                Button(
                    onClick = { guardadoExitoso = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFACC15))
                ) {
                    Text("Aceptar", color = Color(0xFF3C2F00), fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }
}

@Composable
fun HorasGrid(
    horas: List<String>,
    horasSeleccionadas: Set<String>,
    onHoraClick: (String) -> Unit
) {
    val filas = horas.chunked(4)
    filas.forEach { fila ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            fila.forEach { hora ->
                val isSelected = horasSeleccionadas.contains(hora)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .background(
                            if (isSelected) Color(0xFFF1BC2D) else Color(0xFF2A2A2A),
                            RoundedCornerShape(12.dp)
                        )
                        .clickable { onHoraClick(hora) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = hora,
                        color = if (isSelected) Color(0xFF3C2F00) else Color(0xFFE5E2E1),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
            repeat(4 - fila.size) {
                Box(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}