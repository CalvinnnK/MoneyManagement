package com.example.moneymanagementproject


import Add.Transaction.AddTransaction
import Home.Home
import Transaction.TransactionDialog
import Statistic.Wallet.StatisticWallet
import Statistics.Category.Statistics
import Transaction.Transaction
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.databinding.ActivityMainBinding
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var bottomNav : BottomNavigationView
    private lateinit var mAuth: FirebaseAuth
    private val databaseReference = Firebase.database.reference

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest




    public override fun onStart() {
        super.onStart()
        readDatabase(databaseReference)

    }


    private fun readDatabase(postReference: DatabaseReference) {
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Transaction.arrayListTransaction.clear()
                arrayListTransactionMain.clear()
                // Get Post object and use the values to update the UI
                var amount: Long = 0
                var date: Long = 0
                var wallet = ""
                var cate = ""
                var notes = ""
                var imgWallet = ""
                var imgCate = ""

                // Income
                for(snap : DataSnapshot in dataSnapshot.child("transaksi").child("listIncome").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = "Income"
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog(snap.key!!, "income", amount, date, wallet, cate, notes, imgWallet, imgCate))
                }

                for(snap : DataSnapshot in dataSnapshot.child("transaksi").child("listTransaction").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = snap.child("cate").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog(snap.key!!,"expense", amount, date, wallet, cate, notes, imgWallet, imgCate))
                }

                for(snap : DataSnapshot in dataSnapshot.child("transaksi").child("listTransfer").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("walletFrom").value.toString()
                    cate = snap.child("walletTo").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWalletFrom").value.toString()
                    imgCate = snap.child("imgLinkWalletTo").value.toString()

                    addTransaction(TransactionDialog(snap.key!!, "transfer", amount, date, wallet, cate, notes, imgWallet, imgCate))
                }



            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // default fragment
        loadFragment(Home())

        // Bottom nav bar, navigating to another pages (fragments)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home_ic -> loadFragment(Home())
                R.id.transaction_ic -> loadFragment(Transaction())
                R.id.stats_ic -> loadFragment(Statistics())
                R.id.stats_wallet_ic -> loadFragment(StatisticWallet())
                else -> {
                }
            }
            true
        }

        // Pop up window add transaction
        binding.addTransc.setOnClickListener {
            val intent = Intent(this, AddTransaction::class.java)
            // start your next activity
            startActivity(intent)
        }

    }

    companion object{
        var arrayListTransactionMain: ArrayList<TransactionDialog> = ArrayList()
    }

    private fun loadFragment(fragment: Fragment) {
        val transc = supportFragmentManager.beginTransaction()
        transc.replace(R.id.fragmentContainer, fragment)
        transc.addToBackStack(null)
        transc.commit()
    }

    fun addTransaction(data: TransactionDialog){
        arrayListTransactionMain.add(data)
        Transaction.addTransaction(data)
    }

}



