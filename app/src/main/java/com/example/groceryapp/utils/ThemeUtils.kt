package com.example.groceryapp.utils

import androidx.appcompat.app.AppCompatDelegate

object ThemeUtils {
    fun applyTheme(selection: Int) {
        when(selection){
            0 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            1 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            2 -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
