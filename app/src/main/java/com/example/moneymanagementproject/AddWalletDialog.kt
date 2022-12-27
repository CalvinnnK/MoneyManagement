package com.example.moneymanagementproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.AddWalletDialogBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class AddWalletDialog: DialogFragment() {
    private var _binding : AddWalletDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var nameWallet : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = AddWalletDialogBinding.inflate(inflater, container, false)

        // Add wallet button
        binding.addWallet.setOnClickListener{
            addWallet()
        }

        return binding.root
    }

    private fun addWallet(){
        val a = binding.walletNameInput.text.toString()
        val id = Firebase.database.reference.push().key
        val dataRef = Firebase.database.reference

        if (a.isEmpty() || id.isNullOrEmpty()){
            nameWallet.error = "Please input wallet name!"
        }
        else{
            dataRef.child("wallet").child("listWallet").child(id).setValue(Wallet(a,0)).addOnCompleteListener {
                Toast.makeText(activity,"Wallet Added", Toast.LENGTH_LONG).show()
                back_to_main()
            }
        }
    }

    fun back_to_main(){
        if (dialog?.isShowing == true) {
            dialog!!.dismiss()
        }
    }
}