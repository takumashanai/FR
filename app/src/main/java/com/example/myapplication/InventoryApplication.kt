package com.example.myapplication

import android.app.Application

class InventoryApplication : Application(){
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}