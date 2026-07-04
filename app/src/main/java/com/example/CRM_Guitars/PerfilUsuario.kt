package com.example.CRM_Guitars

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
@Composable
fun PerfilUsuario(
    onBack: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = CitaSeleccionada.uidUsuario
        if (uid.isNotEmpty()) {
            val datos = AuthManager.obtenerDatosUsuarioPorId(uid)
            if (datos != null) {
                nombre = datos["nombre"] as? String ?: ""
                email = datos["email"] as? String ?: ""
                telefono = datos["telefono"] as? String ?: ""
            }
        }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBack, contentPadding = PaddingValues(0.dp)) {
                Text("‹", color = Color(0xFFF1BC2D), fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Perfil del Cliente",
                color = Color(0xFFF1BC2D),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Avatar
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFF2A2A2A), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(nombre, color = Color(0xFFE5E2E1), fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            // Campos de información
            CampoInfo("Nombre", nombre)
            Spacer(modifier = Modifier.height(12.dp))
            CampoInfo("Email", email)
            Spacer(modifier = Modifier.height(12.dp))
            CampoInfo("Teléfono", if (telefono.isBlank()) "No especificado" else telefono)
        }
    }
}

@Composable
fun CampoInfo(etiqueta: String, valor: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(etiqueta, color = Color(0xFF8C909F), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(valor, color = Color(0xFFE5E2E1), fontSize = 16.sp)
    }
}

