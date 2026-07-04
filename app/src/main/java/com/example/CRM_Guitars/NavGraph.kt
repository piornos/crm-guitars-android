package com.example.CRM_Guitars

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val INICIO = "inicio"
    const val PANEL_ADMIN = "panel_admin"
    const val CALENDARIO = "calendario"
    const val AGENDAR_CITA = "agendar_cita"
    const val ESTADO_CITA = "estado_cita"
    const val PERFIL = "perfil"
    const val HISTORIAL = "historial"
    const val PERFIL_USUARIO = "perfil_usuario"

    const val AGENDA = "agenda"
}

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {
    var esAdminActual by remember { mutableStateOf(false) }
    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.AGENDA) {
            Agenda(
                onBack = { navController.popBackStack() },
                onVerPerfil = { navController.navigate(Rutas.PERFIL_USUARIO) }
            )
        }
        composable(Rutas.LOGIN) {
            LoginScreen(
                onLoginSuccess = { esAdmin ->
                    esAdminActual = esAdmin
                    if (esAdmin) {
                        navController.navigate(Rutas.PANEL_ADMIN)
                    } else {
                        navController.navigate(Rutas.INICIO)
                    }
                },
                onRegisterClick = { navController.navigate(Rutas.REGISTRO) },
                onForgotPasswordClick = { },
                onGoogleSignInClick = { navController.navigate(Rutas.INICIO) }
            )
        }
        composable(Rutas.INICIO) {
            Inicio(
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onCitasClick = { navController.navigate(Rutas.CALENDARIO) },
                onHistorialClick = { navController.navigate(Rutas.HISTORIAL) },
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }            )
        }
        composable(Rutas.PANEL_ADMIN) {
            PanelAdmin(
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVerDetalleCita = { navController.navigate(Rutas.ESTADO_CITA) },
                onVerAgenda = { navController.navigate(Rutas.AGENDA) }
            )
        }
        composable(Rutas.REGISTRO) {
            Registro(onLoginClick = { navController.popBackStack() })
        }
        composable(Rutas.CALENDARIO) {
            Calendario(
                onNewAppointment = { navController.navigate(Rutas.AGENDAR_CITA) },
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onCitasClick = { navController.navigate(Rutas.CALENDARIO) },
                onHistorialClick = { navController.navigate(Rutas.HISTORIAL) },
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }            )
        }
        composable(Rutas.AGENDAR_CITA) {
            AgendarCita(onBack = { navController.popBackStack() },
                onCitaEnviada = { navController.navigate(Rutas.INICIO) }
            )

        }
        composable(Rutas.ESTADO_CITA) {
            EstadoCita(
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onCitasClick = { navController.navigate(Rutas.CALENDARIO) },
                onHistorialClick = { navController.navigate(Rutas.HISTORIAL) },
                onGuardadoExitoso = { navController.navigate(Rutas.PANEL_ADMIN) },
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },
                onVerPerfilUsuario = { navController.navigate(Rutas.PERFIL_USUARIO) },
                esAdmin = esAdminActual
                )
        }
        composable(Rutas.PERFIL) {
            Perfil(
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onCitasClick = { navController.navigate(Rutas.CALENDARIO) },
                onHistorialClick = { navController.navigate(Rutas.HISTORIAL) },
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },                onGuardarExitoso = { navController.navigate(Rutas.INICIO) }
            )
        }
        composable(Rutas.PERFIL_USUARIO) {
            PerfilUsuario(
                onBack = { navController.popBackStack() }
            )
        }
        composable(Rutas.HISTORIAL) {
            Historial(
                onBack = { navController.popBackStack() },
                onPerfilClick = { navController.navigate(Rutas.PERFIL) },
                onCitasClick = { navController.navigate(Rutas.CALENDARIO) },
                onHistorialClick = { navController.navigate(Rutas.HISTORIAL) },
                onSalirClick = {
                    AuthManager.cerrarSesion()
                    navController.navigate(Rutas.LOGIN) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                },                onVerEstadoCita = { navController.navigate(Rutas.ESTADO_CITA) }
            )
        }
    }
}