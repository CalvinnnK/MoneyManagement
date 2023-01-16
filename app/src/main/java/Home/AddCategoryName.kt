package Home

import Statistics.Category.Category
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
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


        storageReference.child("Category").listAll().addOnSuccessListener{
            it.items.forEach(){
                it.downloadUrl.addOnSuccessListener {
                    addListCategory(it.toString())
                    Log.d("ListViewInner", "" + listCategory.size + " " + it)
                    checkDataIsChanged()
                }
            }

        }

        Log.d("ListViewOuter", "" + listCategory.size)

        adapter = IconCategoryAdapter(context,listCategory)
        binding.categoryGV.adapter = adapter

        var link = ""
        binding.categoryGV.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            var position = i
            position += 1
            Toast.makeText(
                requireContext(), "Icon " + position + " selected",
                Toast.LENGTH_SHORT
            ).show()
            link = listCategory[i]
        }

        // Add wallet button
        binding.addCategory.setOnClickListener{
            if(link != ""){
                addCategory(link)
            }
            else{
                binding.categoryNameInput.error = "Please select an icon!!"
            }
        }

        // Inflate the layout for this fragment
        checkDataIsChanged()
        return binding.root
    }

    private fun addCategory(link: String){
        val a = binding.categoryNameInput.text.toString()
        val id = Firebase.database.reference.push().key
        var imgLink = link
        val dataRef = Firebase.database.reference

        if (a.isEmpty() || id.isNullOrEmpty()){
            binding.categoryNameInput.error = "Please input category name!"
        }
        else{
            dataRef.child("category").child("listCategory").child(id).setValue(Category(a,0, imgLink)).addOnCompleteListener {
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

    fun checkDataIsChanged(){
        adapter!!.notifyDataSetChanged()
    }

    fun addListCategory(input: String){
        this.listCategory.add(input)
    }
}
