package com.example.moneymanagementproject

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel: ViewModel() {
    // Buat Your Wallet di Home, narik array list dari activity ke fragment home
    val arrayListData = MutableLiveData<ArrayList<Wallet>>()

}