package com.example.moneymanagementproject

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.lang.Exception

class TransactionViewModel: ViewModel() {

    private val dbTransaction = FirebaseDatabase.getInstance().getReference(NODE_TRANSACTION)

    private val _result = MutableLiveData<Exception?>()
    val result: LiveData<Exception?> get() = _result

    private val _saveData = MutableLiveData<SaveData?>()
    val savedata: LiveData<SaveData?> get() = _saveData

    fun addTransaction(saveData: SaveData){
        saveData.id = dbTransaction.push().key
        dbTransaction.child("transaction").child(saveData.id!!).setValue(saveData).addOnCompleteListener {
            if(it.isSuccessful) {
                _result.value = null
            }else{
                _result.value = it.exception
            }
        }
    }

    private val childEventListener = object: ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            val save = snapshot.getValue(SaveData::class.java)
            save?.id = snapshot.key
            _saveData.value = save!!
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
            TODO("Not yet implemented")
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            TODO("Not yet implemented")
        }

        override fun onCancelled(error: DatabaseError) {
            TODO("Not yet implemented")
        }
    }

    fun getRealtimeUpdate(){
        dbTransaction.addChildEventListener(childEventListener)
    }

    override fun onCleared() {
        super.onCleared()
        dbTransaction.removeEventListener(childEventListener)
    }


}