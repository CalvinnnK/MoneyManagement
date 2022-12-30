package com.example.moneymanagementproject

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.moneymanagementproject.databinding.FragmentAddTransactionExpenseBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionExpense : Fragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentAddTransactionExpenseBinding? = null
    private val binding get() = _binding!!

    var walletList = ArrayList<String>()
    var categoryList = ArrayList<String>()

    private val databaseReference : DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionExpenseBinding.inflate(inflater,container,false)

        // Setting adapter dengan array wallet dan category
//        childEventListenerRecycler()
        addPostEventListener(databaseReference)
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        binding.categoryAutoComplete.setAdapter(arrayAdapter2)


        arrayAdapter1.notifyDataSetChanged()
        arrayAdapter2.notifyDataSetChanged()

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
        val a3 = binding.walletAutoComplete.text.toString().trim()
        val a4 = binding.categoryAutoComplete.text.toString().trim()
        val a5 = binding.inputNotes.text.toString().trim()
        val id = Firebase.database.reference.push().key


        if(a1.isEmpty() || a3.isEmpty() ){
            binding.inputAmount.error = "Please input amount!"
            binding.walletAutoComplete.error = "Please input wallet!"
            binding.categoryAutoComplete.error = "Please input category!"
        }
        else{
            val dataRef = databaseReference

            var addIncome: Long = 0
            var key = ""

            val changeData = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    // condition untuk nambahin saldo wallet
                    for(snap: DataSnapshot in snapshot.child("wallet").child("listWallet").children){
                        if(snap.child("nameWallet").value.toString() == a3){
                            //Ngambil value dari database lalu ditambah saldo wallet berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() - a1.toLong()
                            key = snap.key.toString()

                            if(key != "") dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                            else{
                                Log.d("key", "FAILED")
                            }
                        }
                    }

                    //condition untuk nambahin saldo category
                    for(snap: DataSnapshot in snapshot.child("category").child("listCategory").children){
                        // condition untuk nambahin saldo wallet
                        if(snap.child("nameCategory").value.toString() == a4){
                            //Ngambil value dari database lalu ditambah saldo category berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("expense").value.toString().toLong() + a1.toLong()
                            key = snap.key.toString()

//                            Log.d("AddTransactionKey", "" + key + " " + snap.child("expense").value.toString() + " " + a1)

                            if(key != "") dataRef.child("category").child("listCategory").child(key).child("expense").setValue(addIncome)
                            else{
                                Log.d("key", "FAILED")
                            }
                        }
                    }
                    //Ini buat masukin data ke dalam total expense overall
                    dataRef.child("category").child("TotalExpense").child("expense").setValue(addIncome)

                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }
            dataRef.addListenerForSingleValueEvent(changeData)

            val saving = SaveData("Expense", a1.toLong(), a2, a3, a4, a5)

            if (id != null) {
                dataRef.child("transaksi").child("listTransaction").child(id).setValue(saving).addOnCompleteListener{
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
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
                    val post = snap.child("nameCategory").value.toString()
                    categoryList.add(post)
//                    Log.d("onDataChangeCate", "" + post)
                }
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    val post = snap.child("nameWallet").value.toString()
                    walletList.add(post)
//                    Log.d("onDataChangeWall", "" + post)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }

//    private fun childEventListenerRecycler() {
//
//        val myQuery = FirebaseDatabase.getInstance().reference.orderByChild("nameWallet")
//        myQuery.addChildEventListener( object : ChildEventListener {
//            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d(TAG, "Check added:" + dataSnapshot.key!!)
//
//                for(snap: DataSnapshot in dataSnapshot.child("listWallet").children){
//                    val name =  snap.child("nameWallet").value.toString()
//                    if(name != "null"){
//                        walletList.add(name)
//                    }
//                    Log.d("datasnap","" + name + " " + walletList.count())
//                }
//
//                for(snap: DataSnapshot in dataSnapshot.child("listCategory").children){
//                    val name =  snap.child("nameCategory").value.toString()
//                    if(name != "null"){
//                        categoryList.add(name)
//                    }
//                    Log.d("datasnap","" + name + " " + categoryList.count())
//                }
//
//
//            }
//
//            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d(ContentValues.TAG, "onChildChanged: ${dataSnapshot.key}")
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so displayed the changed comment.
//                val newComment = dataRef.value
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
//                Log.d(ContentValues.TAG, "onChildRemoved:" + dataSnapshot.key!!)
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed, use the key to determine if we are displaying this
//                // comment and if so remove it.
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
//                Log.d(ContentValues.TAG, "onChildMoved:" + dataSnapshot.key!!)
//                val dataRef = dataSnapshot.child("transaksi")
//                // A comment has changed position, use the key to determine if we are
//                // displaying this comment and if so move it.
//                val movedComment = dataRef.getValue<SaveData>()
//                val commentKey = dataRef.key
//
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
////                Log.w(TAG, "postComments:onCancelled", databaseError.toException())
////                Toast.makeText(context, "Failed to load comments.",
////                    Toast.LENGTH_SHORT).show()
//            }
//        })
//    }

}