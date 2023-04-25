package com.example.myfinance.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.example.myfinance.databinding.ActivityFinanceFormBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class FinanceFormActivity : AppCompatActivity() {

    //Initialize viewBinding
    private lateinit var binding: ActivityFinanceFormBinding

    //Initialize the finances collection (fire store is initialized in MyFinanceApplication)
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFinanceFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Setup the ActionBar with back button and a title
        setupActionBar()

        //Disable keyboard when pressing on the date input field
        binding.dateInputField.keyListener = null

        // Show the both date and time picker dialogs when pressing date input field
        binding.dateInputField.setOnClickListener {
            showDateTimePicker()
        }

        //Add button functionality
        binding.addButton.setOnClickListener {
            addFinanceData()
        }
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

    //Add a new Finance card to the database function
    private fun addFinanceData() {
        // Input fields
        val isPlus = binding.plusMinusSwitch.isChecked
        val amount = binding.amountInputField.text.toString().toDoubleOrNull() ?: 0.0
        val description = binding.descriptionInputField.text.toString()
        val date = binding.dateInputField.tag as? Timestamp ?: Timestamp.now()

        // Input fields layouts
        val inputLayouts = listOf(
            binding.amountInputLayout,
            binding.descriptionInputLayout,
            binding.dateInputLayout,
        )

        // Check for empty fields and show error messages
        inputLayouts.forEach { layout ->
            layout.isErrorEnabled = layout.editText?.text.isNullOrBlank()
            if (layout.isErrorEnabled) {
                layout.error = when (layout.id) {
                    R.id.amount_input_layout -> "Fill in the amount"
                    R.id.description_input_layout -> "Fill in the description"
                    R.id.date_input_layout -> "Choose a date"
                    else -> ""
                }
            }
        }

        // If all fields are filled, add data to fire store
        if (!inputLayouts.any { it.isErrorEnabled }) {
            financeCollectionRef.add(MyFinanceModal(isPlus, amount, description, date))
                .addOnSuccessListener {
                    //Success message in Logcat
                    Log.i("Fire Store", "Finance card data has been added to fire store.")
                    // Navigate back home with a success message extra (to main activity)
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("message", "Finance card added successfully!")
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    //Show error message in Snack-bar and in Logcat
                    Log.d("Fire Store", "Error Adding Finance card: ", it)
                    Snackbar.make(
                        binding.root,
                        "Error adding finance card: ${it.message}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
        }
    }

    //Show date and time picker dialogs
    private fun showDateTimePicker() {
        val currentTime = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val timePickerDialog = TimePickerDialog(
                    this,
                    { _, hourOfDay, minute ->
                        val selectedDate = Calendar.getInstance().apply {
                            set(year, month, dayOfMonth, hourOfDay, minute)
                        }
                        val selectedTimestamp = Timestamp(selectedDate.time)

                        binding.dateInputField.setText(selectedTimestamp.toDate().toString())
                        binding.dateInputField.tag = selectedTimestamp
                    },
                    currentTime.get(Calendar.HOUR_OF_DAY),
                    currentTime.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            },
            currentTime.get(Calendar.YEAR),
            currentTime.get(Calendar.MONTH),
            currentTime.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    //Setup the actionBar function
    private fun setupActionBar() {
        //Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Set the ActionBar title
        supportActionBar?.title = "Add Financial Amount"
    }
}