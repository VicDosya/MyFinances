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

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}