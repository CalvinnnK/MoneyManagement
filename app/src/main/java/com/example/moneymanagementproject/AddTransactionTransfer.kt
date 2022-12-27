package com.example.moneymanagementproject

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.DatePicker
import android.widget.Toast
import com.example.moneymanagementproject.databinding.FragmentAddTransactionTransferBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionTransfer : Fragment(), DatePickerDialog.OnDateSetListener {
    private var _binding: FragmentAddTransactionTransferBinding? = null
    private val binding get() = _binding!!

    var walletList = ArrayList<String>()

    val databaseReference: DatabaseReference = Firebase.database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionTransferBinding.inflate(inflater,container,false)

//        childEventListenerRecycler()
        addPostEventListener(databaseReference)

        // Setting adapter dengan array wallet
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        binding.walletFromAutoComplete.setAdapter(arrayAdapter1)
        binding.walletToAutoComplete.setAdapter(arrayAdapter1)
        // default date today
        binding.dateText.text = getCurrentDate()

        //show date dialog
        binding.dateBtn.setOnClickListener{
            showDateDialog()
        }

        binding.addTransaction.setOnClickListener(){
            saveTransaction()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    fun showDateDialog(){
        val datePicker = DatePickerDialog(requireContext(),
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val selectedDate = "$p3/$p2/$p1"
        binding.dateText.text = selectedDate
    }

    private fun saveTransaction(){
        val a1 = binding.inputAmount.text.toString().trim()
        val a2 = binding.dateText.text.toString().trim()
        val a3 = binding.walletFromAutoComplete.text.toString().trim()
        val a4 = binding.walletToAutoComplete.text.toString().trim()
        val a5 = binding.inputNotes.text.toString().trim()
        val id = Firebase.database.reference.push().key


        if(a1.isEmpty() || a3.isEmpty() || a4.isEmpty()){
            binding.inputAmount.error = "Please input amount!"
            binding.walletFromAutoComplete.error = "Please input wallet!"
            binding.walletToAutoComplete.error = "Please input wallet!"
        }
        if(a3 == a4){
            binding.walletToAutoComplete.error = "Please input a different wallet!"
        }
        else{
            val dataRef = databaseReference

            var addIncome: Long = 0
            var key = ""

            val changeData = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(snap: DataSnapshot in snapshot.child("wallet").child("listWallet").children){
                        //Nyari child database utk wallet asal
                        if(snap.child("nameWallet").value.toString() == a3){
                            //Ngambil value dari database lalu ditambah valuenya berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() - a1.toLong()
                            key = snap.key.toString()
                            Log.d("key", "" + key)
                            if(key != "") dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                            else{
                                Log.d("key", "FAILED")
                            }
                        }
                        else if(snap.child("nameWallet").value.toString() == a4){
                            //Ngambil value dari database lalu ditambah valuenya berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() + a1.toLong()
                            key = snap.key.toString()
                            Log.d("key", "" + key)
                            if(key != "") dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                            else{
                                Log.d("key", "FAILED")
                            }
                        }
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            dataRef.addListenerForSingleValueEvent(changeData)

            val saving = SaveTransfer("Transfer",a1.toLong(),a2,a3,a4,a5)

            if (id != null) {
                dataRef.child("transaksi").child("listTransfer").child(id).setValue(saving).addOnCompleteListener{
                    Toast.makeText(activity,"Transaction Saved", Toast.LENGTH_LONG).show()
                    activity?.finish()
                }
            }
        }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                walletList.clear()
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    val post = snap.child("nameWallet").value.toString()
                    walletList.add(post)
//                    Log.d("onDataChangeWall", "" + post)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }
}