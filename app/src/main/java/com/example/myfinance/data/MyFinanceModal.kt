package com.example.myfinance.data

import com.google.firebase.Timestamp

data class MyFinanceModal(
    val plus: Boolean = false,
    val amount: Double = 0.0,
    val description: String = "",
    val date: Timestamp = Timestamp.now()
)

val financeList = emptyList<MyFinanceModal>()
