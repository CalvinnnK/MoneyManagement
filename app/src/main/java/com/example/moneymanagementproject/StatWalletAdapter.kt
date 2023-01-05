package com.example.moneymanagementproject

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ProgressBar
import android.widget.TextView
import java.text.NumberFormat
import java.util.*

class StatWalletAdapter(private val context: Context?, private val arrayList: ArrayList<StatWallet>) : BaseAdapter() {

    private lateinit var name: TextView
    private lateinit var saldo: TextView
    private lateinit var income: TextView
    private lateinit var expense: TextView
    private lateinit var progress: ProgressBar
    private var currentProgress: Double = 0.0


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
        convertView = LayoutInflater.from(context).inflate(R.layout.item_stat_wallet, parent, false)
        name = convertView.findViewById(R.id.item_stat_nameWallet)
        saldo = convertView.findViewById(R.id.item_stat_saldoWallet)
        income = convertView.findViewById(R.id.item_stat_income)
        expense = convertView.findViewById(R.id.item_stat_expense)
        progress = convertView.findViewById(R.id.progress_bar)

        name.text = arrayList[position].nameWallet
        saldo.text = "Rp " + NumberFormat.getNumberInstance(Locale.US).format(arrayList[position].saldo)
        income.text = "Rp " + NumberFormat.getNumberInstance(Locale.US).format(arrayList[position].income)
        expense.text = "Rp " + NumberFormat.getNumberInstance(Locale.US).format(arrayList[position].expense)

        currentProgress = 0.0
        progress.max = 100

        var value: Double
        if(arrayList[position].income.toInt() == 0 || arrayList[position].expense.toInt() == 0){
            value = 100.0
        }
        else{
            value = arrayList[position].expense.toDouble() / arrayList[position].income.toDouble() * 100
        }
//        Log.d("CurrentProg", "" + value.toString() + " " + arrayList[position].nameWallet + " " + arrayList[position].income)

        progress.progress = value.toInt()

        return convertView
    }
}