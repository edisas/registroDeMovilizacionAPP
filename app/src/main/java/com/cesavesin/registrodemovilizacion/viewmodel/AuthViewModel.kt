package com.cesavesin.registrodemovilizacion.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cesavesin.registrodemovilizacion.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(context: Context) : ViewModel() {  // Agregar contexto
    private val authRepository = AuthRepository(context)  // Pasar contexto

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        _username.value = newUsername
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun authenticateUser(callback: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {  // Ejecutar en un hilo de fondo
            val success = authRepository.authenticate(_username.value, _password.value)
            if (success) {
                callback(true)
            } else {
                _errorMessage.value = "Usuario o contraseña incorrectos"
                callback(false)
            }
        }
    }
}
