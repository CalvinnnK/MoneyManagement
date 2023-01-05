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
import java.text.DateFormat
import java.text.SimpleDateFormat
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
//                    Log.d("onDataChange", "" + post)
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


    private fun childEventListenerRecycler() {

        val myQuery = FirebaseDatabase.getInstance().reference.child("transaksi").child("listTransaction").orderByChild("date")
        // [START child_event_listener_recycler]
        myQuery.addChildEventListener( object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                for(snap: DataSnapshot in dataSnapshot.children){
                    val id =  snap.child("id").value.toString()
                    val amount =  snap.child("amount").value.toString()
                    val wallet =  snap.child("wallet").value.toString()
                    val date =  snap.child("date").value.toString()
                    val cate =  snap.child("cate").value.toString()
                    val notes =  snap.child("notes").value.toString()

                    if(id != "null"){
                        addListTransaction(SaveData(id,amount.toLong(),date.toLong(),wallet,cate,notes))
                    }
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
        })


//        databaseReference.addChildEventListener(childEventListener)
        // [END child_event_listener_recycler]
    }



    fun addListTransaction(data: SaveData){
        this.arrayListTransaction.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}