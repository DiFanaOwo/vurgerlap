package com.example.burguerlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.ui.theme.BurgerRed
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    var pedidos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Seguridad: Si no es el admin, expulsar a Login
    LaunchedEffect(Unit) {
        if (auth.currentUser?.email != "admin@burgerlab.com") {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Escuchar todos los pedidos pendientes o en curso
    LaunchedEffect(Unit) {
        db.collection("pedidos")
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    pedidos = snapshot.documents.map { doc ->
                        val data = doc.data?.toMutableMap() ?: mutableMapOf()
                        data["id"] = doc.id
                        data
                    }
                }
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel Admin - Pedidos", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = {
                        auth.signOut()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = BurgerRed)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(pedidos) { pedido ->
                    val id = pedido["id"] as String
                    val codigo = pedido["codigo"] as? String ?: ""
                    val estado = pedido["estado"] as? String ?: "Pendiente"
                    val tipo = pedido["tipoEntrega"] as? String ?: "Delivery"
                    val direccion = pedido["direccion"] as? String ?: ""

                    // Estado de los items para este pedido
                    var itemsPedido by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
                    
                    // Cargar items de cada pedido (Detalle)
                    LaunchedEffect(id) {
                        db.collection("pedidos").document(id).collection("items")
                            .get()
                            .addOnSuccessListener { result ->
                                itemsPedido = result.documents.map { it.data ?: emptyMap() }
                            }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pedido #$codigo", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = BurgerRed)
                                Badge(containerColor = when(estado) {
                                    "Pendiente" -> Color.Red
                                    "Preparando" -> Color.Blue
                                    "Listo", "En camino" -> Color.Magenta
                                    else -> Color.Gray
                                }) {
                                    Text(estado, color = Color.White, modifier = Modifier.padding(4.dp))
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Text("Entrega: $tipo", fontWeight = FontWeight.SemiBold)
                            if (tipo == "Delivery") {
                                Text("Dirección: $direccion", fontSize = 13.sp)
                            }

                            Divider(Modifier.padding(vertical = 8.dp))

                            // Listado de productos comprados
                            Text("Productos:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            itemsPedido.forEach { item ->
                                Text("• ${item["cantidad"]}x ${item["nombre"]}", fontSize = 14.sp)
                                if ((item["extras"] as? List<*>)?.isNotEmpty() == true) {
                                    Text("  Extras: ${(item["extras"] as List<*>).joinToString()}", fontSize = 12.sp, color = Color.Gray)
                                }
                                if (item["instrucciones"].toString().isNotBlank()) {
                                    Text("  Nota: ${item["instrucciones"]}", fontSize = 12.sp, color = Color.Gray)
                                }
                            }

                            Spacer(Modifier.height(12.dp))
                            Text("Total: ${pedido["total"]} Bs", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            
                            Spacer(Modifier.height(16.dp))
                            
                            Text("Actualizar estado:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                val states = listOf("Preparando", if(tipo == "Delivery") "En camino" else "Listo", "Entregado")
                                
                                states.forEach { s ->
                                    Button(
                                        onClick = { db.collection("pedidos").document(id).update("estado", s) },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(2.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if(estado == s) BurgerRed else Color(0xFFE0E0E0),
                                            contentColor = if(estado == s) Color.White else Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(s, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
