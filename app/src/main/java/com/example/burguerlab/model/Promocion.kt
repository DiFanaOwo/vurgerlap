package com.example.burguerlab.model

data class Promocion(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val descuento: Int = 0,           // porcentaje, ej: 20 = 20%
    val tipo: String = "descuento",   // "descuento" | "combo" | "2x1"
    val vigenciaTexto: String = "",   // ej: "Válido hasta el 30 de junio"
    val activa: Boolean = true,
    val imagen: String = "",
    val codigoPromo: String = ""
)
