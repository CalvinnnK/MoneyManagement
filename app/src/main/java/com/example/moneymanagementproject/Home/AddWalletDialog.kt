package com.example.moneymanagementproject.Home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.MainActivity
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.AddWalletDialogBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.ArrayList

class AddWalletDialog: DialogFragment() {
    private var _binding : AddWalletDialogBinding? = null
    private val binding get() = _binding!!

    private var storageReference = Firebase.storage.reference

    private var iconWallet: ArrayList<String> = ArrayList<String>()
    private var adapter : IconWalletAdapter? = null

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

        //get image link
        iconWallet.clear()
        MainActivity.listIconWallet.forEachIndexed { index, it ->
            addlistWallet(it)
        }


        adapter = IconWalletAdapter(requireContext(),iconWallet)
        binding.iconWalletGV.adapter = adapter


        var link = ""
        binding.iconWalletGV.onItemClickListener = AdapterView.OnItemClickListener{_,_,i,_ ->
            var position = i
            position += 1
            Toast.makeText(
                requireContext(), "Icon " + position + " selected",
                Toast.LENGTH_SHORT
            ).show()
            link = iconWallet[i]
        }

        // Add wallet button
        binding.addWallet.setOnClickListener{
            if(link != ""){
                addWallet(link)
            }
            else{
                binding.walletNameInput.error = "Please select an icon!!"
            }
        }

        return binding.root
    }

    private fun addWallet(imgLink: String){
        val a = binding.walletNameInput.text.toString()
        val id = Firebase.database.reference.push().key
        val dataRef = Firebase.database.reference

        if (a.isEmpty() || id.isNullOrEmpty()){
            binding.walletNameInput.error = "Please input wallet name!"
        }
        else{

            dataRef.child("wallet").child("listWallet").child(id).setValue(Wallet(id, a,0,imgLink)).addOnCompleteListener {
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

    fun addlistWallet(input: String){
        this.iconWallet.add(input)
    }

}