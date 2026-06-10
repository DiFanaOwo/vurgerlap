package com.example.burguerlab.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.burguerlab.R
import com.example.burguerlab.navigation.Routes
import com.example.burguerlab.ui.theme.BurgerCream
import com.example.burguerlab.ui.theme.BurgerRed
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    // Navegar después de 3 segundos
    LaunchedEffect(Unit) {
        delay(3000)
        val currentUser = FirebaseAuth.getInstance().currentUser
        val destination = if (currentUser != null) Routes.HOME else Routes.LOGIN

        navController.navigate(destination) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BurgerCream)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.Start
        ) {
            // Textos superiores
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "BURGER LAB",
                    color = BurgerRed,
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Hamburguesas científicamente deliciosas",
                    color = Color.Black.copy(alpha = 0.7f),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Loading...",
                    color = BurgerRed,
                    fontSize = 14.sp
                )
            }

            // Logo centrado en el espacio restante
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                // Imagen del logo (debe estar en res/drawable/logo_burguer_lab)
                Image(
                    painter = painterResource(id = R.drawable.logo_burguer_lab),
                    contentDescription = "Logo Burger Lab",
                    modifier = Modifier.size(280.dp)
                )
            }

            // Patrón cuadriculado al fondo
            CheckeredPattern(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            )
        }
    }
}