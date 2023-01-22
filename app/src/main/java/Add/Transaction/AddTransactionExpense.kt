package Add.Transaction

import Home.Home
import android.app.DatePickerDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
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

    private var walletList = ArrayList<String>()
    private var categoryList = ArrayList<String>()

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
        // For drop down list
        Home.listWallet.forEachIndexed{ index, w ->
            if(index == Home.listWallet.size-1) return@forEachIndexed
            this.walletList.add(w.nameWallet)
        }
        Home.listCategory.forEachIndexed{ index, c ->
            this.categoryList.add(c.nameCategory)
        }

        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        binding.categoryAutoComplete.setAdapter(arrayAdapter2)

        arrayAdapter1.notifyDataSetChanged()
        arrayAdapter2.notifyDataSetChanged()


        binding.inputAmount.onFocusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                // code to execute when the EditText gains focus
            } else {
                // code to execute when the EditText loses focus
            }
        }


        binding.dateText.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                showCalendar()
            }
            true
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

    private fun showCalendar(){
        // default date today
        binding.dateText.text = Editable.Factory.getInstance().newEditable(getCurrentDate())

        //show date dialog
        var c = Calendar.getInstance()
        var y = c.get(Calendar.YEAR)
        var m = c.get(Calendar.MONTH)
        var d = c.get(Calendar.DAY_OF_MONTH)

        var datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, day ->
            var a = month+1 // Tambah satu karena Calendar.Month januari dimulai dari 0

            binding.dateText.text = Editable.Factory.getInstance().newEditable("" + day + "/" + a + "/" + year)
        }, y, m, d)
        datePicker.show()
    }


    private fun saveTransaction(){
        var a1 = binding.inputAmount.text.toString().trim()
        var a2 = binding.dateText.text.toString().trim()
        var a3 = binding.walletAutoComplete.text.toString().trim()
        var a4 = binding.categoryAutoComplete.text.toString().trim()
        var a5 = binding.inputNotes.text.toString().trim()
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

            var balanceW: Long = 0
            var balanceC: Long = 0
            if(a5 == "") a5 = a4

            Home.listWallet.forEach { w ->
                if(w.nameWallet == a3){
                    balanceW = w.saldo - a1.toLong()
                    a3 = w.id
                    imgLinkWallet = w.imageLink
                    return@forEach
                }
            }
            Home.listCategory.forEach { c->
                if(c.nameCategory == a4){
                    balanceC = c.expense + a1.toLong()
                    a4 = c.id
                    imgLinkCate = c.imgLink
                    return@forEach
                }
            }
            //assign saldo wallet
            dataRef.child("wallet").child("listWallet").child(a3).child("saldo").setValue(balanceW)
            //assign expense category
            dataRef.child("category").child("listCategory").child(a4).child("expense").setValue(balanceC)
            //assign new transaction
            val saving = SaveData(id!!,  a1.toLong(), dateLong, a3, a4, a5, imgLinkWallet, imgLinkCate)
            dataRef.child("transaksi").child("listTransaction").child(id!!).setValue(saving)

            Toast.makeText(activity,"Transaction Saved", Toast.LENGTH_LONG).show()
            activity?.finish()


        }
    }

//    private fun showList(postReference: DatabaseReference) {
//        // [START post_value_event_listener]
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                // Get Post object and use the values to update the UI
//                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
//                    val post = snap.child("nameCategory").value.toString()
//                    categoryList.add(post)
////                    Log.d("onDataChangeCate", "" + post)
//                }
//                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
//                    val post = snap.child("nameWallet").value.toString()
//                    walletList.add(post)
////                    Log.d("onDataChangeWall", "" + post)
//                }
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//        }
//        postReference.addValueEventListener(postListener)
//        // [END post_value_event_listener]
//    }


}