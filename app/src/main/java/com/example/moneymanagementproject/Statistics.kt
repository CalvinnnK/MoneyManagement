package com.example.moneymanagementproject

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.example.moneymanagementproject.databinding.FragmentStatisticsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Statistics : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var listStatCategory: ArrayList<Category> = ArrayList()
    private val databaseReference: DatabaseReference = Firebase.database.reference

    private var adapter : StatisticsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater,container, false)

        addPostEventListener(databaseReference)

        adapter = StatisticsAdapter(context,listStatCategory)
        binding.statCateListView.adapter = adapter


        checkDataIsChanged()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        var name = ""
        var balancePerCategory: Long = 0

        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates

                //Ngitung expense tiap category
                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
                    balancePerCategory = 0
                    name = snap.child("nameCategory").value.toString()
                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransaction").children){
                        if(name == snap1.child("cate").value.toString()){
                            balancePerCategory += snap1.child("amount").value.toString().toLong()
                        }
                    }
                    addStatCate(Category(name, balancePerCategory))
                    Log.d("Stat", "" + listStatCategory.size)

                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }

        postReference.addValueEventListener(postListener)

        // [END post_value_event_listener]
    }
    fun addStatCate(data: Category){
        this.listStatCategory.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}