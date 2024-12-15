package com.example.groceryapp.data

import com.example.groceryapp.data.models.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun createList(listName: String): String {
        val token = db.collection("lists").document().id
        db.collection("lists").document(token).set(mapOf("name" to listName)).await()
        return token
    }

    suspend fun isTokenValid(token: String): Boolean {
        val doc = db.collection("lists").document(token).get().await()
        return doc.exists()
    }

    suspend fun addItem(token: String, item: Item) {
        val docRef = db.collection("lists").document(token).collection("items").document()
        val newItem = item.copy(id = docRef.id)
        docRef.set(newItem).await()
    }

    suspend fun updateItem(token: String, item: Item) {
        item.id?.let {
            db.collection("lists").document(token).collection("items").document(it).set(item).await()
        }
    }

    suspend fun removeItem(token: String, itemId: String) {
        db.collection("lists").document(token).collection("items").document(itemId).delete().await()
    }

    suspend fun getItem(token: String, itemId: String): Item? {
        val doc = db.collection("lists").document(token).collection("items").document(itemId).get().await()
        return doc.toObject(Item::class.java)
    }
}
