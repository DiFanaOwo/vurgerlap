package com.example.burguerlab.model

data class CartItem(
    val hamburguesa: Hamburguesa,
    var cantidad: Int = 1,
    val extras: List<String> = emptyList(),
    val instrucciones: String = "",
    val precioFinal: Double = 0.0
)