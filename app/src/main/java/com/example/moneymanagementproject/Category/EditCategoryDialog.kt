package com.example.moneymanagementproject.Home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.Category.Category
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.AddWalletDialogBinding
import com.example.moneymanagementproject.databinding.FragmentEditCategoryDialogBinding
import com.example.moneymanagementproject.databinding.FragmentEditWalletDialogBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*

class EditCategoryDialog(var data: Category, var position: Int) : DialogFragment()  {

    private var _binding : FragmentEditCategoryDialogBinding? = null
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
        _binding = FragmentEditCategoryDialogBinding.inflate(inflater, container,false )

        binding.editCategoryExit.setOnClickListener{
            dismiss()
        }

        binding.editCategoryDelete.setOnClickListener{
            removeData(data)
        }

        binding.editCategoryNotes.text = data.nameCategory
        binding.editCategoryAmount.text = data.expense.toString()
        Glide.with(requireContext()!!).load(data.imgLink).into(binding.editCategoryImg)

        // Inflate the layout for this fragment
        return binding.root
    }


    private fun removeData(data: Category){
        databaseReference.child("category").child("listCategory").child(data.id).removeValue()
        dismiss()
    }

}

