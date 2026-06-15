package com.example.burguerlab.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.ui.theme.BurgerCream
import com.example.burguerlab.ui.theme.BurgerRed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun RegisterScreen(navController: NavController) {
    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf("") }

    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BurgerCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 40.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start
        ) {
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Crear cuenta",
                    color = Color(0xFF4E342E),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Campo Nombre
                Text("Nombre", fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    placeholder = { Text("Tu nombre completo") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = BurgerRed,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Correo
                Text("Correo electrónico", fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorText = ""
                    },
                    placeholder = { Text("correo@ejemplo.com") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = BurgerRed,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                if (errorText.isNotEmpty()) {
                    Text(errorText, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(start = 4.dp))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Contraseña
                Text("Contraseña", fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = { Text("mínimo 8 caracteres") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle password visibility")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = BurgerRed,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo Confirmar Contraseña
                Text("Confirmar contraseña", fontWeight = FontWeight.Bold, color = Color(0xFF4E342E))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = { Text("repite tu contraseña") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = "Toggle confirm password visibility")
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = BurgerRed,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )

                Spacer(modifier = Modifier.height(32.dp))

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = BurgerRed)
                } else {
                    // Botón Crear Cuenta
                    Button(
                        onClick = {
                            if (nombre.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                                Toast.makeText(context, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (password.length < 8) {
                                errorText = "La contraseña debe tener al menos 8 caracteres"
                                return@Button
                            }
                            if (password != confirmPassword) {
                                Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            isLoading = true
                            auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener { result ->
                                    // Guardar datos adicionales en Firestore
                                    val user = result.user
                                    val userData = hashMapOf(
                                        "uid" to user?.uid,
                                        "nombre" to nombre,
                                        "email" to email
                                    )
                                    user?.uid?.let { uid ->
                                        db.collection("usuarios").document(uid).set(userData)
                                    }

                                    isLoading = false
                                    navController.navigate(Routes.HOME) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                                .addOnFailureListener {
                                    isLoading = false
                                    errorText = it.message ?: "Error al registrarse"
                                }
                        },
                        modifier = Modifier.width(180.dp).height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Crear cuenta", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    TextButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.padding(top = 8.dp)
                    ) {
                        Text("¿Ya tienes cuenta? Inicia sesión", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Patrón cuadriculado
            CheckeredPattern(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }
}