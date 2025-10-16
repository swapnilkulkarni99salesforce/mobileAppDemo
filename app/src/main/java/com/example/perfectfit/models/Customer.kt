package com.example.perfectfit.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val address: String,
    val mobile: String,
    val alternateMobile: String,
    val birthDate: String
) {
    // Computed property for display
    val fullName: String
        get() = "$firstName $lastName"
}

