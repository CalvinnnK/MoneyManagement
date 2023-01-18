package com.example.moneymanagementproject

import Home.*
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    private var adapterT : HomeTransactionAdapter? = null
    //Array kosong buat manggil array dari Activity Main
    private var listWalletF: ArrayList<Wallet> = ArrayList<Wallet>()
    private var listTransaction: ArrayList<TransactionDialog> = ArrayList<TransactionDialog>()
    private var TotalBalance: Long = 0


    private var databaseReference: DatabaseReference = Firebase.database.reference

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
//                val popupWindow = EditWalletDialog()
//                popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
            }
        }




        binding.listViewTransactionHome.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            popUpEditDialog(i)
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
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog("income", amount, date, wallet, cate, notes, imgWallet, imgCate))

                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransaction").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = snap.child("cate").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog("expense", amount, date, wallet, cate, notes, imgWallet, imgCate))

                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransfer").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("walletFrom").value.toString()
                    cate = snap.child("walletTo").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWalletFrom").value.toString()
                    imgCate = snap.child("imgLinkWalletTo").value.toString()

                    addTransaction(TransactionDialog("transfer", amount, date, wallet, cate, notes, imgWallet, imgCate))

                    sortArray()
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

    fun addTransaction(data: TransactionDialog){
        this.listTransaction.add(data)
        checkDataIsChanged()
    }

    fun sortArray(){
        this.listTransaction.sortByDescending { it.date }
        while(this.listTransaction.size > 5){
            this.listTransaction.removeAt(listTransaction.size-1)
        }
    }

    private fun popUpEditDialog(i : Int) {
        val popupWindow = ViewTransactionDialog(this.listTransaction[i])
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
    }

}
