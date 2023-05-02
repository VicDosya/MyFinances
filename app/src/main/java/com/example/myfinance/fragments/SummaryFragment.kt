package com.example.myfinance.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.example.myfinance.databinding.FragmentSummaryBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SummaryFragment : Fragment() {
    //Typically fixes the IllegalStateException error
    private lateinit var context: Context
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    //Initialize viewBinding
    private lateinit var binding: FragmentSummaryBinding

    //Initialize Fire store connectivity
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")

    //Get today's year
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Bind the view using view binding
        binding = FragmentSummaryBinding.inflate(inflater, container, false)
        // Get the root view from the binding
        val view = binding.root

        //Setup Left and right buttons with its year functionality
        setupViews()

        //Update the chart
        updateChart()

        //Update the year text view
        updateButtonYear()

        return view
    }

    //Setup views when pressing left or right buttons
    private fun setupViews() {
        with(binding) {
            leftButton.setOnClickListener {
                selectedYear--
                updateChart()
                updateButtonYear()
            }
            rightButton.setOnClickListener {
                selectedYear++
                updateChart()
                updateButtonYear()
            }
        }
    }

    //Update the chart according to the data from fire base
    private fun updateChart() {
        //Show progress bar
        binding.progressBar.visibility = View.VISIBLE
        financeCollectionRef.get().addOnSuccessListener { querySnapshot ->
            val monthlyData = DoubleArray(12)
            var dataFound = false

            for (document in querySnapshot.documents) {
                val financeData = document.toObject(MyFinanceModal::class.java)
                financeData?.let {
                    val calendar = Calendar.getInstance()
                    calendar.time = it.date.toDate()
                    val year = calendar.get(Calendar.YEAR)
                    val month = calendar.get(Calendar.MONTH)
                    val amount = it.amount

                    // Calculate the sum based on the selected year
                    if (year == selectedYear) {
                        dataFound = true
                        monthlyData[month] += if (it.plus) amount else -amount
                    }
                }
            }

            //If data is empty on the selected year, hide bar chart and send snack-bar message
            if (!dataFound) {
                Snackbar.make(
                    requireView(),
                    "No data found for the selected year",
                    Snackbar.LENGTH_LONG
                ).show()
                binding.financeBarChart.visibility = View.INVISIBLE
                return@addOnSuccessListener
            }

            binding.financeBarChart.visibility = View.VISIBLE

            val entries = monthlyData.mapIndexed { index, amount ->
                BarEntry(index + 1f, amount.toFloat())
            }

            //Setup Bar Chart styling
            binding.financeBarChart.description.isEnabled = false
            val xAxis = binding.financeBarChart.xAxis
            xAxis.valueFormatter = MonthValueFormatter()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            xAxis.granularity = 1f
            // Set the number of labels equal to the number of entries
            xAxis.labelCount = entries.size
            // Set x-axis label text size
            xAxis.textSize = 12f
            // Rotate x-axis labels for better visibility
            xAxis.labelRotationAngle = -45f

            //Setup color for each bar (Above x-Axis or below)
            val dataSet = BarDataSet(entries, "Financial position each month")
            dataSet.valueTextSize = 10f // Set text size of values displayed on top of bars
            dataSet.colors = entries.map { entry ->
                ContextCompat.getColor(
                    requireContext(),
                    if (entry.y >= 0) R.color.bar_positive else R.color.bar_negative
                )
            }
            dataSet.setDrawIcons(false)

            val barData = BarData(dataSet)
            // Adjust the width of the bars
            barData.barWidth = 0.75f
            //Apply the data and update
            binding.financeBarChart.data = barData
            binding.financeBarChart.invalidate()
            //Hide progress bar
            binding.progressBar.visibility = View.INVISIBLE
        }
    }

    //Update the year text view function
    private fun updateButtonYear() {
        val yearText = "$selectedYear"
        binding.yearTextview.text = yearText
    }

    // Custom X-axis value formatter to display month labels
    class MonthValueFormatter : ValueFormatter() {
        private val monthLabels = arrayOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        override fun getFormattedValue(value: Float): String {
            return value.toInt().let { index ->
                if (index in monthLabels.indices) {
                    monthLabels[index]
                } else {
                    ""
                }
            }
        }
    }
}