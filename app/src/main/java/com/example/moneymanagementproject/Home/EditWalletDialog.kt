package com.example.moneymanagementproject.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.AddWalletDialogBinding
import com.example.moneymanagementproject.databinding.FragmentEditWalletDialogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class EditWalletDialog(var data: Wallet, var position: Int) : DialogFragment()  {

    private var _binding : FragmentEditWalletDialogBinding? = null
    private val binding get() = _binding!!

    private var databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditWalletDialogBinding.inflate(inflater, container,false )

        binding.editWalletExit.setOnClickListener{
            dismiss()
        }

        binding.editWalletDelete.setOnClickListener{
            removeData(data)
        }

        binding.editWalletNotes.text = data.nameWallet
        binding.editWalletAmount.text = data.saldo.toString()
        Glide.with(requireContext()!!).load(data.imageLink).into(binding.editWalletImg)


        // Inflate the layout for this fragment
        return binding.root
    }


    private fun removeData(data: Wallet){
        databaseReference.child("wallet").child("listWallet").child(data.id).removeValue()
        dismiss()
    }

}

