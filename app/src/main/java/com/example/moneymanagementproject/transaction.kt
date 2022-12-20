package com.example.moneymanagementproject

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

    //create object class transactionviewmodel
    private lateinit var viewModel: TransactionViewModel

    //ambil kelas adapter
    private var adapter : TransactionAdapter? = null

    //Arraylist transaksi
    private var arrayListTransaction: ArrayList<SaveData> = ArrayList()
    lateinit var listView: ListView

    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TransactionAdapter(context, arrayListTransaction)

        databaseReference = Firebase.database.reference

        childEventListenerRecycler()
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




    private fun childEventListenerRecycler() {

//        val key = databaseReference.child("transaksi").push().key

        val context = this
        // [START child_event_listener_recycler]
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "Check added:" + dataSnapshot.key!!)

                    for(snap: DataSnapshot in dataSnapshot.children){
                        val id =  snap.child("id").value.toString()
                        val amount =  snap.child("amount").value.toString()
                        val wallet =  snap.child("wallet").value.toString()
                        val date =  snap.child("date").value.toString()
                        val notes =  snap.child("notes").value.toString()
                        val cate =  snap.child("cate").value.toString()

                        addListTransaction(SaveData(id,amount,date,wallet,cate,notes))
                    }
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildChanged: ${dataSnapshot.key}")
                val dataRef = dataSnapshot.child("transaksi")
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                val newComment = dataRef.value
                val commentKey = dataRef.key

            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.key!!)
                val dataRef = dataSnapshot.child("transaksi")
                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
                val commentKey = dataRef.key

            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.key!!)
                val dataRef = dataSnapshot.child("transaksi")
                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                val movedComment = dataRef.getValue<SaveData>()
                val commentKey = dataRef.key

            }

            override fun onCancelled(databaseError: DatabaseError) {
//                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
//                Toast.makeText(context, "Failed to load comments.",
//                    Toast.LENGTH_SHORT).show()
            }
        }
        databaseReference.addChildEventListener(childEventListener)
        // [END child_event_listener_recycler]
    }



    fun addListTransaction(data: SaveData){
        this.arrayListTransaction.add(data)
        childEventListenerRecycler()
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}