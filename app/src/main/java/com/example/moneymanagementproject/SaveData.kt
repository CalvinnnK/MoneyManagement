package com.example.moneymanagementproject

import com.google.firebase.database.Exclude

data class SaveData(
    var id: String?,
    val amount: String,
    val date: String,
    val wallet:String,
    val cate: String,
    val notes: String ) {
}