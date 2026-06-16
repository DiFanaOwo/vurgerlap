package com.example.burguerlab.ui

import android.widget.Toast
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.repository.CartRepository
import com.example.burguerlab.ui.theme.BurgerRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfirmOrderScreen(
    navController: NavController
) {

    val context = LocalContext.current
    val items = CartRepository.items
    val total = items.sumOf { it.precioFinal * it.cantidad }

    // Estados para la entrega
    var deliveryType by remember { mutableStateOf("Delivery") } // "Delivery" o "Para Llevar"
    var address by remember { mutableStateOf("") }
    var addressError by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = Color(0xFFFAF5D8),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Confirmar Pedido",
                        color = BurgerRed,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = BurgerRed
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                item {
                    Text(
                        "Resumen de productos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(items) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = item.hamburguesa.foto,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(item.hamburguesa.nombre, fontWeight = FontWeight.Bold)
                                Text("Cantidad: ${item.cantidad}")
                                Text("Subtotal: ${item.precioFinal * item.cantidad} Bs", color = BurgerRed)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Tipo de entrega",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Selector Delivery
                        FilterChip(
                            selected = deliveryType == "Delivery",
                            onClick = { deliveryType = "Delivery" },
                            label = { Text("Delivery") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BurgerRed,
                                selectedLabelColor = Color.White
                            )
                        )
                        // Selector Para Llevar
                        FilterChip(
                            selected = deliveryType == "Para Llevar",
                            onClick = { deliveryType = "Para Llevar" },
                            label = { Text("Para Llevar") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BurgerRed,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    if (deliveryType == "Delivery") {
                        Text(
                            "Dirección de entrega",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        OutlinedTextField(
                            value = address,
                            onValueChange = {
                                address = it
                                addressError = false
                            },
                            placeholder = { Text("Escribe tu dirección exacta...") },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            isError = addressError,
                            supportingText = {
                                if (addressError) Text("La dirección es obligatoria para Delivery")
                            },
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
                        ) {
                            Text(
                                "Retira tu pedido en nuestra sucursal central.",
                                modifier = Modifier.padding(12.dp),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Total a pagar: $total Bs",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validar dirección si es Delivery
                    if (deliveryType == "Delivery" && address.isBlank()) {
                        addressError = true
                        Toast.makeText(context, "Por favor ingresa una dirección", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                    val firestore = FirebaseFirestore.getInstance()
                    val codigo = "BL" + System.currentTimeMillis().toString().takeLast(5)

                    val pedido = hashMapOf(
                        "codigo" to codigo,
                        "usuarioId" to userId,
                        "fecha" to System.currentTimeMillis(),
                        "estado" to "Pendiente",
                        "total" to total,
                        "tipoEntrega" to deliveryType,
                        "direccion" to if (deliveryType == "Delivery") address else "Retiro en local"
                    )

                    firestore.collection("pedidos").add(pedido)
                        .addOnSuccessListener { pedidoDoc ->
                            val pedidoId = pedidoDoc.id
                            items.forEach { item ->
                                val detalle = hashMapOf(
                                    "hamburguesaId" to item.hamburguesa.id,
                                    "nombre" to item.hamburguesa.nombre,
                                    "foto" to item.hamburguesa.foto,
                                    "cantidad" to item.cantidad,
                                    "precioUnitario" to item.precioFinal,
                                    "subtotal" to (item.precioFinal * item.cantidad),
                                    "extras" to item.extras,
                                    "instrucciones" to item.instrucciones
                                )
                                firestore.collection("pedidos").document(pedidoId)
                                    .collection("items").add(detalle)
                            }
                            CartRepository.clear()
                            Toast.makeText(context, "¡Pedido enviado con éxito!", Toast.LENGTH_LONG).show()
                            navController.navigate(Routes.orderStatus(pedidoId)) {
                                popUpTo(Routes.HOME) { inclusive = false }
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Confirmar y Pagar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
