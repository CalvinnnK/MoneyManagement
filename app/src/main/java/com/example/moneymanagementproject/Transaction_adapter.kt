package com.example.moneymanagementproject

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanagementproject.databinding.RecyclerViewTransactionBinding
import org.w3c.dom.Text
import java.util.ArrayList

class TransactionAdapter(private val context: Context?, private val arrayList: ArrayList<SaveData>) : BaseAdapter(){


//    var data = mutableListOf<SaveData>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//        return ViewHolder(RecyclerViewTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
//    }
//
//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        holder.binding.transText.text = data[position].notes
//        holder.binding.transAmount.text = data[position].amount
//    }
//    inner class ViewHolder(val binding: RecyclerViewTransactionBinding): RecyclerView.ViewHolder(binding.root){
//
//    }
//
//    fun addSaveData(saveData: SaveData){
//        if(!data.contains(saveData)){
//            data.add(saveData)
//        }
//        notifyDataSetChanged()
//    }

    private lateinit var row_text : TextView
    private lateinit var row_amount : TextView

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
        row_amount.text = arrayList[position].amount

        return convertView
    }


}