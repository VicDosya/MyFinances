package com.example.myfinance.activities

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import com.example.myfinance.R
import com.example.myfinance.data.MyFinanceModal
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class FinanceFormActivity : AppCompatActivity() {

    //Initialize the finances collection (Firestore is initialized in MyFinanceApplication)
    private val db = FirebaseFirestore.getInstance()
    private val financeCollectionRef = db.collection("finances")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finance_form)

        //Enable the back button in the ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //Set the ActionBar title
        supportActionBar?.title = "Add Financial Amount"

        //"Add" button listener to add input data on Firestore
        //Get all view from the layouts
        val addButton = findViewById<Button>(R.id.add_button)
        val plusMinusSwitch = findViewById<Switch>(R.id.plus_minus_switch)
        val amountInput = findViewById<EditText>(R.id.amount_input_field)
        val descriptionInput = findViewById<EditText>(R.id.description_input_field)
        val dateInput = findViewById<EditText>(R.id.date_input_field)
        //Disable keyboard in dateInput
        dateInput.keyListener = null

        //Set the default date to today's date
        val currentDate = Calendar.getInstance()
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH)
        val day = currentDate.get(Calendar.DAY_OF_MONTH)

        dateInput.setText("$day/${month + 1}/$year")

        dateInput.setOnClickListener{
            // Create a new DatePickerDialog with the current date as the default date
            val datePickerDialog = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                // Set the chosen date in the EditText
                dateInput.setText("$dayOfMonth/${month + 1}/$year")
            }, year, month, day)

            // Show the DatePickerDialog
            datePickerDialog.show()
        }
        addButton.setOnClickListener{
            val isPlus = plusMinusSwitch.isChecked
            val amount = amountInput.text.toString().toDouble()
            val description = descriptionInput.text.toString()
            val date = dateInput.text.toString()
            val dataToFirestore = MyFinanceModal(isPlus, amount, description, date)
            financeCollectionRef.add(dataToFirestore)
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