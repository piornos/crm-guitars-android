package com.example.CRM_Guitars

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.CalendarMonth

enum class NavItem { PERFIL, CITAS, HISTORIAL, SALIR }

@Composable
fun BottomNav(
    itemActivo: NavItem,
    onPerfilClick: () -> Unit,
    onCitasClick: () -> Unit,
    onHistorialClick: () -> Unit,
    onSalirClick: () -> Unit
) {
    val bgColor = Color(0xF21F1F1F)
    val activeItemBg = Color(0xFFF1BC2E)
    val textActivo = Color(0xFF131313)
    val textInactivo = Color(0xFFC3C7D6)
    val textSalir = Color(0xFFF6B2AC)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bgColor)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // PERFIL
        if (itemActivo == NavItem.PERFIL) {
            ItemActivo(icono = Icons.Filled.Person, texto = "Perfil", bg = activeItemBg, color = textActivo, onClick = onPerfilClick)
        } else {
            ItemInactivo(icono = Icons.Outlined.Person, texto = "Perfil", color = textInactivo, onClick = onPerfilClick)
        }

        // CITAS
        if (itemActivo == NavItem.CITAS) {
            ItemActivo(icono = Icons.Filled.CalendarMonth, texto = "Citas", bg = activeItemBg, color = textActivo, onClick = onCitasClick)
        } else {
            ItemInactivo(icono = Icons.Outlined.CalendarMonth, texto = "Citas", color = textInactivo, onClick = onCitasClick)
        }

        // HISTORIAL
        if (itemActivo == NavItem.HISTORIAL) {
            ItemActivo(icono = Icons.Filled.History, texto = "Historial", bg = activeItemBg, color = textActivo, onClick = onHistorialClick)
        } else {
            ItemInactivo(icono = Icons.Outlined.History, texto = "Historial", color = textInactivo, onClick = onHistorialClick)
        }

        // SALIR
        ItemInactivo(icono = Icons.Outlined.Logout, texto = "Salir", color = textSalir, onClick = onSalirClick)
    }
}

@Composable
fun ItemActivo(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String, bg: Color, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(min = 64.dp)
            .background(bg, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icono, contentDescription = texto, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(texto, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ItemInactivo(icono: androidx.compose.ui.graphics.vector.ImageVector, texto: String, color: Color, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .widthIn(min = 64.dp)
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = icono, contentDescription = texto, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(texto, color = color, fontSize = 12.sp)
    }
}