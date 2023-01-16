package Add.Transaction

import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentAddTransactionExpenseBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionExpense : Fragment(){

    private var _binding: FragmentAddTransactionExpenseBinding? = null
    private val binding get() = _binding!!

    var walletList = ArrayList<String>()
    var categoryList = ArrayList<String>()

    val sdf = SimpleDateFormat("d/M/yyyy")

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
        arrayAdapter1.notifyDataSetChanged()
        arrayAdapter2.notifyDataSetChanged()

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
        return sdf.format(Calendar.getInstance().time)
    }


    private fun saveTransaction(){
        val a1 = binding.inputAmount.text.toString().trim()
        val a2 = binding.dateText.text.toString().trim()
        val a3 = binding.walletAutoComplete.text.toString().trim()
        val a4 = binding.categoryAutoComplete.text.toString().trim()
        val a5 = binding.inputNotes.text.toString().trim()
        var imgLinkWallet = ""
        var imgLinkCate = ""
        val id = Firebase.database.reference.push().key

        //convert date to long
        var dateLong: Long = sdf.parse(a2).time
        var dateString: String = sdf.format(dateLong)



        if(a1.isEmpty() || a3.isEmpty() ){
            binding.inputAmount.error = "Please input amount!"
            binding.walletAutoComplete.error = "Please input wallet!"
            binding.categoryAutoComplete.error = "Please input category!"
        }
        else{
            val dataRef = databaseReference

            var addIncome: Long = 0
            var key = ""

            val saving = SaveData( a1.toLong(), dateLong, a3, a4, a5, imgLinkWallet, imgLinkCate)
            dataRef.child("transaksi").child("listTransaction").child(id!!).setValue(saving)

            val changeData = object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    // condition untuk nambahin saldo wallet
                    for(snap: DataSnapshot in snapshot.child("wallet").child("listWallet").children){
                        if(snap.child("nameWallet").value.toString() == a3){
                            //Ngambil value dari database lalu ditambah saldo wallet berdasarkan input transaksiIncome yang baru
                            addIncome = snap.child("saldo").value.toString().toLong() - a1.toLong()
                            key = snap.key.toString()

                            imgLinkWallet = snap.child("imageLink").value.toString()

//                            Log.d("icon", "wall : " + imgLinkWallet)

                            if(key != "") {
                                dataRef.child("wallet").child("listWallet").child(key).child("saldo").setValue(addIncome)
                                dataRef.child("transaksi").child("listTransaction").child(id).child("imgLinkWallet").setValue(imgLinkWallet)
                                dataRef.child("transaksi").child("listTransaction").child(id).child("wallet").setValue(key)
                                Log.d("IDDD", "" + id + " " + imgLinkWallet)
                            }
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
                            imgLinkCate = snap.child("imgLink").value.toString()

//                            Log.d("icon", "cate : " + imgLinkCate)

                            if(key != ""){
                                dataRef.child("category").child("listCategory").child(key).child("expense").setValue(addIncome)
                                dataRef.child("transaksi").child("listTransaction").child(id).child("imgLinkCategory").setValue(imgLinkCate)
                                dataRef.child("transaksi").child("listTransaction").child(id).child("cate").setValue(key)
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


}