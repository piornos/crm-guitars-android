package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.layout.statusBarsPadding
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import com.CRM_guitars.app.SesionActual

val CalBgColor = Color(0xFF131313)
val CalTopBarBg = Color(0xFF201F1F)
val CalYellow = Color(0xFFF1B916)
val CalYellowBtn = Color(0xFFF1BC2D)
val CalTextLight = Color(0xFFE5E2E1)
val CalTextMuted = Color(0xFFC2C6D6)
val CalWidgetBg = Color(0xFF1E1E1E)
val CalTimeSlotBg = Color(0xFF2A2A2A)
val CalBtnDark = Color(0xFF3C2F00)
val CalDayDisabled = Color(0xFF3A3A3A)

@Composable
fun Calendario(
    onNewAppointment: () -> Unit,
    onPerfilClick: () -> Unit = {},
    onCitasClick: () -> Unit = {},
    onHistorialClick: () -> Unit = {},
    onSalirClick: () -> Unit = {}
) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentMonth by remember { mutableStateOf(LocalDate.now().withDayOfMonth(1)) }
    var selectedHour by remember { mutableStateOf<String?>(null) }
    var diasDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }
    var horasDisponibles by remember { mutableStateOf<List<String>>(emptyList()) }

    val diasSemana = listOf("LU", "MA", "MI", "JU", "VI", "SA", "DO")

    // Cargar días disponibles cuando cambia el mes
    LaunchedEffect(currentMonth) {
        diasDisponibles = AuthManager.obtenerDiasDisponiblesDelMes(
            currentMonth.year,
            currentMonth.monthValue
        )
        selectedDate = null
        selectedHour = null
        horasDisponibles = emptyList()
    }

    // Cargar horas disponibles cuando cambia el día seleccionado
    LaunchedEffect(selectedDate) {
        selectedDate?.let { date ->
            val fechaFormato = "${date.dayOfMonth.toString().padStart(2, '0')}-${date.monthValue.toString().padStart(2, '0')}-${date.year}"
            horasDisponibles = AuthManager.obtenerHorasDisponibles(fechaFormato)
            selectedHour = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalBgColor)
            .statusBarsPadding()
    ) {
        // TOP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(CalTopBarBg)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Agendar Cita",
                color = CalYellow,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(CalWidgetBg, RoundedCornerShape(9999.dp)),
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
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = CalTextLight, modifier = Modifier.size(20.dp))
                }
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // CALENDARIO WIDGET
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CalWidgetBg, RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                // Navegación mes
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() }} ${currentMonth.year}",
                        color = CalTextLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row {
                        Text(
                            text = "‹",
                            color = CalTextLight,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable {
                                    currentMonth = currentMonth.minusMonths(1)
                                }
                                .padding(horizontal = 8.dp)
                        )
                        Text(
                            text = "›",
                            color = CalTextLight,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .clickable {
                                    currentMonth = currentMonth.plusMonths(1)
                                }
                                .padding(horizontal = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Días de la semana
                Row(modifier = Modifier.fillMaxWidth()) {
                    diasSemana.forEach { dia ->
                        Text(
                            text = dia,
                            color = CalTextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Días del mes
                val firstDayOfMonth = currentMonth.dayOfWeek.value - 1
                val daysInMonth = currentMonth.lengthOfMonth()
                val totalCells = firstDayOfMonth + daysInMonth
                val rows = (totalCells + 6) / 7

                for (row in 0 until rows) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (col in 0 until 7) {
                            val dayIndex = row * 7 + col - firstDayOfMonth + 1
                            if (dayIndex < 1 || dayIndex > daysInMonth) {
                                Box(modifier = Modifier.weight(1f).height(36.dp))
                            } else {
                                val date = currentMonth.withDayOfMonth(dayIndex)
                                val fechaFormato = "${dayIndex.toString().padStart(2, '0')}-${currentMonth.monthValue.toString().padStart(2, '0')}-${currentMonth.year}"
                                val isDisponible = diasDisponibles.contains(fechaFormato)
                                val isSelected = date == selectedDate

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .padding(2.dp)
                                        .background(
                                            when {
                                                isSelected -> CalYellowBtn
                                                else -> Color.Transparent
                                            },
                                            CircleShape
                                        )
                                        .then(
                                            if (isDisponible) Modifier.clickable {
                                                selectedDate = date
                                            } else Modifier
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayIndex.toString(),
                                        color = when {
                                            isSelected -> CalBtnDark
                                            isDisponible -> CalTextLight
                                            else -> CalDayDisabled
                                        },
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // HORAS DISPONIBLES
            if (selectedDate != null) {
                if (horasDisponibles.isEmpty()) {
                    Text(
                        text = "No hay horas disponibles para este día",
                        color = CalTextMuted,
                        fontSize = 14.sp
                    )
                } else {
                    val horasMañana = horasDisponibles.filter { it < "14:00" }
                    val horasTarde = horasDisponibles.filter { it >= "14:00" }

                    if (horasMañana.isNotEmpty()) {
                        Text("MAÑANA", color = CalTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            horasMañana.forEach { hora ->
                                val isSelected = hora == selectedHour
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .background(
                                            if (isSelected) CalYellowBtn else CalTimeSlotBg,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedHour = hora },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = hora,
                                        color = if (isSelected) CalBtnDark else CalTextLight,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    if (horasTarde.isNotEmpty()) {
                        Text("TARDE", color = CalTextLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            horasTarde.forEach { hora ->
                                val isSelected = hora == selectedHour
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .background(
                                            if (isSelected) CalYellowBtn else CalTimeSlotBg,
                                            RoundedCornerShape(12.dp)
                                        )
                                        .clickable { selectedHour = hora },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = hora,
                                        color = if (isSelected) CalBtnDark else CalTextLight,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            } else {
                Text(
                    text = "Selecciona un día disponible para ver las horas",
                    color = CalTextMuted,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // BOTÓN CONTINUAR
            Button(
                onClick = {
                    if (selectedHour != null && selectedDate != null) {
                        CitaTemporal.fecha = "${selectedDate!!.dayOfMonth} ${selectedDate!!.month.getDisplayName(TextStyle.SHORT, Locale("es"))} ${selectedDate!!.year}"
                        CitaTemporal.hora = selectedHour ?: ""
                        onNewAppointment()
                    }
                },
                enabled = selectedHour != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CalYellowBtn)
            ) {
                Text(
                    text = "Continuar",
                    color = CalBtnDark,
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