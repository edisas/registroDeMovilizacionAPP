package com.cesavesin.registrodemovilizacion.ui.components

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 🔥 Nueva versión
const val APP_VERSION = "V1.000.0012"

@Composable
fun BottomStatusBar(context: Context) {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    var isOnline by remember { mutableStateOf(checkInternetConnection(connectivityManager)) }
    var locationText by remember { mutableStateOf("📍 Buscando...") }
    val coroutineScope = rememberCoroutineScope()

    // 🔹 Actualización cada 5 segundos
    LaunchedEffect(Unit) {
        while (true) {
            isOnline = checkInternetConnection(connectivityManager)
            updateLocation(context) { location ->
                locationText = location
            }
            delay(5000) // Se actualiza cada 5 segundos
        }
    }

    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isOnline) "🟢 Online" else "🔴 Offline",
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start
            )
            Text(
                text = APP_VERSION, // 🔥 Se muestra la versión en lugar del usuario
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.Center
            )
            Text(
                text = locationText,
                modifier = Modifier.weight(2f),
                textAlign = TextAlign.End
            )
        }
    }
}

private fun checkInternetConnection(connectivityManager: ConnectivityManager): Boolean {
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
}

private fun updateLocation(context: Context, callback: (String) -> Unit) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    try {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                callback("📍 ${location.latitude}, ${location.longitude}")
            } else {
                callback("📍 No disponible")
            }
        }
    } catch (e: SecurityException) {
        callback("📍 Permiso denegado")
    }
}
