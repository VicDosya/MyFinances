package com.example.myfinance.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.myfinance.R
import com.example.myfinance.fragments.MyFinancesFragment
import com.example.myfinance.fragments.SummaryFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Hide the Action Bar if it is not null.
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }

        //Set MyFinancesFragment as default
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFinancesFragment())
                .commit()
        }

        //Set up button listeners
        val myFinanceBtn = findViewById<Button>(R.id.myFinances_btn)
        val summaryBtn = findViewById<Button>(R.id.summary_btn)

        myFinanceBtn.setOnClickListener {
            switchFragment(R.id.myFinances_btn)
        }

        summaryBtn.setOnClickListener {
            switchFragment(R.id.summary_btn)
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