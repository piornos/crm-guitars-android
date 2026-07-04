package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun Inicio(
    onPerfilClick: () -> Unit,
    onCitasClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onSalirClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF131313))
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(48.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CRM Guitars",
                color = Color(0xFFFACC15),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF1E1E1E), RoundedCornerShape(9999.dp)),
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
                    Icon(Icons.Filled.Person, contentDescription = "Perfil", tint = Color(0xFFC2C6D6), modifier = Modifier.size(20.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Qué quieres hacer hoy?",
            color = Color(0xFFC2C6D6),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        FilaInicio(
            icono = Icons.Filled.Person,
            titulo = "Perfil",
            descripcion = "Gestiona tus datos personales",
            onClick = onPerfilClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilaInicio(
            icono = Icons.Filled.CalendarMonth,
            titulo = "Citas",
            descripcion = "Agenda una nueva cita",
            onClick = onCitasClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilaInicio(
            icono = Icons.Filled.History,
            titulo = "Historial",
            descripcion = "Consulta tus historial de citas",
            onClick = onHistorialClick
        )

        Spacer(modifier = Modifier.height(12.dp))

        FilaInicio(
            icono = Icons.Filled.Logout,
            titulo = "Salir",
            descripcion = "Cerrar sesión",
            colorIcono = Color(0xFFF6B2AC),
            onClick = onSalirClick
        )
    }
}

@Composable
fun FilaInicio(
    icono: ImageVector,
    titulo: String,
    descripcion: String,
    colorIcono: Color = Color(0xFFFACC15),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(colorIcono.copy(alpha = 0.15f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = titulo, tint = colorIcono, modifier = Modifier.size(24.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(titulo, color = Color(0xFFE5E2E1), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(descripcion, color = Color(0xFFC2C6D6), fontSize = 13.sp)
        }

        Icon(
            Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = Color(0xFFC2C6D6),
            modifier = Modifier.size(20.dp)
        )
    }
}

