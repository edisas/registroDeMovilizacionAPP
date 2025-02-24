package com.cesavesin.registrodemovilizacion.utils

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import android.content.Context
import com.cesavesin.registrodemovilizacion.datastore.UserPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

object ApiClient {

    private val client = OkHttpClient()
    private lateinit var userPreferences: UserPreferences

    // 🔹 Método para inicializar `UserPreferences`
    fun initialize(context: Context) {
        userPreferences = UserPreferences(context)
    }

    fun uploadImage(imageFile: File, callback: (Boolean, String) -> Unit) {
        // 🔥 Obtener el token almacenado en `UserPreferences`
        val token = runBlocking { userPreferences.tokenFlow.first() }
        if (token.isNullOrEmpty()) {
            callback(false, "⚠️ No se encontró un token de autenticación")
            return
        }
        println("🔑 1Usando token para autenticación: $token")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", imageFile.name, imageFile.asRequestBody("image/png".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("https://prawn-cunning-koala.ngrok-free.app/files/upload") // ✅ Enlace del API
            .post(requestBody)
            .addHeader("Authorization", "Bearer $token") // ✅ Se agrega el token correctamente
            .addHeader("Content-Type", "multipart/form-data") // ✅ Se especifica correctamente el tipo de contenido
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, "❌ Error: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                callback(response.isSuccessful, response.body?.string() ?: "Sin respuesta")
            }
        })
    }
}
object ApiConfig {
    // URL base del API (cámbiala aquí cuando necesites)
    const val BASE_URL = "https://prawn-cunning-koala.ngrok-free.app"

    // Ejemplo de endpoints (opcional, para referencia)
    const val VALIDATE_TOKEN = "/auth/validate-token"
    const val LOGIN = "/auth/login" // Puedes agregar más según tu API
}