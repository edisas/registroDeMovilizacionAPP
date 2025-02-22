package com.cesavesin.registrodemovilizacion.ui.main

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.ui.platform.LocalContext
import com.cesavesin.registrodemovilizacion.ui.components.BottomStatusBar



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    capturedImagePath: String?,  // 🔥 Ahora recibimos la imagen aquí
    onTakePicture: () -> Unit,
    onGetLocation: () -> Unit,
    onUploadImage: () -> Unit,
    onLogout: () -> Unit,
    uploadMessage: String?
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Registro de Movilización") },
                actions = {
                    IconButton(onClick = { onLogout() }) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Cerrar sesión"
                        )
                    }
                }
            )
        },

        bottomBar = { BottomStatusBar(context) } // 🔥 Se integra la barra de estado
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Bienvenido a la Aplicación", style = MaterialTheme.typography.headlineMedium)

            Button(onClick = onTakePicture) {
                Text(text = "Tomar Foto")
            }

            Button(onClick = onGetLocation) {
                Text(text = "Obtener Ubicación")
            }
            println("📸 Imagen capturada - Paso 4: $capturedImagePath")
            Button(
                onClick = onUploadImage,
                enabled = !capturedImagePath.isNullOrEmpty()  // 🔥 Solo habilitado si hay imagen

            ) {
                Text(text = "Subir Imagen")
            }

            uploadMessage?.let {
                Text(text = it, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

