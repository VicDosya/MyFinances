package com.example.myfinance.activities

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.myfinance.R
import com.example.myfinance.fragments.MyFinancesFragment
import com.example.myfinance.fragments.SummaryFragment
import com.google.android.material.snackbar.Snackbar
import java.text.DateFormatSymbols
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Set today's date on the date picker
        setTodayDate()

        //LinearLayout of the month and year TextViews
        val monthYearLayout = findViewById<LinearLayout>(R.id.month_year_layout)

        //monthYearLayout click listener to open the month and year dialog
        monthYearLayout.setOnClickListener {
            showMonthYearPickerDialog()
        }

        //Set up button listeners
        val myFinanceBtn = findViewById<Button>(R.id.myFinances_btn)
        val summaryBtn = findViewById<Button>(R.id.summary_btn)

        //Hide the Action Bar if it is not null.
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Set MyFinancesFragment as default and its button disabled.
        if (savedInstanceState == null) {
            myFinanceBtn.isEnabled = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFinancesFragment())
                .commit()
        }

        myFinanceBtn.setOnClickListener {
            switchFragment(R.id.myFinances_btn)
            myFinanceBtn.isEnabled = false
            summaryBtn.isEnabled = true
        }

        summaryBtn.setOnClickListener {
            switchFragment(R.id.summary_btn)
            myFinanceBtn.isEnabled = true
            summaryBtn.isEnabled = false
        }

        //Check if there is a message extra from any Activity - display a Snack-bar
        if (intent.hasExtra("message")) {
            val message = intent.getStringExtra("message")
            val rootView = findViewById<View>(android.R.id.content)
            if (message != null) {
                Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    //Button listener to switch fragments
    private fun switchFragment(id: Int) {
        when (id) {
            //If myFinances button is pressed, switch to MyFinancesFragment.
            R.id.myFinances_btn -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFinancesFragment())
                .commit()
            //If Summary button is pressed, switch to SummaryFragment.
            R.id.summary_btn -> supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, SummaryFragment())
                .commit()
        }
    }

    private fun showMonthYearPickerDialog() {
        //Set the initial date to the current month and year
        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)

        //Create a custom view for the dialog containing month and year spinners
        val dialogView = layoutInflater.inflate(R.layout.month_year_picker_dialog, null)
        val monthSpinner = dialogView.findViewById<Spinner>(R.id.month_spinner)
        val yearSpinner = dialogView.findViewById<Spinner>(R.id.year_spinner)

        //Create adapters for the month and year spinners
        val monthAdapter = ArrayAdapter<String>(
            this,
            android.R.layout.simple_spinner_item,
            DateFormatSymbols().months
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        monthSpinner.adapter = monthAdapter

        val yearAdapter =
            ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, getYearsList())
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        yearSpinner.adapter = yearAdapter

        //Set the selected values of the spinners to the initial month and year
        monthSpinner.setSelection(initialMonth)
        yearSpinner.setSelection(getYearsList().indexOf(initialYear.toString()))

        //Create a new AlertDialog with the custom view
        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Select Month and Year")
            .setPositiveButton("OK") { _, _ ->
                //Get the selected values from the spinners
                val selectedMonth = monthSpinner.selectedItemPosition
                val selectedYear = yearSpinner.selectedItem.toString().toInt()

                //Update the month and year TextViews with the selected values
                val monthTextView = findViewById<TextView>(R.id.month_text_view)
                val yearTextView = findViewById<TextView>(R.id.year_text_view)
                monthTextView.text = DateFormatSymbols().months[selectedMonth]
                yearTextView.text = selectedYear.toString()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    //Returns a list of the years from 1900 to the current year
    private fun getYearsList(): List<String> {
        val years = mutableListOf<String>()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (year in 2000..currentYear) {
            years.add(year.toString())
        }
        return years
    }

    private fun setTodayDate() {
        val monthTextView = findViewById<TextView>(R.id.month_text_view)
        val yearTextView = findViewById<TextView>(R.id.year_text_view)
        //Set month and year layout to today's date

        //Get current month and year
        val calendar = Calendar.getInstance()
        val currentMonth =
            calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        val currentYear = calendar.get(Calendar.YEAR).toString()

        //Set month and year TextViews to current month and year
        monthTextView.text = currentMonth
        yearTextView.text = currentYear
    }

}