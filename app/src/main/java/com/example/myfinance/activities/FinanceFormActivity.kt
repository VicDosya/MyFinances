package com.example.myfinance.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.example.myfinance.R

class FinanceFormActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_form)

        //Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Set the ActionBar title
        supportActionBar?.title = "Add Financial Amount"
    }

    //Handling the back button in the ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Handle Up button click
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}