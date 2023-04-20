package com.example.myfinance.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfinance.R
import com.example.myfinance.adapters.MyFinancesAdapter
import com.example.myfinance.data.financeList

class MyFinancesFragment : Fragment() {

    private lateinit var myFinancesRecyclerView: RecyclerView
    private lateinit var myFinancesAdapter: MyFinancesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_my_finances, container, false)

        //Initialize RecyclerView
        myFinancesRecyclerView = view.findViewById(R.id.my_finances_recycler_view)
        myFinancesRecyclerView.layoutManager = LinearLayoutManager(activity)
        //Apply 'financeList' dummy data from MyFinanceModal.kt
        myFinancesAdapter = MyFinancesAdapter(financeList)
        //Apply adapter to the recycler list
        myFinancesRecyclerView.adapter = myFinancesAdapter
        //Return the updated view variable
        return view
    }
}