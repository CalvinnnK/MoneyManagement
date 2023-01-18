package Add.Transaction

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentAddTransactionTransferBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionTransfer : Fragment() {
    private var _binding: FragmentAddTransactionTransferBinding? = null
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
        val a3 = binding.walletFromAutoComplete.text.toString().trim()
        val a4 = binding.walletToAutoComplete.text.toString().trim()
        var a5 = binding.inputNotes.text.toString().trim()
        var imgLinkWallet = ""
        val id = Firebase.database.reference.push().key!!

        var dateLong: Long = sdf.parse(a2).time


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

            if(a5 == "") a5 = "Transfer"

            val saving = SaveTransfer(a1.toLong(),dateLong,a3,a4,a5,"","")
            dataRef.child("transaksi").child("listTransfer").child(id).setValue(saving)

            val changeData = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(snap: DataSnapshot in snapshot.child("wallet").child("listWallet").children){
                        //Nyari child database utk wallet asal
                        if(snap.child("nameWallet").value.toString() == a3){
                            //Ngambil value dari database lalu ditambah valuenya berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() - a1.toLong()
                            key = snap.key.toString()

                            imgLinkWallet = snap.child("imageLink").value.toString()

                            Log.d("key", "" + key)
                            if(key != ""){
                                dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                                dataRef.child("transaksi").child("listTransfer").child(id).child("imgLinkWalletFrom").setValue(imgLinkWallet)
                                dataRef.child("transaksi").child("listTransfer").child(id).child("walletFrom").setValue(key)
                            }
                            else{
                                Log.d("key", "FAILED")
                            }
                        }
                        else if(snap.child("nameWallet").value.toString() == a4){
                            //Ngambil value dari database lalu ditambah valuenya berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() + a1.toLong()
                            key = snap.key.toString()

                            imgLinkWallet = snap.child("imageLink").value.toString()

                            Log.d("key", "" + key)
                            if(key != ""){
                                dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                                dataRef.child("transaksi").child("listTransfer").child(id).child("imgLinkWalletTo").setValue(imgLinkWallet)
                                dataRef.child("transaksi").child("listTransfer").child(id).child("walletTo").setValue(key)

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