package com.example.myfinance.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class SummaryFragment : Fragment() {

    private lateinit var barChart: BarChart
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")
    private lateinit var leftButton: Button
    private lateinit var rightButton: Button
    private lateinit var yearTextView: TextView

    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_summary, container, false)
        barChart = view.findViewById(R.id.finance_bar_chart)
        leftButton = view.findViewById(R.id.left_button)
        rightButton = view.findViewById(R.id.right_button)
        yearTextView = view.findViewById(R.id.year_textview)

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

        updateChart()
        updateButtonYear()

        return view
    }

    private fun updateChart() {
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
                        if (it.plus) {
                            monthlyData[month] += amount
                        } else {
                            monthlyData[month] -= amount
                        }
                    }
                }
            }

            if(!dataFound) {
                Snackbar.make(requireView(), "No data found for the selected year", Snackbar.LENGTH_LONG).show()
                barChart.visibility = View.INVISIBLE
                return@addOnSuccessListener
            }

            barChart.visibility = View.VISIBLE

            val entries = mutableListOf<BarEntry>()

            for (i in monthlyData.indices) {
                val month = (i + 1).toFloat()
                val amount = monthlyData[i].toFloat()
                entries.add(BarEntry(month, amount))
            }

            barChart.description.isEnabled = false
            val xAxis = barChart.xAxis
            xAxis.valueFormatter = MonthValueFormatter()
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(true)
            xAxis.granularity = 1f
            xAxis.labelCount = entries.size // Set the number of labels equal to the number of entries
            xAxis.textSize = 12f // Set x-axis label text size
            xAxis.labelRotationAngle = -45f // Rotate x-axis labels for better visibility


            val dataSet = BarDataSet(entries, "Financial position each month")
            dataSet.valueTextSize = 10f // Set text size of values displayed on top of bars

            dataSet.colors = entries.map { entry ->
                if(entry.y >= 0) {
                    ContextCompat.getColor(requireContext(), R.color.bar_positive)
                } else {
                    // Color for positive values

                    ContextCompat.getColor(requireContext(), R.color.bar_negative) // Color for negative values
                }
            }
            dataSet.setDrawIcons(false)

            val barData = BarData(dataSet)
            barData.barWidth = 0.75f // Adjust the width of the bars
            barChart.data = barData
            barChart.invalidate()
        }
    }

    private fun updateButtonYear() {
        val yearText = "$selectedYear"
        yearTextView.text = yearText
    }

    // Custom X-axis value formatter to display month labels
    class MonthValueFormatter : ValueFormatter() {
        private val monthLabels = arrayOf(
            "", "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )

        override fun getFormattedValue(value: Float): String {
            val index = value.toInt()
            return if (index >= 0 && index < monthLabels.size) {
                monthLabels[index]
            } else {
                ""
            }
        }
    }
}