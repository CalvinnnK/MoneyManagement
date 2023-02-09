package com.example.moneymanagementproject.Home

import com.example.moneymanagementproject.Transaction.TransactionData
import com.example.moneymanagementproject.Transaction.TransactionDialogAdapter
import com.example.moneymanagementproject.Transaction.ViewTransactionDialog
import com.example.moneymanagementproject.Category.Category
import com.example.moneymanagementproject.Transaction.Transaction
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    private var adapterT : TransactionDialogAdapter? = null

    private var TotalBalance: Long = 0


    private var databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        getIcon()

        binding.homeTransactionSeeMore.setOnClickListener{
            val targetFragment = Transaction()
            val fragmentManager = parentFragmentManager
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.replace(R.id.fragmentContainer, targetFragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commit()
        }



//        Adapter Wallet
        adapterW = WalletAdapter(context,listWallet)
        binding.walletGrid.adapter = adapterW

        adapterW?.notifyDataSetChanged()
        binding.walletGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            if(i == listWallet.size - 1){
                val popupWindow = AddWalletDialog()
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
            }
            else{
                popUpEditWallet(i)
            }
        }


        binding.listViewTransactionHome.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            popUpEditDialog(i)
        }

        EventListenerWallet(databaseReference)
        EventListenerTransaction(databaseReference.child("transaksi"))

        //Adapter com.example.moneymanagementproject.Transaction
        adapterT = TransactionDialogAdapter(context,listTransaction,5)
        binding.listViewTransactionHome.adapter = adapterT
        binding.listViewTransactionHome.isNestedScrollingEnabled = true
        checkDataIsChanged()

        return binding.root
    }


    companion object{
        var listWallet: ArrayList<Wallet> = ArrayList<Wallet>()
        var listCategory: ArrayList<Category> = ArrayList<Category>()
        var listTransaction: ArrayList<TransactionData> = ArrayList<TransactionData>()
        var listIconWallet: ArrayList<String> = ArrayList()
        var listIconCategory: ArrayList<String> = ArrayList()

        fun syncTransactionDatabase(data: TransactionData){
            var type = ""
            if(data.type == "income") type = "listIncome"
            else if (data.type == "expense") type = "listTransaction"
            else if(data.type == "transfer") type = "listTransfer"

            var ref = Firebase.database.reference.child("transaksi").child(type).child(data.id)

            ref.child("amount").setValue(data.amount)
            ref.child("date").setValue(data.date)

            if(type == "listTransfer") ref.child("walletFrom").setValue(data.wallet)
            else ref.child("wallet").setValue(data.wallet)

            if(type == "listTransfer") ref.child("walletTo").setValue(data.cate)
            else ref.child("cate").setValue(data.cate)

            ref.child("notes").setValue(data.notes)

            if(type == "listTransfer") ref.child("imgLinkWalletFrom").setValue(data.imageLinkWallet)
            else ref.child("imgLinkWallet").setValue(data.imageLinkWallet)

            if(type == "listTransfer") ref.child("imgLinkWalletTo").setValue(data.imageLinkCategory)
            else ref.child("imgLinkCategory").setValue(data.imageLinkCategory)

        }

        fun syncWalletDatabase(data: Wallet){
            var ref = Firebase.database.reference.child("wallet").child("listWallet").child(data.id)
            ref.child("saldo").setValue(data.saldo)

        }

        fun syncCategoryDatabase(data: Category){
            var ref = Firebase.database.reference.child("category").child("listCategory").child(data.id)
            ref.child("expense").setValue(data.expense)
        }


    }

    private fun getIcon(){
        listIconCategory.clear()
        listIconWallet.clear()

        var storageReference = Firebase.storage.reference
        storageReference.child("wallet").listAll().addOnSuccessListener{
            it.items.forEach(){
                it.downloadUrl.addOnSuccessListener {
                    listIconWallet.add(it.toString())
                    checkDataIsChanged()
                }
            }
        }

        storageReference.child("Category").listAll().addOnSuccessListener{
            it.items.forEach(){
                it.downloadUrl.addOnSuccessListener {
                    listIconCategory.add(it.toString())
                    checkDataIsChanged()
                }
            }
        }

    }

    private fun calculateTotalWallet() {
        TotalBalance = 0
        listWallet.forEach {
            TotalBalance += it.saldo
        }
        binding.totalAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(TotalBalance)
    }

    fun checkDataIsChanged(){
        adapterW?.notifyDataSetChanged()
        adapterT?.notifyDataSetChanged()
    }



    private fun EventListenerWallet(ref: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listWallet.clear()
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    val post = snap.getValue<Wallet>()!!
                    addWallet(post)
                }
                Home.listWallet.add(Wallet("none","Add Wallet",0))
                calculateTotalWallet()

                listCategory.clear()
                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
                    val post = snap.getValue<Category>()!!
                    addCategory(post)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        ref.addValueEventListener(postListener)
    }

    private fun EventListenerTransaction(postReference: DatabaseReference) {
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

                    addTransaction(TransactionData(snap.key!!, "income", amount, date, wallet, cate, notes, imgWallet, imgCate))
                    Log.d("KeyTest", "" + snap.key)
                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransaction").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = snap.child("cate").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionData(snap.key!!, "expense", amount, date, wallet, cate, notes, imgWallet, imgCate))

                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransfer").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("walletFrom").value.toString()
                    cate = snap.child("walletTo").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWalletFrom").value.toString()
                    imgCate = snap.child("imgLinkWalletTo").value.toString()

                    addTransaction(TransactionData(snap.key!!, "transfer", amount, date, wallet, cate, notes, imgWallet, imgCate))

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


    fun addTransaction(data: TransactionData){
        listTransaction.add(data)
        checkDataIsChanged()
    }

    fun addWallet(data: Wallet){
        Home.listWallet.add(data)
        checkDataIsChanged()
    }
    fun addCategory(data: Category){
        Home.listCategory.add(data)
        checkDataIsChanged()
    }


    fun sortArray(){
        listTransaction.sortByDescending { it.date }
        while(listTransaction.size > 5){
            listTransaction.removeAt(listTransaction.size-1)
        }
    }

    private fun popUpEditDialog(i : Int) {
        val popupWindow = ViewTransactionDialog(listTransaction[i], i)
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
        checkDataIsChanged()
    }

    private fun popUpEditWallet(i: Int){
        val popupWindow = EditWalletDialog(listWallet[i], i)
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
        checkDataIsChanged()
    }

}