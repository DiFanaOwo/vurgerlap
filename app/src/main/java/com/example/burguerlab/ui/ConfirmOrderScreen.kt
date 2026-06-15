package com.example.burguerlab.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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

    val total = items.sumOf {
        it.precioFinal * it.cantidad
    }

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
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
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

                items(items) { item ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Column {

                            AsyncImage(
                                model = item.hamburguesa.foto,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                contentScale = ContentScale.Crop
                            )

                            Column(
                                modifier = Modifier.padding(12.dp)
                            ) {

                                Text(
                                    item.hamburguesa.nombre,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    "Cantidad: ${item.cantidad}"
                                )

                                Text(
                                    "Subtotal: ${item.precioFinal * item.cantidad} Bs"
                                )
                            }
                        }
                    }
                }
            }

            Text(
                text = "Total: $total Bs",
                style = MaterialTheme.typography.headlineSmall
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Button(
                onClick = {

                    val userId =
                        FirebaseAuth.getInstance().currentUser?.uid
                            ?: return@Button

                    val firestore = FirebaseFirestore.getInstance()

                    val codigo =
                        "BL" + System.currentTimeMillis()
                            .toString()
                            .takeLast(5)

                    val pedido = hashMapOf(
                        "codigo" to codigo,
                        "usuarioId" to userId,
                        "fecha" to System.currentTimeMillis(),
                        "estado" to "Pendiente",
                        "total" to total
                    )

                    firestore
                        .collection("pedidos")
                        .add(pedido)
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

                                firestore
                                    .collection("pedidos")
                                    .document(pedidoId)
                                    .collection("items")
                                    .add(detalle)
                            }
                            CartRepository.clear()
                            Toast.makeText(
                                context,
                                "Pedido confirmado correctamente",
                                Toast.LENGTH_LONG
                            ).show()

                            navController.navigate(
                                Routes.HOME
                            ) {
                                popUpTo(Routes.HOME)
                            }
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BurgerRed
                )
            ) {
                Text("Confirmar Pedido")
            }
        }
    }
}