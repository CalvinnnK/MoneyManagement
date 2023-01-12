package com.example.moneymanagementproject

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.databinding.FragmentAddCategoryNameBinding
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AddCategoryName : DialogFragment() {
    private var _binding : FragmentAddCategoryNameBinding? = null
    private val binding get() = _binding!!

    private var arrayList: ArrayList<String> = ArrayList()

    private var storageReference = Firebase.storage.reference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCategoryNameBinding.inflate(inflater, container, false)

            var downloadLink = ""
            storageReference.child("wallet").listAll().addOnSuccessListener{ (items, prefixes) ->
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
//                        Glide.with(requireContext()).load(downloadLink).into(binding.imageTest)
                        }).addOnFailureListener(OnFailureListener {
                        // Handle any errors
                    })
                }
            }.addOnFailureListener {
                // Uh-oh, an error occurred!
            }


        binding.addCategory.setOnClickListener{
            addCategory()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addCategory(){
        val a = binding.categoryNameInput.text.toString()
        val id = Firebase.database.reference.push().key
        val dataRef = Firebase.database.reference

        if (a.isEmpty() || id.isNullOrEmpty()){
            binding.categoryNameInput.error = "Please input category name!"
        }
        else{
            dataRef.child("category").child("listCategory").child(id).setValue(Category(a,0)).addOnCompleteListener {
                Toast.makeText(activity,"Category Added", Toast.LENGTH_LONG).show()
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
