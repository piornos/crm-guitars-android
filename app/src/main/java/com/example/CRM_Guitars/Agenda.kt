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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ClienteAgenda(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val telefono: String = ""
)

@Composable
fun Agenda(
    onBack: () -> Unit,
    onVerPerfil: (String) -> Unit = {}
) {
    var clientes by remember { mutableStateOf<List<ClienteAgenda>>(emptyList()) }
    var cargando by remember { mutableStateOf(true) }
    var busqueda by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val datos = AuthManager.obtenerTodosLosUsuarios()
        clientes = datos.map { usuario ->
            ClienteAgenda(
                uid = usuario["uid"] as? String ?: "",
                nombre = usuario["nombre"] as? String ?: "",
                email = usuario["email"] as? String ?: "",
                telefono = usuario["telefono"] as? String ?: ""
            )
        }
        cargando = false
    }

    val clientesFiltrados = clientes.filter {
        it.nombre.contains(busqueda, ignoreCase = true) ||
                it.email.contains(busqueda, ignoreCase = true)
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
                text = "Agenda de Clientes",
                color = Color(0xFFF1BC2D),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Buscador
            OutlinedTextField(
                value = busqueda,
                onValueChange = { busqueda = it },
                placeholder = { Text("🔍  Buscar cliente...", color = Color(0xFFC2C6D6)) },
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
                text = "CLIENTES",
                color = Color(0xFFFACC15),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (cargando) {
                CircularProgressIndicator(color = Color(0xFFFACC15))
            } else if (clientesFiltrados.isEmpty()) {
                Text("No hay clientes registrados", color = Color(0xFFC2C6D6), fontSize = 14.sp)
            } else {
                clientesFiltrados.forEach { cliente ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF1E1E1E), RoundedCornerShape(12.dp))
                            .clickable {
                                CitaSeleccionada.uidUsuario = cliente.uid
                                CitaSeleccionada.nombreUsuario = cliente.nombre
                                onVerPerfil(cliente.uid)
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(Color(0xFF2A2A2A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("👤", fontSize = 22.sp)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                cliente.nombre,
                                color = Color(0xFFE5E2E1),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(cliente.email, color = Color(0xFFC2C6D6), fontSize = 13.sp)
                            if (cliente.telefono.isNotBlank()) {
                                Text(cliente.telefono, color = Color(0xFF8C909F), fontSize = 12.sp)
                            }
                        }
                        Text("›", color = Color(0xFFC2C6D6), fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

