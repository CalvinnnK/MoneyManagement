package com.example.moneymanagementproject

import Add.Transaction.SaveData
import Home.*
import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    private var adapterT : HomeTransactionAdapter? = null
    //Array kosong buat manggil array dari Activity Main
    private var listWalletF: ArrayList<Wallet> = ArrayList<Wallet>()
    private var listTransaction: ArrayList<HomeTransaction> = ArrayList<HomeTransaction>()
    private var TotalBalance: Long = 0


    private var databaseReference: DatabaseReference = Firebase.database.reference
//    private var storageReference: DatabaseReference = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.homeTransactionSeeMore.setOnClickListener{
            val popupWindow = AddCategoryName()
            popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Category" )
        }

//        Adapter Wallet
        adapterW = WalletAdapter(context,listWalletF)
        binding.walletGrid.adapter = adapterW

        adapterW?.notifyDataSetChanged()
        binding.walletGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            if(i == listWalletF.size - 1){
                val popupWindow = AddWalletDialog()
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
            }
            else{
                Toast.makeText(
                    requireContext(), listWalletF[i].nameWallet + "selected" + i ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        addPostEventListenerWallet(databaseReference.child("wallet").child("listWallet"))



        //Adapter Transaction
        adapterT = HomeTransactionAdapter(context,listTransaction)
        binding.listViewTransactionHome.adapter = adapterT
        binding.listViewTransactionHome.isNestedScrollingEnabled = true

        addPostEventListenerTransaction(databaseReference.child("transaksi"))





        checkDataIsChanged()

        return binding.root
    }

    fun checkDataIsChanged(){
        adapterW?.notifyDataSetChanged()
        adapterT?.notifyDataSetChanged()
    }

    private fun addPostEventListenerWallet(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates and also reset balance too
                listWalletF.clear()
                TotalBalance = 0

                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.children){
                    val post = snap.getValue<Wallet>()!!
                    addWallet(post)
                    TotalBalance += snap.child("saldo").value.toString().toLong()
                    binding.totalAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(TotalBalance)
                }
                listWalletF.add(Wallet("Add Wallet",0))

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
    }

    private fun addPostEventListenerTransaction(postReference: DatabaseReference) {
        var amount: Long = 0
        var date: Long = 0
        var wallet = ""
        var cate = ""
        var notes = ""
        var imgWallet = ""
        var imgCate = ""

        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates and also reset balance too
                listTransaction.clear()

                // Income
                for(snap : DataSnapshot in dataSnapshot.child("listIncome").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = "Income"
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCate").value.toString()

                    addTransaction(HomeTransaction("income", amount, date, wallet, cate, notes, imgWallet, imgCate))
                    Log.d("Home check", "" + listTransaction.size)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
    }


    fun addWallet(data: Wallet){
        this.listWalletF.add(data)
        checkDataIsChanged()
    }

    fun addTransaction(data: HomeTransaction){
        this.listTransaction.add(data)
        checkDataIsChanged()
    }

}
