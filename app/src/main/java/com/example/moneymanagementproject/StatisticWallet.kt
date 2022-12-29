package com.example.moneymanagementproject

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moneymanagementproject.databinding.FragmentStatisticWalletBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class StatisticWallet : Fragment() {
    private var _binding: FragmentStatisticWalletBinding? = null
    private val binding get() = _binding!!

    private var listStatWallet = ArrayList<StatWallet>()

    private val databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticWalletBinding.inflate(inflater, container, false)

        var adapter = StatWalletAdapter(context,listStatWallet)
        binding.statWalletListView.adapter = adapter

        addPostEventListener(databaseReference)
        adapter.notifyDataSetChanged()

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        var name: String = ""
        var income: Long = 0
        var expense: Long = 0

        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                // clean the array to avoid duplicates
                listStatWallet.clear()

                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    name = ""
                    income = 0
                    expense = 0
                    Log.d("AAA loop wallet","" + snap.child("nameWallet").value.toString())
                    name =  snap.child("nameWallet").value.toString()

                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransaction").children){
                        if(snap1.child("wallet").value.toString() == snap.child("nameWallet").value.toString()){
                            expense += snap1.child("amount").value.toString().toLong()
                            Log.d("AAA loop transaksi","" + snap1.child("notes").value.toString())
                        }
                    }
                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listIncome").children){
                        if(snap1.child("wallet").value.toString() == snap.child("nameWallet").value.toString()){
                            income += snap1.child("amount").value.toString().toLong()
                            Log.d("AAA loop income","" + snap1.child("notes").value.toString())
                        }
                    }
                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransfer").children){
                        if(snap1.child("walletFrom").value.toString() == snap.child("nameWallet").value.toString()){
                            expense += snap1.child("amount").value.toString().toLong()
                            Log.d("AAA loop transfer","" + snap1.child("notes").value.toString())
                        }
                        else if(snap1.child("walletTo").value.toString() == snap.child("nameWallet").value.toString()){
                            income += snap1.child("amount").value.toString().toLong()
                            Log.d("AAA loop transfer","" + snap1.child("notes").value.toString())
                        }
                    }
                    listStatWallet.add(StatWallet(name,income-expense,income, expense))
                    Log.d("AAA onDataChange", "" + name + " " + income + " " + expense + listStatWallet.count())
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addListenerForSingleValueEvent(postListener)
        // [END post_value_event_listener]
    }


}