package com.example.moneymanagementproject.Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.moneymanagementproject.R
import java.util.*

class IconWalletAdapter (private val context: Context?, private val arrayList: ArrayList<String>) :
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
        listItemView = LayoutInflater.from(context).inflate(R.layout.list_icon_wallet, parent, false)
        var imgLink : ImageView = listItemView!!.findViewById(R.id.list_icon_wallet_IV)

        val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
        Glide.with(context!!).load(arrayList[position]).apply(requestOptions).into(imgLink)


//        Glide.with(context!!).load(arrayList[position]).into(imgLink)

        return listItemView
    }

}