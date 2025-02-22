package com.cesavesin.registrodemovilizacion.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cesavesin.registrodemovilizacion.viewmodel.AuthViewModel
import androidx.compose.ui.text.input.PasswordVisualTransformation
import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.cesavesin.registrodemovilizacion.viewmodel.AuthViewModelFactory
import androidx.compose.ui.platform.LocalContext



@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    val context = LocalContext.current  // Obtener contexto
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(context))

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val errorMessage by authViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = {
                username = it
                authViewModel.onUsernameChanged(it)
            },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = password,
            onValueChange = {
                password = it
                authViewModel.onPasswordChanged(it)
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage != null) {
            Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                isLoading = true
                authViewModel.authenticateUser { success ->
                    isLoading = false
                    if (success) {
                        onLoginSuccess()
                    }
                }
            },
            enabled = !isLoading
        ) {
            Text(if (isLoading) "Autenticando..." else "Iniciar Sesión")
        }
    }
}
