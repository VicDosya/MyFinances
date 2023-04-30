package com.example.myfinance.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import java.text.SimpleDateFormat
import java.util.Locale

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
        //If plus variable is true, set it to "+" string, otherwise, "-"
        val symbol = if (myFinance.plus) "+" else "-"
        //Depending on the symbol of the plus variable, decide its color
        val color = if (myFinance.plus) ContextCompat.getColor(
            holder.itemView.context,
            R.color.bar_positive
        ) else ContextCompat.getColor(holder.itemView.context, R.color.bar_negative)
        //Symbol text
        holder.financeSymbol.text = symbol
        holder.financeSymbol.setTextColor(color)
        //Amount text
        holder.financeAmount.text = myFinance.amount.toString()
        holder.financeAmount.setTextColor(color)
        //Description text
        holder.financeDescription.text = myFinance.description
        //Date text
        val dateFormat = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
        holder.financeDate.text = dateFormat.format(myFinance.date.toDate())
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val financeSymbol: TextView = itemView.findViewById(R.id.amountSymbol)
        val financeAmount: TextView = itemView.findViewById(R.id.amount_text_view)
        val financeDescription: TextView = itemView.findViewById(R.id.description_text_view)
        val financeDate: TextView = itemView.findViewById(R.id.date_text_view)
    }
}