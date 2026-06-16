package com.example.burguerlab.repository

import com.example.burguerlab.model.Promocion
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow

class PromocionRepo {

    private val db = FirebaseFirestore.getInstance()

    /** Escucha en tiempo real la colección "promociones" en Firestore.
     *  Si la colección no existe o está vacía, devuelve promociones de ejemplo. */
    fun getPromociones(): Flow<List<Promocion>> = callbackFlow {
        val listener = db.collection("promociones")
            .whereEqualTo("activa", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    trySend(promocionesEjemplo())
                    return@addSnapshotListener
                }

                val lista = snapshot.documents.mapNotNull { doc ->
                    try {
                        Promocion(
                            id           = doc.id,
                            titulo       = doc.getString("titulo") ?: "",
                            descripcion  = doc.getString("descripcion") ?: "",
                            descuento    = (doc.getLong("descuento") ?: 0L).toInt(),
                            tipo         = doc.getString("tipo") ?: "descuento",
                            vigenciaTexto = doc.getString("vigenciaTexto") ?: "",
                            activa       = doc.getBoolean("activa") ?: true,
                            imagen       = doc.getString("imagen") ?: "",
                            codigoPromo  = doc.getString("codigoPromo") ?: ""
                        )
                    } catch (e: Exception) { null }
                }

                // Si Firestore devuelve vacío, usar datos de ejemplo
                trySend(if (lista.isEmpty()) promocionesEjemplo() else lista)
            }

        awaitClose { listener.remove() }
    }

    /** Promociones hardcodeadas para cuando Firestore aún no tiene datos */
    private fun promocionesEjemplo(): List<Promocion> = listOf(
        Promocion(
            id           = "promo1",
            titulo       = "¡20% OFF en tu primer pedido!",
            descripcion  = "Regístrate y obtén un 20% de descuento en tu primera hamburguesa.",
            descuento    = 20,
            tipo         = "descuento",
            vigenciaTexto = "Válido hasta el 30 de julio",
            activa       = true,
            codigoPromo  = "PRIMERA20"
        ),
        Promocion(
            id           = "promo2",
            titulo       = "Combo Doble Impacto",
            descripcion  = "2 hamburguesas clásicas + 2 papas + 2 bebidas al precio de 1 combo.",
            descuento    = 0,
            tipo         = "combo",
            vigenciaTexto = "Solo los viernes y sábados",
            activa       = true,
            codigoPromo  = "COMBO2X1"
        ),
        Promocion(
            id           = "promo3",
            titulo       = "2x1 en Burger Especial",
            descripcion  = "Lleva dos Burger Especiales y paga solo una. ¡La segunda es gratis!",
            descuento    = 50,
            tipo         = "2x1",
            vigenciaTexto = "Válido hasta el 15 de julio",
            activa       = true,
            codigoPromo  = "2X1ESPECIAL"
        ),
        Promocion(
            id           = "promo4",
            titulo       = "Martes de Descuento",
            descripcion  = "Todos los martes 15% OFF en cualquier hamburguesa del menú.",
            descuento    = 15,
            tipo         = "descuento",
            vigenciaTexto = "Todos los martes",
            activa       = true,
            codigoPromo  = "MARTES15"
        )
    )
}
