package com.example.moneymanagementproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class TransactionViewModel: ViewModel() {

    private val dbTransaction = FirebaseDatabase.getInstance().getReference(NODE_TRANSACTION)

    private val _result = MutableLiveData<Exception?>()

    val result: LiveData<Exception?> get() = _result




}