package com.example.groceryapp.viewmodel

import androidx.lifecycle.*
import com.example.groceryapp.data.models.Item
import com.google.firebase.firestore.FirebaseFirestore

class ListViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>()
    val items: LiveData<List<Item>> get() = _items

    fun loadItems(token: String) {
        val db = FirebaseFirestore.getInstance()
        db.collection("lists").document(token).collection("items")
            .addSnapshotListener { snapshot, e ->
                if (e != null) return@addSnapshotListener
                if (snapshot != null && !snapshot.isEmpty) {
                    val list = snapshot.toObjects(Item::class.java)
                    _items.value = list
                } else {
                    _items.value = emptyList()
                }
            }
    }
}
