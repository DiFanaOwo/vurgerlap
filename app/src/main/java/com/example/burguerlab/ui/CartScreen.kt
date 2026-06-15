package com.example.burguerlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.repository.CartRepository
import com.example.burguerlab.ui.theme.BurgerRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {

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
                        "Carrito",
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

            if (items.isEmpty()) {

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Tu carrito está vacío")
                }

            } else {

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    itemsIndexed(items) { index, item ->

                        Card(
                            modifier = Modifier.fillMaxWidth(),
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
                                        text = item.hamburguesa.nombre,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Text(
                                        "Precio: ${item.precioFinal} Bs"
                                    )

                                    if (item.extras.isNotEmpty()) {
                                        Text(
                                            "Extras: ${item.extras.joinToString()}"
                                        )
                                    }

                                    if (item.instrucciones.isNotBlank()) {
                                        Text(
                                            "Nota: ${item.instrucciones}"
                                        )
                                    }

                                    Spacer(
                                        modifier = Modifier.height(12.dp)
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Button(
                                            onClick = {
                                                CartRepository.decrease(index)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BurgerRed
                                            )
                                        ) {
                                            Text("-")
                                        }

                                        Text(
                                            text = item.cantidad.toString(),
                                            modifier = Modifier.padding(horizontal = 16.dp)
                                        )

                                        Button(
                                            onClick = {
                                                CartRepository.increase(index)
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = BurgerRed
                                            )
                                        ) {
                                            Text("+")
                                        }

                                        Spacer(
                                            modifier = Modifier.weight(1f)
                                        )

                                        IconButton(
                                            onClick = {
                                                CartRepository.remove(item)
                                            }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Eliminar",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                HorizontalDivider()

                Spacer(
                    modifier = Modifier.height(16.dp)
                )

                Text(
                    text = "Total: $total Bs",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = {
                        navController.navigate(
                            Routes.CONFIRM_ORDER
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BurgerRed
                    )
                ) {
                    Text("Continuar Pedido")
                }
            }
        }
    }
}