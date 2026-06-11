package com.example.burguerlab.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import com.example.burguerlab.model.Hamburguesa
import com.example.burguerlab.ui.theme.BurgerRed  // ← importado desde theme

val EXTRAS = listOf(
    "Queso extra"    to 2,
    "Tocino"         to 3,
    "Jalapeños"      to 2,
    "Pepinillos"     to 1,
    "Salsa especial" to 1
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BurguerDetailScreen(hamburguesaId: String, navController: NavController) {

    var burger by remember { mutableStateOf<Hamburguesa?>(null) }

    val extrasState = remember {
        mutableStateMapOf<String, Boolean>().apply {
            EXTRAS.forEach { (nombre, _) -> put(nombre, false) }
        }
    }

    var instrucciones by remember { mutableStateOf("") }

    LaunchedEffect(hamburguesaId) {
        Firebase.firestore.collection("hamburguesa")
            .document(hamburguesaId)
            .get()
            .addOnSuccessListener { doc ->
                burger = Hamburguesa(
                    id          = doc.id,
                    nombre      = doc.getString("nombre") ?: "",
                    descripcion = doc.getString("descripcion") ?: "",
                    precio      = doc.getDouble("precio") ?: 0.0,
                    foto        = doc.getString("foto") ?: ""
                )
            }
    }

    if (burger == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BurgerRed)
        }
        return
    }

    val b = burger!!

    val precioExtras = EXTRAS
        .filter { (nombre, _) -> extrasState[nombre] == true }
        .sumOf { (_, precio) -> precio }
    val precioTotal = b.precio + precioExtras

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(b.nombre, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 20.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            AsyncImage(
                model = b.foto.ifBlank { null },
                contentDescription = b.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(220.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(b.nombre, fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(b.descripcion, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("${b.precio}Bs", color = BurgerRed, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            Text("Extras", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            EXTRAS.forEach { (nombre, precio) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row {
                        Checkbox(
                            checked = extrasState[nombre] ?: false,
                            onCheckedChange = { extrasState[nombre] = it },
                            colors = CheckboxDefaults.colors(checkedColor = BurgerRed)
                        )
                        Text("$nombre +${precio}Bs", modifier = Modifier.padding(start = 4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = instrucciones,
                onValueChange = { instrucciones = it },
                placeholder = { Text("Instrucciones especiales... Ej: Sin cebolla") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val authUser = FirebaseAuth.getInstance().currentUser
            Button(
                onClick = {
                    if (authUser != null) {
                        // TODO: lógica real del carrito
                        navController.popBackStack()
                    } else {
                        navController.navigate(com.example.burguerlab.navigation.Routes.LOGIN)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = BurgerRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    if (authUser != null) "Agregar al carrito — ${precioTotal}Bs" else "Inicia sesión para comprar",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
