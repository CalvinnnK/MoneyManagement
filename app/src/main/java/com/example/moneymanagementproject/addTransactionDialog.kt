package com.example.moneymanagementproject

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.FragmentAddTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class addTransactionDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var activity: Activity
    private var transaction: Transaction = Transaction()

    private lateinit var autoCompleteTextView: AutoCompleteTextView

    //get all variable in add transaction fragment
    private lateinit var inputAmount: EditText
    private lateinit var dateText: TextView
    private lateinit var inputWallet: AutoCompleteTextView
    private lateinit var inputCategory: AutoCompleteTextView
    private lateinit var inputNotes: EditText
    private lateinit var saveBtn: Button

//    array list wallet
    var walletList = ArrayList<Wallet>()
    var categoryList = ArrayList<Category>()

    var database = Firebase.database.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

        //list wallet list
        walletList.add(Wallet("Bank",0))
        walletList.add(Wallet("Go Pay",0))
        walletList.add(Wallet("OVO",0))
        walletList.add(Wallet("Shopee Pay",0))

        categoryList.add(Category("Food",0))
        categoryList.add(Category("Clothes", 0))
        categoryList.add(Category("Groceries", 0))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)

        //setting id
        inputAmount = binding.inputAmount
        inputWallet = binding.walletAutoComplete
        inputCategory = binding.categoryAutoComplete
        inputNotes = binding.inputNotes
        dateText = binding.dateText


        // Membuat menu drop list
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        binding.categoryAutoComplete.setAdapter(arrayAdapter2)

        // default date today
        dateText.text = getCurrentDate()


        //show date dialog
        binding.dateBtn.setOnClickListener{
            showDateDialog()
        }

        binding.addTransaction.setOnClickListener(){
            saveTransaction()
        }

        return  binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    fun showDateDialog(){
        val datePicker: DatePickerDialog = DatePickerDialog(activity,
            this,
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePicker.show()
    }


//    fun addWallet(input: String){
//        this.walletList.add(input)
//    }

    private fun getCurrentDate():String{
        val sdf = SimpleDateFormat("dd/MM/yyyy")
        return sdf.format(Date())
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val selectedDate: String = "$p3/$p2/$p1"
        binding.dateText.text = selectedDate
    }

    private fun saveTransaction(){
        val a1 = inputAmount.text.toString().trim()
        val a2 = dateText.text.toString().trim()
        val a3 = inputWallet.text.toString().trim()
        val a4 = inputCategory.text.toString().trim()
        val a5 = inputNotes.text.toString().trim()
        val id = Firebase.database.reference.push().key

        Log.d(ContentValues.TAG, "saveTransaction:  " + id)

        if(a1.isEmpty() || a3.isEmpty() ){
            inputAmount.error = "Please input Amount"
            inputWallet.error = "Please input wallet"
            inputCategory.error = "Please Input Category"
        }
        else{
            val dataRef = FirebaseDatabase.getInstance("https://money-management-app-9810f-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference()

            val saving = SaveData(id, a1, a2, a3, a4, a5)

            if (id != null) {
                //add to arraylist in adapter
                transaction.addListTransaction(saving)

                dataRef.child("transaksi").child(id).setValue(saving).addOnCompleteListener{
                    Toast.makeText(activity,"Transaction Saved", Toast.LENGTH_LONG).show()
                    back_to_main()
                }

                transaction.checkDataIsChanged()
            }
        }
    }

    fun back_to_main(){
        if (dialog?.isShowing == true) {
            dialog!!.dismiss()
        }
    }




}






