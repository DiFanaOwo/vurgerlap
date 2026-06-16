package com.example.burguerlab.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Storefront
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
import com.example.burguerlab.ui.theme.BurgerRed
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderStatusScreen(orderId: String, navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    var orderData by remember { mutableStateOf<Map<String, Any>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Escuchar cambios en tiempo real desde Firestore
    DisposableEffect(orderId) {
        val registration = db.collection("pedidos").document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (snapshot != null && snapshot.exists()) {
                    orderData = snapshot.data
                }
                isLoading = false
            }
        onDispose { registration.remove() }
    }

    Scaffold(
        containerColor = Color(0xFFFAF5D8),
        topBar = {
            TopAppBar(
                title = { Text("Estado del Pedido", color = BurgerRed, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Inicio", tint = BurgerRed)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = BurgerRed)
            }
        } else if (orderData == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No se encontró el pedido")
            }
        } else {
            val status = orderData!!["estado"] as? String ?: "Pendiente"
            val codigo = orderData!!["codigo"] as? String ?: ""
            val fechaLong = orderData!!["fecha"] as? Long ?: 0L
            val tipoEntrega = orderData!!["tipoEntrega"] as? String ?: "Delivery"
            
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val hora = sdf.format(Date(fechaLong))

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Pedido #$codigo", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                                Text("Realizado a las $hora", color = Color.Gray, fontSize = 14.sp)
                            }
                            Badge(containerColor = BurgerRed) {
                                Text(tipoEntrega, color = Color.White, modifier = Modifier.padding(4.dp))
                            }
                        }
                        
                        Spacer(Modifier.height(32.dp))
                        
                        // Flujo de estados
                        StatusStep("Pedido recibido", status == "Pendiente" || status == "Preparando" || status == "Listo" || status == "En camino" || status == "Entregado", Icons.Default.CheckCircle)
                        StatusLine(status == "Preparando" || status == "Listo" || status == "En camino" || status == "Entregado")
                        StatusStep("En preparación", status == "Preparando" || status == "Listo" || status == "En camino" || status == "Entregado", Icons.Default.Restaurant)
                        StatusLine(status == "Listo" || status == "En camino" || status == "Entregado")
                        
                        if (tipoEntrega == "Para Llevar") {
                            StatusStep("Listo para retirar", status == "Listo" || status == "Entregado", Icons.Default.Storefront)
                        } else {
                            StatusStep("En camino", status == "En camino" || status == "Entregado", Icons.Default.LocalShipping)
                        }
                        
                        StatusLine(status == "Entregado")
                        StatusStep("Entregado", status == "Entregado", Icons.Default.Fastfood)
                    }
                }
                
                Spacer(Modifier.height(24.dp))
                
                val estimatedTime = when(status) {
                    "Pendiente" -> "30-40 min"
                    "Preparando" -> "20-25 min"
                    "En camino", "Listo" -> "10-15 min"
                    else -> "¡Buen provecho!"
                }
                
                Text("Tiempo estimado", color = Color.Gray)
                Text(estimatedTime, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = BurgerRed)

                Spacer(Modifier.weight(1f))
                
                Button(
                    onClick = { navController.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } } },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Volver al Menú")
                }
            }
        }
    }
}

@Composable
fun StatusStep(text: String, isCompleted: Boolean, icon: ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isCompleted) BurgerRed else Color.LightGray,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            text = text,
            color = if (isCompleted) Color.Black else Color.LightGray,
            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal,
            fontSize = 16.sp
        )
    }
}

@Composable
fun StatusLine(isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .padding(start = 13.dp)
            .width(2.dp)
            .height(30.dp)
            .background(if (isCompleted) BurgerRed else Color.LightGray)
    )
}
