package com.example.moneymanagementproject

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    //Array kosong buat manggil array dari Activity Main
    private var listWalletF: ArrayList<Wallet> = ArrayList<Wallet>()

    private lateinit var mainViewModel: MainViewModel

    private var databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        //Memunculkan add wallet dialog
        binding.walletAdd.setOnClickListener{
            val popupWindow = AddWalletDialog()
            popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
        }

//        Adapter Wallet
        adapterW = WalletAdapter(context,listWalletF)
        binding.walletGrid.adapter = adapterW
        adapterW?.notifyDataSetChanged()

        addPostEventListener(databaseReference.child("wallet"))

//        childEventListenerRecycler()
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
                // clean the array to avoid duplicates
                listWalletF.clear()
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("listWallet").children){
                    val post = snap.getValue<Wallet>()!!
                    addWallet(post)
                    Log.d("onDataChange", "" + post + " " + listWalletF.count())
                }
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
