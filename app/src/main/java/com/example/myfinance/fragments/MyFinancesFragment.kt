package com.example.myfinance.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfinance.R
import com.example.myfinance.activities.FinanceFormActivity
import com.example.myfinance.adapters.MyFinancesAdapter
import com.example.myfinance.data.MyFinanceModal
import com.example.myfinance.databinding.FragmentMyFinancesBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.DateFormatSymbols
import java.util.Calendar
import kotlin.collections.ArrayList

class MyFinancesFragment : Fragment() {

    private lateinit var binding: FragmentMyFinancesBinding
    private lateinit var myFinancesAdapter: MyFinancesAdapter
    private var currentMonth = Calendar.getInstance().get(Calendar.MONTH)
    private var currentYear = Calendar.getInstance().get(Calendar.YEAR)

    //Initialize the finances collection (fire store is initialized in MyFinanceApplication)
    private val financeCollectionName = "finances"
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection(financeCollectionName)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        //Inflate the layout for this fragment using viewBinding
        binding = FragmentMyFinancesBinding.inflate(inflater, container, false)

        //Pressing the floating button will navigate to the FinanceFormActivity
        binding.floatingAddButton.setOnClickListener {
            val intent = Intent(requireContext(), FinanceFormActivity::class.java)
            startActivity(intent)
        }

        //Date Changer functionality
        //By default, apply to the text views of today's date using the setDate function
        setDate(currentMonth, currentYear)

        //Load data for current month and year
        loadFinanceData(currentMonth, currentYear)

        //Onclick Listener for left button
        binding.leftButton.setOnClickListener {
            updateMonthAndYear(-1)
        }

        //Onclick Listener for right button
        binding.rightButton.setOnClickListener {
            updateMonthAndYear(1)
        }

        //Refresh functionality
        binding.swipeRefreshLayout.setOnRefreshListener {
            loadFinanceData(currentMonth, currentYear)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        //Initialize RecyclerView
        binding.myFinancesRecyclerView.layoutManager = LinearLayoutManager(activity)

        //Apply 'financeList' dummy data from MyFinanceModal.kt
        myFinancesAdapter = MyFinancesAdapter(com.example.myfinance.data.financeList)
        //Apply adapter to the recycler list
        binding.myFinancesRecyclerView.adapter = myFinancesAdapter
        //Return the updated view variable
        return binding.root
    }

    private fun updateList(financeList: ArrayList<MyFinanceModal>) {
        //Calculate total plus and minus amounts
        val (totalPlusAmount, totalMinusAmount) = financeList.partition { it.plus }
            .let { (plusList, minusList) ->
                plusList.sumOf { it.amount } to minusList.sumOf { it.amount }
            }
        //Update the text views
        binding.plusAmountTextview.text = getString(R.string.total_plus_amount, totalPlusAmount)
        binding.minusAmountTextview.text = getString(R.string.total_minus_amount, totalMinusAmount)
        //Update the adapter and the recycler view
        myFinancesAdapter = MyFinancesAdapter(financeList)
        binding.myFinancesRecyclerView.adapter = myFinancesAdapter
    }

    //Load finance data according to the date (month and year)
    private fun loadFinanceData(month: Int, year: Int) {
        //Calendar Instances (start to end)
        val calendarStart = Calendar.getInstance().apply {
            set(year, month, 1, 0, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val calendarEnd = Calendar.getInstance().apply {
            set(year, month, calendarStart.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59)
            set(Calendar.MILLISECOND, 999)
        }

        //Fetch data from the fire store
        financeCollectionRef.orderBy("date", Query.Direction.DESCENDING)
            .get().addOnSuccessListener { result ->
                Log.i("Fire Store", "Fetched finance data.")
                val financeList = ArrayList<MyFinanceModal>()
                result?.forEach { document ->
                    val finance = document.toObject(MyFinanceModal::class.java)
                    val financeDate = finance.date.toDate()
                    if (financeDate.after(calendarStart.time) && financeDate.before(calendarEnd.time)) {
                        financeList.add(finance)
                    }
                }
                updateList(financeList)

            }
            .addOnFailureListener { exception ->
                Log.d("Fire Store", "Error getting documents: ", exception)
                Toast.makeText(activity, "Failed to load finance data.", Toast.LENGTH_SHORT).show()
            }
    }

    //Function to handle the left and right button to change months and years
    private fun updateMonthAndYear(direction: Int) {
        currentMonth += direction
        currentYear += currentMonth / 12
        currentMonth %= 12
        if (currentMonth < 0) {
            currentMonth += 12
            currentYear -= 1
        }
        setDate(currentMonth, currentYear)
        loadFinanceData(currentMonth, currentYear)
    }

    //Function to set the date to the month and year TextViews
    private fun setDate(month: Int, year: Int) {
        binding.monthTextview.text = DateFormatSymbols().months[month]
        binding.yearTextview.text = year.toString()
    }
}