package com.example.burguerlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyOrdersScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    
    var pedidos by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        if (userId != null) {
            db.collection("pedidos")
                .whereEqualTo("usuarioId", userId)
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
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Pedidos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BurgerRed)
            }
        } else if (pedidos.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Aún no tienes pedidos realizados")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(pedidos) { pedido ->
                    val id = pedido["id"] as String
                    val codigo = pedido["codigo"] as? String ?: ""
                    val estado = pedido["estado"] as? String ?: "Pendiente"
                    val total = pedido["total"]
                    val fechaLong = pedido["fecha"] as? Long ?: 0L
                    
                    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val fechaStr = sdf.format(Date(fechaLong))

                    Card(
                        onClick = { navController.navigate(Routes.orderStatus(id)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Pedido #$codigo", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(fechaStr, fontSize = 12.sp, color = Color.Gray)
                                Text("$total Bs", fontWeight = FontWeight.Bold, color = BurgerRed)
                            }
                            
                            Badge(
                                containerColor = when(estado) {
                                    "Pendiente" -> Color(0xFFFFEBEE)
                                    "Preparando" -> Color(0xFFE3F2FD)
                                    "Listo", "En camino" -> Color(0xFFF3E5F5)
                                    "Entregado" -> Color(0xFFE8F5E9)
                                    else -> Color.LightGray
                                },
                                contentColor = when(estado) {
                                    "Pendiente" -> Color.Red
                                    "Preparando" -> Color.Blue
                                    "Listo", "En camino" -> Color.Magenta
                                    "Entregado" -> Color(0xFF2E7D32)
                                    else -> Color.DarkGray
                                }
                            ) {
                                Text(estado, modifier = Modifier.padding(6.dp), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
