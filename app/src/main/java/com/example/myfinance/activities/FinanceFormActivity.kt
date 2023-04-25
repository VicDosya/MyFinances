package com.example.myfinance.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class FinanceFormActivity : AppCompatActivity() {

    //Initialize the finances collection (fire store is initialized in MyFinanceApplication)
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_form)

        //Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Set the ActionBar title
        supportActionBar?.title = "Add Financial Amount"

        //"Add" button listener to add input data on fire store
        //Get all view from the layouts
        val addButton = findViewById<Button>(R.id.add_button)
        val plusMinusSwitch = findViewById<Switch>(R.id.plus_minus_switch)
        val amountInput = findViewById<EditText>(R.id.amount_input_field)
        val descriptionInput = findViewById<EditText>(R.id.description_input_field)
        val dateInput = findViewById<EditText>(R.id.date_input_field)
        //Disable keyboard in dateInput
        dateInput.keyListener = null

        //Date and Time picker dialog
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
            //Create a new TimePickerDialog to choose the time
            val currentTime = Calendar.getInstance() // Get the current time
            val hour = currentTime.get(Calendar.HOUR_OF_DAY)
            val minute = currentTime.get(Calendar.MINUTE)
            val timePickerDialog = TimePickerDialog(this, { _, hourOfDay, minute ->
                // Combine the selected date and time into a single Date object
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth, hourOfDay, minute)
                val selectedTimestamp = Timestamp(selectedDate.time)

                // Set the chosen date and time in the EditText
                dateInput.setText(selectedTimestamp.toDate().toString())
                dateInput.tag = selectedTimestamp
            }, hour, minute, true)

            // Show the TimePickerDialog
            timePickerDialog.show()
        }, year, month, day)

        // Show the DatePickerDialog when the dateInput field is clicked
        dateInput.setOnClickListener {
            datePickerDialog.show()
        }

        //Add button functionality
        addButton.setOnClickListener {
            // Input fields
            val isPlus = plusMinusSwitch.isChecked
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = descriptionInput.text.toString()
            val date = if (dateInput.tag != null) dateInput.tag as Timestamp else Timestamp.now()

            // Input fields layouts
            val inputLayouts = listOf(
                findViewById<TextInputLayout>(R.id.amount_input_layout),
                findViewById<TextInputLayout>(R.id.description_input_layout),
                findViewById<TextInputLayout>(R.id.date_input_layout)
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
                            findViewById(android.R.id.content),
                            "Error adding finance card: ${it.message}",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
            }
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
}