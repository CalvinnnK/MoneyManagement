package Home

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.databinding.FragmentViewTransactionDialogBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewTransactionDialog(private val list: TransactionDialog, private var position: Int) : DialogFragment() {

    private var _binding : FragmentViewTransactionDialogBinding? = null
    private val binding get() = _binding!!
    private val databaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewTransactionDialogBinding.inflate(inflater, container, false)

        binding.editTransactionExit.setOnClickListener{
            dismiss()
        }

        binding.editTransactionDelete.setOnClickListener{
            removeData(list)
        }

        binding.editTransactionEdit.setOnClickListener{
            editData()
        }

        Log.d("ViewT", "${list.id} ${list.wallet} ${list.cate}")


        Glide.with(requireContext()!!).load(list.imageLinkCategory).into(binding.editTransactionImg)
        binding.editTransactionNotes.text = list.notes
        binding.editTransactionAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(list.amount)
        binding.editTransactionDate.text = getDate(list.date)


        if(list.type == "expense"){
            binding.editTransactionWallet.text = getWallet(list.wallet)
            binding.editTransactionCategory.text = getCategory(list.cate)
        }
        else if(list.type == "income"){
            binding.editTransactionWallet.text = getWallet(list.wallet)
            binding.editTransactionCategory.text = "Income"
        }
        else if(list.type == "transfer"){
            binding.editTransactionWallet.text = getWallet(list.wallet)
            binding.editTransactionCategory.text = "Transfer"
        }



        // Inflate the layout for this fragment
        return binding.root
    }
    private fun getWallet(id: String): String{
        Home.listWallet.forEach {
            if(it.id == id) return it.nameWallet
        }
        return "-"
    }


    private fun getCategory(id: String): String{
        Home.listCategory.forEach {
            if(it.id == id) return it.nameCategory
        }
        return "-"
    }

    private fun editData() {
        val popupWindow = EditTransactionDialog(this.list, position)
        dismiss()
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
    }

    private fun removeData(list: TransactionDialog) {
        var amount: Long = 0
        if(list.type == "income"){
            Home.listWallet.forEach {
                if(it.id == list.wallet){
                    amount = it.saldo - list.amount
                    databaseReference.child("wallet").child("listWallet").child(it.id).child("saldo").setValue(amount)
                }
            }
            databaseReference.child("transaksi").child("listIncome").child(list.id).removeValue()
        }
        else if(list.type == "expense"){
            Home.listWallet.forEach {
                if(it.id == list.wallet){
                    amount = it.saldo + list.amount
                    databaseReference.child("wallet").child("listWallet").child(it.id).child("saldo").setValue(amount)

                }
            }
            Home.listCategory.forEach {
                if(it.id == list.cate){
                    amount = it.expense - list.amount
                    databaseReference.child("category").child("listCategory").child(it.id).child("expense").setValue(amount)
                }
            }
            databaseReference.child("transaksi").child("listTransaction").child(list.id).removeValue()
        }
        else if(list.type == "transfer"){
            Home.listWallet.forEach {
                if(it.id == list.wallet){
                    amount = it.saldo + list.amount
                    databaseReference.child("wallet").child("listWallet").child(it.id).child("saldo").setValue(amount)
                }
                else if(it.id == list.cate){
                    amount = it.saldo - list.amount
                    databaseReference.child("wallet").child("listWallet").child(it.id).child("saldo").setValue(amount)
                }
            }
            databaseReference.child("transaksi").child("listTransfer").child(list.id).removeValue()
        }

        Toast.makeText(context,"Transaction Deleted", Toast.LENGTH_LONG).show()
        dismiss()
    }

    fun getDate(date: Long): String? {
        val format = SimpleDateFormat("d MMMM yyyy")
        return format.format(date)
    }

}