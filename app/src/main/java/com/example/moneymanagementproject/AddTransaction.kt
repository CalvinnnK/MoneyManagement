package com.example.moneymanagementproject

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import java.util.*

class AddTransaction : AppCompatActivity() {

    private lateinit var  exit: Button
    private lateinit var amount: EditText
    private lateinit var date: EditText
    private lateinit var notes: EditText
    private lateinit var savebtn: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        exit = findViewById(R.id.exitTransc)
        amount = findViewById(R.id.inputAmount)
        date = findViewById(R.id.inputDate)
        notes = findViewById(R.id.inputNotes)
        savebtn = findViewById(R.id.btnAddTransc)

        savebtn.setOnClickListener{
            saveTransaction()
        }

        exit.setOnClickListener{
            backToMain()
        }

    }

    private fun saveTransaction() {
        val a1 = amount.text.toString().trim()
        val a2 = date.text.toString().trim()
        val a3 = notes.text.toString().trim()
        Log.d(TAG, "ngentoa  " )
        val dataID = UUID.randomUUID().toString()
        Log.d(TAG, "saveTransaction:  " + dataID)


        if(a1.isEmpty() || a2.isEmpty() ){
            amount.error = "Please input Amount"
            date.error = "Please input date"
        }
        else{
            val dataRef = FirebaseDatabase.getInstance("https://money-management-app-9810f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

            val saving = SaveData(dataID, a1, a2, a3)

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


}