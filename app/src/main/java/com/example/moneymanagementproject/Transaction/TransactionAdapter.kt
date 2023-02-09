package com.example.moneymanagementproject.Transaction

import android.content.Context
import android.graphics.Color

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class TransactionAdapter(private val context: Context?, private val arrayList: ArrayList<TransactionData>) : BaseAdapter() {

    private lateinit var notes : TextView
    private lateinit var amount : TextView
    private lateinit var date : TextView
    private lateinit var wallet : ImageView
    private lateinit var cate : ImageView

    private val simpleDateFormat = SimpleDateFormat("dd/MM")


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
        notes = convertView.findViewById(R.id.trans_text)
        amount = convertView.findViewById(R.id.trans_amount)
        date = convertView.findViewById(R.id.trans_date)
        wallet = convertView.findViewById(R.id.trans_wallet)
        cate = convertView.findViewById(R.id.trans_ic)

        notes.text = arrayList[position].notes

        if(notes.text == ""){
            notes.text = arrayList[position].cate
        }

        var datelong = arrayList[position].date

        date.text = simpleDateFormat.format(Date(datelong))

        if(arrayList[position].type == "expense"){
            amount.text = "-Rp " + NumberFormat.getInstance(Locale.US).format(arrayList[position].amount)
            amount.setTextColor(Color.parseColor("#F44336"))
        }
        else{
            amount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(arrayList[position].amount)
            amount.setTextColor(Color.parseColor("#00752F"))
        }



        Glide.with(context!!).load(arrayList[position].imageLinkCategory).into(cate)
        Glide.with(context!!).load(arrayList[position].imageLinkWallet).into(wallet)

        return convertView
    }
}
