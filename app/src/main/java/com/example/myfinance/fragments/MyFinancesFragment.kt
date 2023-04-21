package com.example.myfinance.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinance.R
import com.example.myfinance.activities.FinanceFormActivity
import com.example.myfinance.adapters.MyFinancesAdapter
import com.example.myfinance.data.MyFinanceModal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MyFinancesFragment : Fragment() {

    private lateinit var myFinancesRecyclerView: RecyclerView
    private lateinit var myFinancesAdapter: MyFinancesAdapter
    private lateinit var floatingAddButton: FloatingActionButton

    //Initialize the finances collection (Firestore is initialized in MyFinanceApplication)
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_finances, container, false)

        //Pressing the floating button will navigate to the FinanceFormActivity
        floatingAddButton = view.findViewById(R.id.floating_add_button)
        floatingAddButton.setOnClickListener {
            val intent = Intent(activity, FinanceFormActivity::class.java)
            startActivity(intent)
        }

        //Initialize RecyclerView
        myFinancesRecyclerView = view.findViewById(R.id.my_finances_recycler_view)
        myFinancesRecyclerView.layoutManager = LinearLayoutManager(activity)

        //Get finance data from firestore and apply it to financeList
        financeCollectionRef.orderBy(
            "date",
            Query.Direction.DESCENDING
        ) //order by date in descending order
            .get().addOnSuccessListener { result ->
                Log.i("Firestore", "Fetched finance data.")
                val financeList = ArrayList<MyFinanceModal>()
                if (result != null) {
                    for (document in result) {
                        val finance = document.toObject(MyFinanceModal::class.java)
                        financeList.add(finance)
                    }
                    financeList.sortByDescending { finance -> SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(finance.date) }
                }
                updateList(financeList)

            }.addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }

        //Apply 'financeList' dummy data from MyFinanceModal.kt
        myFinancesAdapter = MyFinancesAdapter(com.example.myfinance.data.financeList)
        //Apply adapter to the recycler list
        myFinancesRecyclerView.adapter = myFinancesAdapter
        //Return the updated view variable
        return view
    }

    private fun updateList(financeList: ArrayList<MyFinanceModal>) {
        myFinancesAdapter = MyFinancesAdapter(financeList)
        myFinancesRecyclerView.adapter = myFinancesAdapter
    }
}