package com.example.burguerlab.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.burguerlab.model.Promocion
import com.example.burguerlab.repository.PromocionRepo
import com.example.burguerlab.ui.theme.BurgerCream
import com.example.burguerlab.ui.theme.BurgerRed

// Colores auxiliares para las tarjetas
private val BurgerGold   = Color(0xFFF5A623)
private val BurgerGreen  = Color(0xFF2E7D32)
private val BurgerOrange = Color(0xFFE65100)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionScreen(navController: NavController) {
    val repo = remember { PromocionRepo() }
    val promociones by repo.getPromociones().collectAsState(initial = emptyList())

    val descuentos = promociones.filter { it.tipo == "descuento" }
    val combos     = promociones.filter { it.tipo == "combo" }
    val dosXuno    = promociones.filter { it.tipo == "2x1" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Promociones",
                        color = BurgerRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Text("←", fontSize = 20.sp, color = BurgerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BurgerCream)
            )
        },
        containerColor = BurgerCream
    ) { padding ->
        if (promociones.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = BurgerRed)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // ── BANNER PRINCIPAL ──────────────────────────────────────────
            item {
                PromoBanner()
                Spacer(modifier = Modifier.height(20.dp))
            }

            // ── SECCIÓN DESCUENTOS ────────────────────────────────────────
            if (descuentos.isNotEmpty()) {
                item {
                    SeccionTitulo(
                        icon  = Icons.Filled.LocalOffer,
                        texto = "Descuentos especiales"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(descuentos) { promo ->
                            TarjetaDescuento(promo)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }

            // ── SECCIÓN 2x1 ───────────────────────────────────────────────
            if (dosXuno.isNotEmpty()) {
                item {
                    SeccionTitulo(
                        icon  = Icons.Filled.Star,
                        texto = "Ofertas 2x1"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(dosXuno) { promo ->
                    TarjetaDestacada(promo, gradientColors = listOf(BurgerRed, BurgerOrange))
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            // ── SECCIÓN COMBOS ────────────────────────────────────────────
            if (combos.isNotEmpty()) {
                item {
                    SeccionTitulo(
                        icon  = Icons.Filled.Restaurant,
                        texto = "Combos promocionales"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
                items(combos) { promo ->
                    TarjetaDestacada(promo, gradientColors = listOf(BurgerGreen, Color(0xFF66BB6A)))
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }

            // ── PIE ───────────────────────────────────────────────────────
            item {
                Text(
                    "* Las promociones no son acumulables entre sí.",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── BANNER ANIMADO ────────────────────────────────────────────────────────────
@Composable
private fun PromoBanner() {
    val infiniteTransition = rememberInfiniteTransition(label = "banner")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "shimmer"
    )
    val bgColor = Color(
        red   = (0.85f + shimmer * 0.05f).coerceIn(0f, 1f),
        green = (0.25f - shimmer * 0.05f).coerceIn(0f, 1f),
        blue  = (0.25f - shimmer * 0.05f).coerceIn(0f, 1f)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(
                Brush.horizontalGradient(listOf(bgColor, BurgerOrange.copy(alpha = 0.85f)))
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "🍔  OFERTAS DEL DÍA",
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Descuentos que no puedes perder",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape  = RoundedCornerShape(20.dp),
                color  = Color.White.copy(alpha = 0.25f)
            ) {
                Text(
                    "  ¡Aprovecha ahora!  ",
                    fontSize = 12.sp,
                    color    = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
                )
            }
        }
    }
}

// ── TÍTULO DE SECCIÓN ─────────────────────────────────────────────────────────
@Composable
private fun SeccionTitulo(icon: ImageVector, texto: String) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = BurgerRed, modifier = Modifier.size(22.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(texto, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF2E2E2E))
    }
}

// ── TARJETA DE DESCUENTO (horizontal scroll) ──────────────────────────────────
@Composable
private fun TarjetaDescuento(promo: Promocion) {
    Card(
        modifier = Modifier.width(200.dp),
        shape    = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors   = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Cabecera roja con porcentaje
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .background(
                        Brush.verticalGradient(listOf(BurgerRed, BurgerOrange))
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (promo.descuento > 0) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${promo.descuento}%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text("DESCUENTO", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                } else {
                    Text("🎁", fontSize = 40.sp)
                }
            }
            // Contenido
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    promo.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 14.sp,
                    maxLines   = 2,
                    overflow   = TextOverflow.Ellipsis,
                    color      = Color(0xFF2E2E2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    promo.descripcion,
                    fontSize  = 11.sp,
                    color     = Color.Gray,
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                VigenciaChip(promo.vigenciaTexto)
                if (promo.codigoPromo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    CodigoChip(promo.codigoPromo)
                }
            }
        }
    }
}

// ── TARJETA DESTACADA (ancho completo, para combos y 2x1) ────────────────────
@Composable
private fun TarjetaDestacada(promo: Promocion, gradientColors: List<Color>) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row {
            // Franja lateral con gradiente
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(130.dp)
                    .background(Brush.verticalGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        when (promo.tipo) {
                            "2x1"   -> "2x1"
                            "combo" -> "COMBO"
                            else    -> "${promo.descuento}%"
                        },
                        fontSize   = if (promo.tipo == "2x1") 22.sp else 16.sp,
                        fontWeight = FontWeight.Black,
                        color      = Color.White,
                        textAlign  = TextAlign.Center
                    )
                    if (promo.tipo == "2x1") {
                        Text("GRATIS", fontSize = 10.sp, color = Color.White.copy(alpha = 0.85f))
                    }
                }
            }
            // Texto
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp)
            ) {
                Text(
                    promo.titulo,
                    fontWeight = FontWeight.Bold,
                    fontSize   = 15.sp,
                    color      = Color(0xFF2E2E2E)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    promo.descripcion,
                    fontSize = 12.sp,
                    color    = Color.Gray,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(8.dp))
                VigenciaChip(promo.vigenciaTexto)
                if (promo.codigoPromo.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    CodigoChip(promo.codigoPromo)
                }
            }
        }
    }
}

// ── CHIP VIGENCIA ─────────────────────────────────────────────────────────────
@Composable
private fun VigenciaChip(texto: String) {
    if (texto.isEmpty()) return
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            Icons.Filled.Timer,
            contentDescription = null,
            tint   = BurgerGold,
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(3.dp))
        Text(texto, fontSize = 10.sp, color = BurgerGold, fontWeight = FontWeight.Medium)
    }
}

// ── CHIP CÓDIGO PROMO ─────────────────────────────────────────────────────────
@Composable
private fun CodigoChip(codigo: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .border(1.dp, BurgerRed.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .background(BurgerRed.copy(alpha = 0.07f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            "Código: $codigo",
            fontSize   = 10.sp,
            color      = BurgerRed,
            fontWeight = FontWeight.Bold
        )
    }
}
