package com.example.myfinance.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal

class MyFinancesAdapter(private val myFinancesList: List<MyFinanceModal>) :
    RecyclerView.Adapter<MyFinancesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.finance_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return myFinancesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val myFinance = myFinancesList[position]
        holder.financeAmount.text = myFinance.amount.toString()
        holder.financeDescription.text = myFinance.description
        holder.financeDate.text = myFinance.date
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val financeAmount: TextView = itemView.findViewById(R.id.amount_text_view)
        val financeDescription: TextView = itemView.findViewById(R.id.description_text_view)
        val financeDate: TextView = itemView.findViewById(R.id.date_text_view)
    }
}