package com.example.moneymanagementproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.FragmentAddTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding

class addTransactionDialog : DialogFragment() {

    private var _binding: FragmentAddTransactionDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoCompleteTextView: AutoCompleteTextView

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


        return  binding.root
    }


    fun addWallet(input: String){
        this.walletList.add(input)
    }

}






