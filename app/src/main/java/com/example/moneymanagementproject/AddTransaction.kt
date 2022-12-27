package com.example.moneymanagementproject

import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.example.moneymanagementproject.databinding.FragmentAddTransactionDialogBinding
import com.google.android.material.tabs.TabLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTransaction : AppCompatActivity() {

    private lateinit var exit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Ambil ID dari resource file
        exit = findViewById(R.id.exit_transaction)

//        Adapter buat tab page view
        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = ViewPagerAdapter(supportFragmentManager)
        val tabLayout = findViewById<TabLayout>(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)


        exit.setOnClickListener{
            finish()
        }
    }

}