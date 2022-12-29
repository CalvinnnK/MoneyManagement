package com.example.moneymanagementproject

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.util.ArrayList

class StatWalletAdapter(private val context: Context?, private val arrayList: ArrayList<StatWallet>) : BaseAdapter() {

    private lateinit var name: TextView
    private lateinit var saldo: TextView
    private lateinit var income: TextView
    private lateinit var expense: TextView

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
        income = convertview!!.findViewById(R.id.item_stat_income)
        expense = convertview!!.findViewById(R.id.item_stat_expense)

        name.text = arrayList[position].nameWallet
        saldo.text = arrayList[position].saldo.toString()
        income.text = arrayList[position].income.toString()
        expense.text = arrayList[position].expense.toString()

        return convertView
    }
}