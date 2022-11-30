package com.example.moneymanagementproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding

class addTransactionDialog : DialogFragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    private lateinit var autoCompleteTextView: AutoCompleteTextView

//    array list wallet
    var walletList = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

        //list wallet list
        walletList.add("Bank")
        walletList.add("Bank1")
        walletList.add("Bank2")


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        return binding.root

        autoCompleteTextView = binding.root.findViewById(R.id.walletAutoComplete)

        val arrayAdapter = ArrayAdapter(requireContext(),R.layout.fragment_add_transaction_dialog, walletList)
        autoCompleteTextView.setAdapter(arrayAdapter)







    }

}





