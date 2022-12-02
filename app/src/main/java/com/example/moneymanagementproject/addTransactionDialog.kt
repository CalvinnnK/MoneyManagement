package com.example.moneymanagementproject

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.FragmentAddTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding
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

    private lateinit var autoCompleteTextView: AutoCompleteTextView

    //get all variable in add transaction fragment
    private lateinit var inputAmount: EditText
    private lateinit var dateBtn: Button
    private lateinit var dateText: TextView
    private lateinit var inputWallet: AutoCompleteTextView
    private lateinit var inputCategory: AutoCompleteTextView
    private lateinit var inputNotes: EditText

//    array list wallet

    var walletList = ArrayList<String>()
    var categoryList = ArrayList<String>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

        //list wallet list
        walletList.add("Bank")
        walletList.add("Gopay")
        walletList.add("OVO")

        categoryList.add("Makan")
        categoryList.add("Necessities")

        //set date text
//        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
//        val current = LocalDateTime.now().format(formatter)
        dateText.text = getCurrentDate()


    }

    fun getCurrentDate():String{
        val sdf = SimpleDateFormat("yyyy/MM/dd")
        return sdf.format(Date())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionDialogBinding.inflate(inflater, container, false)


        // Membuat menu drop list
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_wallet, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        binding.categoryAutoComplete.setAdapter(arrayAdapter2)


        //show date dialog
        binding.dateBtn.setOnClickListener{
            showDateDialog()
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


    fun addWallet(input: String){
        this.walletList.add(input)
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        val selectedDate: String = "$p3/$p2/$p1"
        binding.dateText.text = selectedDate
    }

}






