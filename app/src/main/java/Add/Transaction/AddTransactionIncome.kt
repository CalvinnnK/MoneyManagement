package Add.Transaction

import android.app.DatePickerDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentAddTransactionIncomeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionIncome : Fragment(){
    private var _binding: FragmentAddTransactionIncomeBinding? = null
    private val binding get() = _binding!!

    var walletList = ArrayList<String>()
    val sdf = SimpleDateFormat("d/M/yyyy")

    val databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionIncomeBinding.inflate(inflater,container,false)

        addPostEventListener(databaseReference)

//        childEventListenerRecycler()

        // Setting adapter dengan array wallet
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)

        // default date today
        binding.dateText.text = getCurrentDate()

        //show date dialog
        var c = Calendar.getInstance()
        var y = c.get(Calendar.YEAR)
        var m = c.get(Calendar.MONTH)
        var d = c.get(Calendar.DAY_OF_MONTH)

        binding.dateBtn.setOnClickListener{
            var datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                    view, year, month, day ->
                var a = month+1 // Tambah satu karena Calendar.Month januari dimulai dari 0

                binding.dateText.text = "" + day + "/" + a + "/" + year
            }, y, m, d)
            datePicker.show()
        }


        var storeRef = Firebase.storage.reference


//        storeRef.child("Category").listAll().addOnSuccessListener {
//            it.items.forEach(){
//                it.downloadUrl.addOnSuccessListener {
//                    var a = it.toString()
//                    Log.d("storeREf", "" + a)
//                }
//            }
//        }

        var a = storeRef.child("Category").child("Bonus.png").downloadUrl.toString()


        Log.d("storeREf", "" + storeRef.child("Category").child("Bonus.png").downloadUrl.toString())


        binding.addTransaction.setOnClickListener(){
            saveTransaction()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getCurrentDate():String{
        return sdf.format(Date())
    }

    private fun saveTransaction(){
        val a1 = binding.inputAmount.text.toString().trim()
        val a2 = binding.dateText.text.toString().trim()
        val a3 = binding.walletAutoComplete.text.toString().trim()
        val a4 = binding.inputNotes.text.toString().trim()
        var imgLinkWallet = ""
        val id = Firebase.database.reference.push().key!!

        var defaultImg = "https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/Category%2FBonus.png?alt=media&token=5e0a41ee-833e-42e0-8113-57099595d20b"

        var dateLong: Long = sdf.parse(a2).time


        if(a1.isEmpty() || a3.isEmpty() ){
            binding.inputAmount.error = "Please input amount!"
            binding.walletAutoComplete.error = "Please input wallet!"
        }
        else{
            val dataRef = databaseReference


            if (a1.isEmpty() || a3.isEmpty()) {
                binding.inputAmount.error = "Please input amount"
                binding.walletAutoComplete.error = "Please input wallet"
            }
            else{
                var addIncome: Long = 0
                var key = ""

                val saving = SaveIncome(a1.toLong(),dateLong, a3, a4, defaultImg)
                dataRef.child("transaksi").child("listIncome").child(id).setValue(saving)

                val changeData = object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(snap: DataSnapshot in snapshot.child("wallet").child("listWallet").children){
                            if(snap.child("nameWallet").value.toString() == a3){
                                //Ngambil value dari database lalu ditambah valuenya berdasarkan input transaksiIncome yang baru
                                addIncome = snap.child("saldo").value.toString().toLong() + a1.toLong()
                                key = snap.key.toString()

                                imgLinkWallet = snap.child("imageLink").value.toString()

                                Log.d("key", "" + key)
                                if(key != ""){
                                    dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                                    dataRef.child("transaksi").child("listIncome").child(id).child("imgLinkWallet").setValue(imgLinkWallet)
                                }
                                else{
                                    Log.d("key", "FAILED")
                                }
                            }
                        }

                        Toast.makeText(activity,"Transaction Saved", Toast.LENGTH_LONG).show()
                        activity?.finish()
                    }
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                }
                dataRef.addListenerForSingleValueEvent(changeData)


            }
        }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
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

