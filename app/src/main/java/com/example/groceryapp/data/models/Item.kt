package com.example.groceryapp.data.models

import com.google.firebase.Timestamp

/**
 * Represents a grocery item.
 */
data class Item(
    val id: String? = null,
    val name: String = "",
    val quantity: String = "",
    val buyBefore: Timestamp? = null,
    val price: Double = 0.0,
    val lastModifiedBy: String = ""
)
