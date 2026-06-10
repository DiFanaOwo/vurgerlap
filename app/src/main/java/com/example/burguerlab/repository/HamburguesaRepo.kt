package com.example.burguerlab.repository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.example.burguerlab.model.Hamburguesa
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
class HamburguesaRepo {

    // Referencia a la colección "hamburguesa" en Firestore
    private val col = Firebase.firestore.collection("hamburguesa")

    // Retorna un Flow que emite la lista cada vez que Firestore cambia
    fun getHamburguesas(): Flow<List<Hamburguesa>> = callbackFlow {

        // Escucha en tiempo real la colección entera
        val listener = col.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            // Convierte cada documento a un objeto Hamburguesa
            val lista = snapshot.documents.map { doc ->
                Hamburguesa(
                    id          = doc.id,
                    nombre      = doc.getString("nombre") ?: "",
                    descripcion = doc.getString("descripcion") ?: "",
                    precio      = doc.getDouble("precio") ?: 0.0,
                    foto        = doc.getString("foto") ?: ""
                )
            }
            trySend(lista)   // envía la lista al Flow
        }

        // Cuando el Flow se cancela, elimina el listener para no tener fugas
        awaitClose { listener.remove() }
    }
}