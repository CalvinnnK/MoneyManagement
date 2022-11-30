package com.example.moneymanagementproject

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*

class AddTransaction : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    private lateinit var  exit: Button
    private lateinit var amount: EditText
    private lateinit var date: Button
    private lateinit var datetxt: TextView
    private lateinit var notes: EditText
    private lateinit var wallet: EditText
    private lateinit var category: EditText
    private lateinit var savebtn: Button

    private lateinit var viewModel : TransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        exit = findViewById(R.id.exitTransc)
        amount = findViewById(R.id.inputAmount)
        date = findViewById(R.id.dateBtn)
        datetxt = findViewById(R.id.inputDate)
        wallet = findViewById(R.id.inputwallet)
        category = findViewById(R.id.inputCate)
        notes = findViewById(R.id.inputNotes)
        savebtn = findViewById(R.id.btnAddTransc)


//        Default date dibuat hari ini
        date.text = "Today"

        date.setOnClickListener{
            showDateDialog()
        }

        savebtn.setOnClickListener{
//            saveTransaction()
        }

        exit.setOnClickListener{
            backToMain()
        }

    }

    private fun saveTransaction() {
        val a1 = amount.text.toString().trim()
        val a2 = datetxt.text.toString().trim()
        val a3 = wallet.text.toString().trim()
        val a4 = category.text.toString().trim()
        val a5 = notes.text.toString().trim()
        val dataID = UUID.randomUUID().toString()

        Log.d(TAG, "saveTransaction:  " + dataID)


        if(a1.isEmpty() || a3.isEmpty() ){
            amount.error = "Please input Amount"
            wallet.error = "Please input wallet"
        }
        else{
            val dataRef = FirebaseDatabase.getInstance("https://money-management-app-9810f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

            val saving = SaveData(dataID, a1, a2, a3, a4, a5)

            if (dataID != null) {
                dataRef.child(dataID).setValue(saving).addOnCompleteListener{
                    Toast.makeText(applicationContext,"Transaction Saved", Toast.LENGTH_LONG).show()
                    backToMain()
                }
            }
        }
    }

    fun backToMain(){
        finish()
    }

    fun basicReadWrite() {

        // [START write_message]
        // Write a message to the database
        val myRef = FirebaseDatabase.getInstance().getReference("https://money-management-app-9810f-default-rtdb.asia-southeast1.firebasedatabase.app")

        myRef.setValue("Hello, World!")
        // [END write_message]

        // [START read_message]
        // Read from the database
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val value = dataSnapshot.getValue<String>()
                Log.d(ContentValues.TAG, "Value is: $value")
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(ContentValues.TAG, "Failed to read value.", error.toException())
            }
        })
        // [END read_message]
    }

    fun showDateDialog(){
        val datePicker: DatePickerDialog = DatePickerDialog(this,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        datePicker.show()
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val selectedDate: String = "$p3/$p2/$p1"
        datetxt.text = selectedDate
    }


}