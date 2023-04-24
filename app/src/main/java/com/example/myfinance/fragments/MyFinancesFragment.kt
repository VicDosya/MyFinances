package com.example.myfinance.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.myfinance.R
import com.example.myfinance.activities.FinanceFormActivity
import com.example.myfinance.adapters.MyFinancesAdapter
import com.example.myfinance.data.MyFinanceModal
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DateFormatSymbols
import java.util.Calendar
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

        //Date Changer functionality
        //By default, get today's month and year and apply to the text views
        var currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        var currentYear = Calendar.getInstance().get(Calendar.YEAR)
        //Get all Date views
        val leftButton = view.findViewById<Button>(R.id.left_button)
        val rightButton = view.findViewById<Button>(R.id.right_button)
        val monthTextView = view.findViewById<TextView>(R.id.month_textview)
        val yearTextView = view.findViewById<TextView>(R.id.year_textview)
        monthTextView.text = DateFormatSymbols().months[currentMonth]
        yearTextView.text = currentYear.toString()
        //Get SwipeRefresh View
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)

        //Load data for current month and year
        loadFinanceData(currentMonth, currentYear)

        //Onclick Listener for left button
        leftButton.setOnClickListener {
            //Decrement currentMonth by 1
            currentMonth -= 1
            // If currentMonth is less than 0, set it to 11 (December) and decrement currentYear
            if (currentMonth < 0) {
                currentMonth = 11
                currentYear -= 1
            }
            //Update month and year TextViews with new values
            monthTextView.text = DateFormatSymbols().months[currentMonth]
            yearTextView.text = currentYear.toString()

            //Load data for updated month and year
            loadFinanceData(currentMonth, currentYear)
        }

        //Onclick Listener for right button
        rightButton.setOnClickListener {
            //Decrement currentMonth by 1
            currentMonth += 1
            // If currentMonth is less than 0, set it to 11 (December) and decrement currentYear
            if (currentMonth > 11) {
                currentMonth = 0
                currentYear += 1
            }
            //Update month and year TextViews with new values
            monthTextView.text = DateFormatSymbols().months[currentMonth]
            yearTextView.text = currentYear.toString()

            //Load data for updated month and year
            loadFinanceData(currentMonth, currentYear)
        }

        //Refresh functionality
        swipeRefreshLayout.setOnRefreshListener {
            loadFinanceData(currentMonth, currentYear)
            swipeRefreshLayout.isRefreshing = false
        }

        //Initialize RecyclerView
        myFinancesRecyclerView = view.findViewById(R.id.my_finances_recycler_view)
        myFinancesRecyclerView.layoutManager = LinearLayoutManager(activity)

        //Apply 'financeList' dummy data from MyFinanceModal.kt
        myFinancesAdapter = MyFinancesAdapter(com.example.myfinance.data.financeList)
        //Apply adapter to the recycler list
        myFinancesRecyclerView.adapter = myFinancesAdapter
        //Return the updated view variable
        return view
    }

    private fun updateList(financeList: ArrayList<MyFinanceModal>) {
        //Calculate total plus and minus amounts
        var totalPlusAmount: Double = 0.0
        var totalMinusAmount: Double = 0.0
        for (finance in financeList) {
            if (finance.plus) {
                totalPlusAmount += finance.amount
            } else {
                totalMinusAmount += finance.amount
            }
        }
        //Update the text views
        val plusAmountTextView = view?.findViewById<TextView>(R.id.plusAmount_textview)
        val minusAmountTextView = view?.findViewById<TextView>(R.id.minusAmount_textview)
        plusAmountTextView?.text = "+$totalPlusAmount"
        minusAmountTextView?.text = "-$totalMinusAmount"

        myFinancesAdapter = MyFinancesAdapter(financeList)
        myFinancesRecyclerView.adapter = myFinancesAdapter
    }

    fun loadFinanceData(month: Int, year: Int) {
        //Calendar Instances (start to end)
        val calendarStart = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendarEnd = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        //Fetch data from the Firestore
        financeCollectionRef.orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                Log.i("Firestore", "Fetched finance data.")
                val financeList = ArrayList<MyFinanceModal>()
                if (result != null) {
                    for (document in result) {
                        val finance = document.toObject(MyFinanceModal::class.java)
                        val financeDate = finance.date.toDate()
                        if (financeDate.after(calendarStart.time) && financeDate.before(calendarEnd.time)) {
                            financeList.add(finance)
                        }
                    }
                }
                updateList(financeList)

            }.addOnFailureListener { exception ->
                Log.d("Firestore", "Error getting documents: ", exception)
            }
    }
}