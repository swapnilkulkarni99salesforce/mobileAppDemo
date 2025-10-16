package com.example.perfectfit.models

data class Order(
    val id: Int,
    val orderId: String,
    val customerName: String,
    val date: String,
    val amount: String,
    val status: String
)

