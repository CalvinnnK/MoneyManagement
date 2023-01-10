package com.example.moneymanagementproject

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.text.NumberFormat
import java.util.*
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    //Array kosong buat manggil array dari Activity Main
    private var listWalletF: ArrayList<Wallet> = ArrayList<Wallet>()
    private var TotalBalance: Long = 0

    val storage = Firebase.storage.reference


    private var databaseReference: DatabaseReference = Firebase.database.reference
//    private var storageReference: DatabaseReference = Firebase.database.reference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


        binding.homeTransactionSeeMore.setOnClickListener{
            val popupWindow = AddCategoryName()
            popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Category" )
        }

//        Adapter Wallet
        adapterW = WalletAdapter(context,listWalletF)
        binding.walletGrid.adapter = adapterW

        adapterW?.notifyDataSetChanged()
        binding.walletGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->

            if(i == listWalletF.size - 1){
                val popupWindow = AddWalletDialog()
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
            }
            else{
                Toast.makeText(
                    requireContext(), listWalletF[i].nameWallet + "selected" + i ,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        addPostEventListener(databaseReference.child("wallet"))


        val storageReference = Firebase.storage.reference
        var downloadLink: String = ""


//        storageReference.child("wallet/BCA.png").downloadUrl.addOnSuccessListener(
//            OnSuccessListener<Uri?> {
//                downloadLink = it.toString()
//                Log.d("imageeeOnsuccess", "" + downloadLink)
//                Glide.with(requireContext()).load(downloadLink).into(binding.imageTest)
//            }).addOnFailureListener(OnFailureListener {
//            // Handle any errors
//        })


        storageReference.child("wallet").listAll().addOnSuccessListener { (items, prefixes) ->
            prefixes.forEach { prefix ->
                // All the prefixes under listRef.
                // You may call listAll() recursively on them.
            }

            items.forEach {
//                Log.d("image", "" + it)
                it.downloadUrl.addOnSuccessListener(
                    OnSuccessListener<Uri?> {
                        downloadLink = it.toString()
//                        Log.d("imageURL", "" + downloadLink)
                        Glide.with(requireContext()).load(downloadLink).into(binding.imageTest)
                    }).addOnFailureListener(OnFailureListener {
                    // Handle any errors
                })
            }
        }.addOnFailureListener {
                // Uh-oh, an error occurred!
            }



        checkDataIsChanged()

        return binding.root
    }

    fun checkDataIsChanged(){
        adapterW?.notifyDataSetChanged()
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates and also reset balance too
                listWalletF.clear()
                TotalBalance = 0

                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("listWallet").children){
                    val post = snap.getValue<Wallet>()!!
                    addWallet(post)
                    TotalBalance += snap.child("saldo").value.toString().toLong()
                    binding.totalAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(TotalBalance)
                }
                listWalletF.add(Wallet("Add Wallet",0))
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }

    fun addWallet(data:Wallet){
        this.listWalletF.add(data)
        checkDataIsChanged()
    }
}
