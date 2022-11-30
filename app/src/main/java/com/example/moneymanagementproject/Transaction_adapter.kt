package com.example.moneymanagementproject

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.moneymanagementproject.databinding.RecyclerViewTransactionBinding

class transaction_adapter: RecyclerView.Adapter<transaction_adapter.ViewHolder>() {

    var data = mutableListOf<SaveData>()


    inner class ViewHolder(val binding: RecyclerViewTransactionBinding): RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RecyclerViewTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
        return data.size
    }
}