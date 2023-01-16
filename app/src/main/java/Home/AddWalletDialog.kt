package Home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
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

        // Hardcode Link Image Wallet Icon
        addIconWallet("https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/wallet%2FBCA.png?alt=media&token=b5689798-6f93-4055-b4fc-730c26eec9ad")
        addIconWallet("https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/wallet%2Fgopay.png?alt=media&token=84fff6e7-4b80-4f0f-90eb-6512d4651fd3")
        addIconWallet("https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/wallet%2FOVO.png?alt=media&token=919616e0-0ca6-4708-a8a7-e31dac0229a0")
        addIconWallet("https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/wallet%2FShopeePay.png?alt=media&token=985f3a02-df60-4209-9946-8e7b9cddaf5b")

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

    private fun addIconWallet(downloadLink: String) {
        iconWallet.add(downloadLink)
    }

    private fun addWallet(imgLink: String){
        val a = binding.walletNameInput.text.toString()
        val id = Firebase.database.reference.push().key
        val dataRef = Firebase.database.reference

        if (a.isEmpty() || id.isNullOrEmpty()){
            binding.walletNameInput.error = "Please input wallet name!"
        }
        else{
//            if(a.equals("Bank",true)){ imgLink = iconWallet[0]}
//            else if(a.equals("Go Pay",true)){imgLink = iconWallet[1]}
//            else if(a.equals("OVO",true)) {imgLink = iconWallet[2]}
//            else if(a.equals("Shopee Pay",true)){imgLink = iconWallet[3]}
//            else {imgLink = iconWallet[0]}

            dataRef.child("wallet").child("listWallet").child(id).setValue(Wallet(a,0,imgLink)).addOnCompleteListener {
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