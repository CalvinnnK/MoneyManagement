package com.example.moneymanagementproject

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class StatisticsAdapter(private val context: Context?, private val arrayList: ArrayList<Category>) : BaseAdapter()  {

    private lateinit var name: TextView
    private lateinit var percent: TextView
    private lateinit var balance: TextView
    private var total: Double = 0.0

    private val databaseReference = Firebase.database.reference

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertview: View?, parent: ViewGroup?): View {
        var convertView = convertview
        convertView = LayoutInflater.from(context).inflate(R.layout.item_stat_category, parent, false)



        name = convertView.findViewById(R.id.item_stat_nameCategory)
        percent = convertView.findViewById(R.id.item_stat_percentage)
        balance = convertView.findViewById(R.id.item_stat_expense)

        var p:Double = 0.0

        name.text = arrayList[position].nameCategory
        var listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                total = snapshot.child("category"). child("TotalExpense").child("expense").value.toString().toDouble()

                if(arrayList[position].expense.toInt() == 0){
                    p = 0.0
                }else{
                    p = arrayList[position].expense * 100 / total
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        databaseReference.addListenerForSingleValueEvent(listener)

        percent.text = p.toString() + "%"
//        Log.d("StatAdapter", "" + arrayList[position].nameCategory  + " " + arrayList[position].expense)
        balance.text = arrayList[position].expense.toString()

        return convertView
    }
}