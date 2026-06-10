package com.example.burguerlab.ui

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

// Patrón cuadriculado decorativo usado en Login, Register y Splash
@Composable
fun CheckeredPattern(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val squareSize = 40f
        val cols = (size.width / squareSize).toInt() + 1
        val rows = (size.height / squareSize).toInt() + 1

        for (row in 0..rows) {
            for (col in 0..cols) {
                val isRed = (row + col) % 2 == 0
                drawRect(
                    color = if (isRed) Color(0xFFD84040) else Color(0xFFFEF7E7),
                    topLeft = Offset(col * squareSize, row * squareSize),
                    size = Size(squareSize, squareSize)
                )
            }
        }
    }
}
