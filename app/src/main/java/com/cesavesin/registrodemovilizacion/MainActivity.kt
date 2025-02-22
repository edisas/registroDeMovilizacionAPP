package com.cesavesin.registrodemovilizacion

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.cesavesin.registrodemovilizacion.ui.auth.LoginScreen
import com.cesavesin.registrodemovilizacion.ui.main.MainScreen
import com.cesavesin.registrodemovilizacion.ui.theme.RegistroDeMovilizacionTheme
import com.cesavesin.registrodemovilizacion.datastore.UserPreferences
import com.cesavesin.registrodemovilizacion.utils.ApiClient
import com.cesavesin.registrodemovilizacion.ui.LoadingScreen
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var cameraLauncher: ActivityResultLauncher<Void?>
    private lateinit var locationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var cameraPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ApiClient.initialize(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val capturedImagePath = mutableStateOf<String?>(null)

        cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            bitmap?.let {
                val path = saveCapturedImage(it)
                capturedImagePath.value = path
            }
        }

        locationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) getCurrentLocation()
            else println("🚫 Permiso de ubicación denegado")
        }

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) cameraLauncher.launch(null)
            else println("🚫 Permiso de cámara denegado")
        }

        setContent {
            RegistroDeMovilizacionTheme {
                val userPreferences = remember { UserPreferences(this) }
                var isLoggedIn by remember { mutableStateOf<Boolean?>(null) }
                var uploadMessage by remember { mutableStateOf<String?>(null) }
                val coroutineScope = rememberCoroutineScope()

                LaunchedEffect(Unit) {
                    coroutineScope.launch {
                        val token = userPreferences.tokenFlow.first()
                        isLoggedIn = !token.isNullOrEmpty()
                    }
                }

                when (isLoggedIn) {
                    null -> LoadingScreen()
                    true -> MainScreen(
                        capturedImagePath = capturedImagePath.value,  // 🔥 Cambio aquí: .value
                        onTakePicture = { checkAndLaunchCamera() },
                        onGetLocation = { checkAndGetLocation() },
                        onUploadImage = {
                            capturedImagePath.value?.let { path ->
                                val imageFile = File(path)
                                uploadImage(imageFile) { success, message ->
                                    uploadMessage = if (success) "✅ Imagen subida correctamente" else "❌ Errors: $message"
                                    println("❌ Errors: $message")
                                }
                            } ?: run {
                                uploadMessage = "⚠️ No hay imagen para subir"
                            }
                        },
                        onLogout = {
                            coroutineScope.launch {
                                userPreferences.clearToken()
                                isLoggedIn = false
                            }
                        },
                        uploadMessage = uploadMessage
                    )
                    false -> LoginScreen(
                        onLoginSuccess = { isLoggedIn = true }
                    )
                }
            }
        }
    }

    private fun checkAndLaunchCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraLauncher.launch(null)
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun saveCapturedImage(bitmap: Bitmap): String? {
        return try {
            val directory = getExternalFilesDir(null)
            if (directory == null) {
                println("❌ No se pudo acceder al directorio de almacenamiento")
                return null
            }
            val file = File(directory, "captura_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            println("📸 Imagen guardada en: ${file.path}")
            file.path
        } catch (e: Exception) {
            println("❌ Error al guardar imagen: ${e.message}")
            null
        }
    }

    private fun checkAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        } else {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                println("📍 Ubicación actual: ${it.latitude}, ${it.longitude}")
            }
        }
    }

    private fun uploadImage(imageFile: File, callback: (Boolean, String) -> Unit) {
        println("🚀 Intentando subir imagen desde: ${imageFile.absolutePath}")
        ApiClient.uploadImage(imageFile, callback)
    }
}