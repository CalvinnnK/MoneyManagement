package Home

import android.R
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.databinding.FragmentViewTransactionDialogBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class ViewTransactionDialog(private val list: TransactionDialog) : DialogFragment() {

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
            removeData()
        }

        binding.editTransactionEdit.setOnClickListener{
            editData()
        }


        Glide.with(requireContext()!!).load(list.imageLinkCategory).into(binding.editTransactionImg)
        binding.editTransactionNotes.text = list.notes
        binding.editTransactionAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(list.amount)
        binding.editTransactionDate.text = getDate(list.date)


        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if(list.id == "expense"){
                    binding.editTransactionWallet.text = dataSnapshot.child("wallet").child("listWallet").child(list.wallet).child("nameWallet").value.toString()
                    binding.editTransactionCategory.text = dataSnapshot.child("category").child("listCategory").child(list.cate).child("nameCategory").value.toString()
                }
                else if(list.id == "income"){
                    binding.editTransactionWallet.text = dataSnapshot.child("wallet").child("listWallet").child(list.wallet).child("nameWallet").value.toString()
                    binding.editTransactionCategory.text = "Income"
                }
                else if(list.id == "transfer"){
                    binding.editTransactionWallet.text = dataSnapshot.child("wallet").child("listWallet").child(list.wallet).child("nameWallet").value.toString()
                    binding.editTransactionCategory.text = dataSnapshot.child("wallet").child("listWallet").child(list.cate).child("nameWallet").value.toString()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        databaseReference.addListenerForSingleValueEvent(postListener)



        // Inflate the layout for this fragment
        return binding.root
    }

    private fun editData() {
        val popupWindow = EditTransactionDialog(this.list)
        dismiss()
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
    }

    private fun removeData() {
        TODO("Not yet implemented")
    }

    fun getDate(date: Long): String? {
        val format = SimpleDateFormat("d MMMM yyyy")
        return format.format(date)
    }

}