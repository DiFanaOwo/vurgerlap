package com.example.burguerlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
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
