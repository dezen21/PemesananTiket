package com.example.pemesanantiket.model

data class Ticket(
        val idTicket: String? = "",
        val name: String? = "",
        val numTicket: String? = "",
        val requirement: String? = "",
        val location: String? = "",
        val date: String? = "",
        val time: String? = "",
        val totalPrice: Int? = 0
)