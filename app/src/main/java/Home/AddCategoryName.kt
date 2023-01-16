package Home

import Statistics.Category.Category
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.moneymanagementproject.databinding.FragmentAddCategoryNameBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage


class AddCategoryName : DialogFragment() {
    private var _binding : FragmentAddCategoryNameBinding? = null
    private val binding get() = _binding!!

    private var listCategory: ArrayList<String> = ArrayList()

    private var adapter: IconCategoryAdapter? = null

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
        storageReference.child("wallet").listAll().addOnSuccessListener{
            it.items.forEach(){
                var links = it.downloadUrl.toString()
                listCategory.add(links)
            }
        }

        adapter = IconCategoryAdapter(context,listCategory)
        binding.categoryGV.adapter = adapter




        //            var storeRef = Firebase.storage.reference
//            storeRef.child("Category").listAll().addOnSuccessListener {
//                it.items.forEach(){
//                    Log.d("storeREf", ""+it.downloadUrl.toString())
//                }
//            }



        binding.addCategory.setOnClickListener{
            addCategory()
        }
        // Inflate the layout for this fragment

        adapter!!.notifyDataSetChanged()
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
