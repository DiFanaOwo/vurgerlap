package com.example.burguerlab.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.burguerlab.model.CartItem

object CartRepository {

    private val _items = mutableStateListOf<CartItem>()

    val items: List<CartItem>
        get() = _items

    fun add(item: CartItem) {
        _items.add(item)
    }

    fun remove(item: CartItem) {
        _items.remove(item)
    }

    fun increase(index: Int) {
        val item = _items[index]

        _items[index] = item.copy(
            cantidad = item.cantidad + 1
        )
    }

    fun decrease(index: Int) {
        val item = _items[index]

        if (item.cantidad > 1) {
            _items[index] = item.copy(
                cantidad = item.cantidad - 1
            )
        }
    }

    fun clear() {
        _items.clear()
    }
}