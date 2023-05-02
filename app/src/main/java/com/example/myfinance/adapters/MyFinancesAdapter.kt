package com.example.myfinance.adapters

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.example.myfinance.data.financeList
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Locale

class MyFinancesAdapter(
    private val context: Context,
    private val myFinancesList: List<MyFinanceModal>
) :
    RecyclerView.Adapter<MyFinancesAdapter.MyViewHolder>() {

    private val financeCollectionName = "finances"
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection(financeCollectionName)

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

        //Set OnClickListener for the finance card deletion
        holder.itemView.setOnClickListener {
            deleteFinanceCard(position)
        }
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val financeSymbol: TextView = itemView.findViewById(R.id.amountSymbol)
        val financeAmount: TextView = itemView.findViewById(R.id.amount_text_view)
        val financeDescription: TextView = itemView.findViewById(R.id.description_text_view)
        val financeDate: TextView = itemView.findViewById(R.id.date_text_view)
    }

    private fun deleteFinanceCard(position: Int) {
        val finance = myFinancesList.getOrNull(position)
        if (finance != null) {
            val amount = finance.amount
            val description = finance.description
            val date = finance.date
            val plus = finance.plus

            val alertDialogBuilder = AlertDialog.Builder(context)
            alertDialogBuilder.setTitle("Delete Finance Card")
            alertDialogBuilder.setMessage("Are you sure you want to delete this  finance card?")
            alertDialogBuilder.setPositiveButton("Delete") { dialog, _ ->
                //Remove from Firebase fire store
                financeCollectionRef.whereEqualTo("amount", amount)
                    .whereEqualTo("description", description)
                    .whereEqualTo("date", date)
                    .whereEqualTo("plus", plus)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        querySnapshot.documents.forEach { document ->
                            document.reference.delete()
                        }
                        Toast.makeText(context, "Success! Please refresh.", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Fire Store", "Error deleting document: ", exception)
                    }
                dialog.dismiss()
            }
            alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            alertDialogBuilder.create().show()
        } else {
            Log.d("MyFinancesAdapter", "Invalid position: $position")
        }
    }
}