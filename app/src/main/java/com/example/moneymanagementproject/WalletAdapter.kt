package com.example.moneymanagementproject

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import kotlinx.coroutines.NonDisposableHandle.parent
import java.util.ArrayList

class WalletAdapter(private val context: Context?,  private val arrayList: ArrayList<Wallet>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if(listItemView == null){
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_wallet, parent, false)
        }


        val nameWallet : TextView= listItemView!!.findViewById<TextView>(R.id.name_wallet)
        val saldoWallet : TextView= listItemView!!.findViewById<TextView>(R.id.saldo_wallet)

        nameWallet.text = arrayList[position].nameWallet
        saldoWallet.text = arrayList[position].saldo.toString()

        Log.d("walletAdapter","namaWallet" + nameWallet)



        return listItemView
    }

}