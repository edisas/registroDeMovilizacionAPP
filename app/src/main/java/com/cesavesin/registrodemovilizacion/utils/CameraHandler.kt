package com.cesavesin.registrodemovilizacion.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object CameraHandler {

    // 🔹 Guardamos la imagen como PNG
    fun saveBitmap(context: Context, bitmap: Bitmap): File? {
        return try {
            val directory = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File(directory, "captura_${System.currentTimeMillis()}.png")

            val outputStream = FileOutputStream(file)
            bitmap.compress(CompressFormat.PNG, 100, outputStream) // Guardamos en alta calidad
            outputStream.flush()
            outputStream.close()

            Log.d("CameraHandler", "📸 Imagen guardada en: ${file.absolutePath}")
            file
        } catch (e: IOException) {
            Log.e("CameraHandler", "❌ Error al guardar imagen: ${e.message}")
            null
        }
    }
}
