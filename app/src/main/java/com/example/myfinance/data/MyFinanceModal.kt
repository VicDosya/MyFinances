package com.example.myfinance.data

data class MyFinanceModal(val plus: Boolean = false, val amount: Double = 0.0, val description: String = "", val date: String = "")

val financeList = emptyList<MyFinanceModal>()
