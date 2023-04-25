package com.example.myfinance.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myfinance.R
import com.example.myfinance.databinding.ActivityMainBinding
import com.example.myfinance.fragments.MyFinancesFragment
import com.example.myfinance.fragments.SummaryFragment
import com.google.android.material.snackbar.Snackbar
import java.lang.IllegalArgumentException

class MainActivity : AppCompatActivity() {

    //binding variable to replace the 'findByViewId' code
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Hide the Action Bar (If it is not null)
        supportActionBar?.hide()

        //Set MyFinancesFragment as default Fragment view and its button disabled.
        if (savedInstanceState == null) {
            binding.myFinancesBtn.isEnabled = false
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, MyFinancesFragment())
                .commit()
        }

        //Clicking MyFinance Button will switch to MyFinanceFragment (Disable button accordingly)
        binding.myFinancesBtn.setOnClickListener {
            switchFragment(R.id.myFinances_btn)
            binding.myFinancesBtn.isEnabled = false
            binding.summaryBtn.isEnabled = true
        }

        //Clicking Summary Button will switch to SummaryFragment (Disable button accordingly)
        binding.summaryBtn.setOnClickListener {
            switchFragment(R.id.summary_btn)
            binding.myFinancesBtn.isEnabled = true
            binding.summaryBtn.isEnabled = false
        }

        //Check if there is a message extra from any Activity - and display it as a Snack-bar
        intent.getStringExtra("message")?.let { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    //Function that switches between fragments using the fragment_container in the MainActivity
    private fun switchFragment(id: Int) {
        val fragment = when (id) {
            R.id.myFinances_btn -> MyFinancesFragment()
            R.id.summary_btn -> SummaryFragment()
            else -> throw IllegalArgumentException("Invalid fragment ID")
        }
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}