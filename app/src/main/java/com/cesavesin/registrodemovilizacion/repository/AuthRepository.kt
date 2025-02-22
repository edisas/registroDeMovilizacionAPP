package com.cesavesin.registrodemovilizacion.repository

import android.content.Context
import com.cesavesin.registrodemovilizacion.datastore.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class AuthRepository(private val context: Context) {
    private val client = OkHttpClient()
    private val userPreferences = UserPreferences(context)

    suspend fun authenticate(username: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            val requestBody = FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build()

            val request = Request.Builder()
                .url("https://prawn-cunning-koala.ngrok-free.app/auth/token")  // Ajusta tu URL
                .post(requestBody)
                .build()

            println("📡 Enviando solicitud a: ${request.url}")
            println("📋 Datos enviados: username=$username, password=$password")

            try {
                val response = client.newCall(request).execute()

                println("✅ Código de respuesta: ${response.code}")

                if (!response.isSuccessful) {
                    println("❌ Error en la solicitud. Código: ${response.code}")
                    return@withContext false
                }

                val responseBody = response.body?.string()
                println("📨 Respuesta del servidor: $responseBody")

                val json = JSONObject(responseBody ?: "")
                val token = json.optString("access_token")

                return@withContext if (token.isNotEmpty()) {
                    println("🎉 Token recibido y almacenado: $token")
                    userPreferences.saveToken(token)  // Guardar el token en DataStore
                    true
                } else {
                    println("⚠️ No se recibió token válido")
                    false
                }
            } catch (e: IOException) {
                println("🚨 Error de conexión: ${e.message}")
                return@withContext false
            }
        }
    }
}
