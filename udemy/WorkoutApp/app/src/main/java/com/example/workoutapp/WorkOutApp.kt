package com.example.workoutapp

import android.app.Application
import com.example.workoutapp.database.HistoryDatabase

class WorkOutApp: Application() {
    val db: HistoryDatabase by lazy {
        HistoryDatabase.getInstance(this)
    }
}