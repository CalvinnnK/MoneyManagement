package com.example.moneymanagementproject

import Add.Transaction.SaveData
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.util.*


class Transaction : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    //ambil kelas adapter
    private var adapter : TransactionAdapter? = null

    //Arraylist transaksi
    private var arrayListTransaction: ArrayList<SaveData> = ArrayList()
    lateinit var listView: ListView

    private var databaseReference: DatabaseReference = Firebase.database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TransactionAdapter(context, arrayListTransaction)


//        childEventListenerRecycler()
        addPostEventListener(databaseReference.child("transaksi"))
        checkDataIsChanged()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        binding.ListViewTransaction.adapter = adapter

        return binding.root
    }


    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates
                arrayListTransaction.clear()
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("listTransaction").children){
                    val post = snap.getValue<SaveData>()!!
                    addListTransaction(post)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addListenerForSingleValueEvent(postListener)
        // [END post_value_event_listener]
    }

    fun addListTransaction(data: SaveData){
        this.arrayListTransaction.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}