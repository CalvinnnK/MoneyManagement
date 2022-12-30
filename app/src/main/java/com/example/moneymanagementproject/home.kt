package com.example.moneymanagementproject

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    //Array kosong buat manggil array dari Activity Main
    private var listWalletF: ArrayList<Wallet> = ArrayList<Wallet>()
    private var TotalBalance: Long = 0


    private var databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        //Memunculkan add wallet dialog
//        binding.walletAdd.setOnClickListener{
//            val popupWindow = AddWalletDialog()
//            popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
//        }

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

        addPostEventListener(databaseReference.child("wallet"))

        checkDataIsChanged()

        return binding.root
    }

    fun checkDataIsChanged(){
        adapterW?.notifyDataSetChanged()
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates and also reset balance too
                listWalletF.clear()
                TotalBalance = 0

                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("listWallet").children){
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
        // [END post_value_event_listener]
    }


//    private fun childEventListenerRecycler() {
//        val myQuery = FirebaseDatabase.getInstance().reference.orderByChild("nameWallet")
//        // [START child_event_listener_recycler]
//        myQuery.addChildEventListener( object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                for(snap : DataSnapshot in dataSnapshot.child("listWallet").children) {
//                    val name = snap.child("nameWallet").value.toString()
//                    val saldo = snap.child("saldo").value.toString()
//                    if (name.isNotEmpty() && saldo.isNotEmpty()) {
//                        addWallet(Wallet(name, saldo.toLong()))
//                    }
//
//                }
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so displayed the changed comment.
//                val newComment = dataRef.value
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so remove it.
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d(ContentValues.TAG, "onChildMoved:" + dataSnapshot.key!!)
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed position, use the key to determine if we are
//                // displaying this comment and if so move it.
//                val movedComment = dataRef.getValue<SaveData>()
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
////                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
////                Toast.makeText(context, "Failed to load comments.",
////                    Toast.LENGTH_SHORT).show()
//            }
//        })
//
//
////        databaseReference.addChildEventListener(childEventListener)
//        // [END child_event_listener_recycler]
//    }

    fun addWallet(data:Wallet){
        this.listWalletF.add(data)
        checkDataIsChanged()
    }
}
