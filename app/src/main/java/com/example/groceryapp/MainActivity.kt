package com.example.groceryapp

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.groceryapp.databinding.ActivityMainBinding
import com.example.groceryapp.utils.ThemeUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val token = prefs.getString("list_token", null)
        val nickname = prefs.getString("nickname", null)
        val selectedTheme = prefs.getInt("theme", 0)

        when (selectedTheme) {
            0 -> setTheme(R.style.Theme_GroceryApp_Light)
            1 -> setTheme(R.style.Theme_GroceryApp_Dark)
            2 -> setTheme(R.style.Theme_GroceryApp_Custom)
        }


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the toolbar as the ActionBar
        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        if (token != null && nickname != null) {
            navController.navigate(R.id.listHomeFragment)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

