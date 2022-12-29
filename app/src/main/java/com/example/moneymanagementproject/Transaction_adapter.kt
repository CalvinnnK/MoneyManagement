package com.example.moneymanagementproject

import android.content.Context

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class TransactionAdapter(private val context: Context?, private val arrayList: ArrayList<SaveData>) : BaseAdapter(){

    private lateinit var row_text : TextView
    private lateinit var row_amount : TextView
    private lateinit var row_dates : TextView

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
        convertView = LayoutInflater.from(context).inflate(R.layout.recycler_view_transaction, parent, false)
        row_text = convertView.findViewById(R.id.trans_text)
        row_amount = convertView.findViewById(R.id.trans_amount)

        row_text.text = " " + arrayList[position].notes
        row_amount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(arrayList[position].amount)

        return convertView
    }


}