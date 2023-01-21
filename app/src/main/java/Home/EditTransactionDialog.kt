package Home

import android.app.DatePickerDialog
import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentEditTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentViewTransactionDialogBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditTransactionDialog(private var list: TransactionDialog) : DialogFragment() {
    private var _binding : FragmentEditTransactionDialogBinding? = null
    private val binding get() = _binding!!
    private val databaseReference = Firebase.database.reference

    private var transactionList: ArrayList<String> = arrayListOf("income", "expense", "transfer")
    private var walletList: ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()
    private var walletListID: ArrayList<String> = ArrayList()
    private var categoryListID: ArrayList<String> = ArrayList()
    private val sdf = SimpleDateFormat("d/M/yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditTransactionDialogBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(list.imageLinkCategory).into(binding.editTransactionImg)
        binding.editTransactionNotes.text = Editable.Factory.getInstance().newEditable(list.notes)
        binding.editTransactionAmount.text = Editable.Factory.getInstance().newEditable(list.amount.toString())
        binding.editTransactionDate.text = Editable.Factory.getInstance().newEditable(getDate(list.date))

        binding.editTransactionDate.setOnTouchListener { view, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                showCalendar()
            }
            true
        }

        // ambil wallet dan category dari firebase
        showList(databaseReference)
        // set text sesuai tipe transaksi yg dipilih


        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.item_category, transactionList)
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)


        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        binding.categoryAutoComplete.setAdapter(arrayAdapter2)

        arrayAdapter1.notifyDataSetChanged()
        arrayAdapter2.notifyDataSetChanged()

        binding.editTransactionExit.setOnClickListener{
            dismiss()
        }

        binding.editTransactionDone.setOnClickListener{
            saveTransaction()
        }



        var arrey = getWallet()

        Log.d("edit", "" + arrey)

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getWallet(): ArrayList<Wallet> {
        return Home.listWallet
    }

    private fun showCalendar(){
        //show date dialog
        var c = Calendar.getInstance()
        var y = c.get(Calendar.YEAR)
        var m = c.get(Calendar.MONTH)
        var d = c.get(Calendar.DAY_OF_MONTH)

        var datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, day ->
            var a = month+1 // Tambah satu karena Calendar.Month januari dimulai dari 0

            binding.editTransactionDate.text = Editable.Factory.getInstance().newEditable("" + day + "/" + a + "/" + year)
        }, y, m, d)
        datePicker.show()
    }

    fun getDate(date: Long): String? {
        val format = SimpleDateFormat("d/m/yyyy")
        return format.format(date).toString()
    }

    private fun showList(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
                    val post = snap.child("nameCategory").value.toString()
                    categoryList.add(post)
                    categoryListID.add(snap.key!!)
                }
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    val post = snap.child("nameWallet").value.toString()
                    walletList.add(post)
                    walletListID.add(snap.key!!)
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

    fun saveTransaction(){
        var type = ""
        //ambil data
            if(list.type == "income") type = "listIncome"
            else if(list.type == "expense") type = "listTransaction"
            else if(list.type == "transfer") type = "listTransfer"

            val ref = databaseReference.child("transaksi").child(type).child(list.id)
            val refWallet = databaseReference.child("wallet").child("listWallet").child(list.wallet)
            val refCate = databaseReference.child("category").child("listCategory").child(list.cate)

            //check amount
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var difference: Long = 0
                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("transaksi").child(type).children){
                   if(snap.key == list.id){
                       difference = snap.child("amount").value.toString().toLong() - binding.editTransactionAmount.text.toString().toLong()
                   }
                }

                ref.child("amount").setValue(binding.editTransactionAmount.text.toString().toLong())
                //merubah amount wallet
                var amountWallet: Long = 0
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    if(snap.key == list.wallet){
                        amountWallet = snap.child("saldo").value.toString().toLong() + difference
                    }
                }
                refWallet.child("saldo").setValue(amountWallet)



            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }
        databaseReference.addListenerForSingleValueEvent(postListener)

            //Check Notes
        ref.child("notes").setValue(binding.editTransactionNotes.text.toString())


            //check date


//        }
        dismiss()

    }


}