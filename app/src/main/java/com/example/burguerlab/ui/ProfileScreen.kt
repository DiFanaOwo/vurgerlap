package com.example.burguerlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.ui.theme.BurgerCream
import com.example.burguerlab.ui.theme.BurgerRed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val currentUser = auth.currentUser

    // Si no hay sesión, redirigir a login
    if (currentUser == null) {
        LaunchedEffect(Unit) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(Routes.PROFILE) { inclusive = true }
            }
        }
        return
    }

    var nombre by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser.email ?: "") }
    var direccion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Cargar datos del usuario desde Firestore
    LaunchedEffect(currentUser.uid) {
        db.collection("usuarios").document(currentUser.uid)
            .get()
            .addOnSuccessListener { doc ->
                nombre = doc.getString("nombre") ?: currentUser.displayName ?: ""
                email = doc.getString("email") ?: currentUser.email ?: ""
                direccion = doc.getString("direccion") ?: ""
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    // Diálogo de confirmación para cerrar sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar sesión", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que deseas cerrar sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        auth.signOut()
                        showLogoutDialog = false
                        navController.navigate(Routes.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BurgerRed)
                ) {
                    Text("Cerrar sesión", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BurgerCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TopAppBar
            TopAppBar(
                title = {
                    Text(
                        "Mi Perfil",
                        color = BurgerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 20.sp, color = BurgerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BurgerCream
                )
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BurgerRed)
                }
            } else {
                // Avatar / ícono de perfil
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(BurgerRed.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AccountCircle,
                        contentDescription = "Avatar",
                        tint = BurgerRed,
                        modifier = Modifier.size(80.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (nombre.isNotEmpty()) {
                    Text(
                        text = nombre,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4E342E)
                    )
                }

                Text(
                    text = email,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Card con datos del usuario
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Información personal",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF4E342E),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        ProfileInfoRow(
                            icon = Icons.Filled.Person,
                            label = "Nombre",
                            value = nombre.ifEmpty { "No registrado" }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = Color(0xFFEEEEEE)
                        )

                        ProfileInfoRow(
                            icon = Icons.Filled.Email,
                            label = "Correo",
                            value = email.ifEmpty { "No registrado" }
                        )

                        if (direccion.isNotEmpty()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = Color(0xFFEEEEEE)
                            )
                            ProfileInfoRow(
                                icon = Icons.Filled.Home,
                                label = "Dirección",
                                value = direccion
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Botón cerrar sesión
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Cerrar sesión",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Patrón cuadriculado decorativo
                CheckeredPattern(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = BurgerRed,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = label,
                fontSize = 11.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color(0xFF2E2E2E),
                fontWeight = FontWeight.Normal
            )
        }
    }
}
