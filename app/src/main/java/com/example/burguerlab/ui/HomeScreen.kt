package com.example.burguerlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.burguerlab.model.Hamburguesa
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.repository.HamburguesaRepo
import com.example.burguerlab.ui.theme.BurgerRed  // ← importado desde theme, no redefinido

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val repo = remember { HamburguesaRepo() }
    val hamburguesas by repo.getHamburguesas().collectAsState(initial = emptyList())
    var query by remember { mutableStateOf("") }

    val filtradas = hamburguesas.filter {
        it.nombre.contains(query, ignoreCase = true)
    }

    val currentUser = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Burger Lab",
                        color = BurgerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )
                },
                actions = {
                    // Botón Promociones
                    IconButton(onClick = { navController.navigate(Routes.PROMOTIONS) }) {
                        Icon(
                            imageVector = Icons.Filled.LocalOffer,
                            contentDescription = "Promociones",
                            tint = BurgerRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    // Botón Perfil
                    IconButton(onClick = {
                        if (currentUser != null) {
                            navController.navigate(Routes.PROFILE)
                        } else {
                            navController.navigate(Routes.LOGIN)
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "Perfil",
                            tint = BurgerRed,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(horizontal = 16.dp)) {

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Buscar hamburguesas...") },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Banner promociones
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = BurgerRed),
                onClick = { navController.navigate(Routes.PROMOTIONS) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏷️", fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("¡Descuentos y combos disponibles!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text("Ver →", color = Color.White, fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(filtradas) { burger ->
                    BurgerCard(
                        hamburguesa = burger,
                        onClick = { navController.navigate(Routes.detail(burger.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun BurgerCard(hamburguesa: Hamburguesa, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            AsyncImage(
                model = hamburguesa.foto.ifBlank { null },
                contentDescription = hamburguesa.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(hamburguesa.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(hamburguesa.descripcion, fontSize = 13.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${hamburguesa.precio.toInt()}Bs",
                        color = BurgerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Button(
                        onClick = onClick,
                        colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Agregar", color = Color.White)
                    }
                }
            }
        }
    }
}
