package Add.Transaction

import Home.Home
import android.app.DatePickerDialog
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentAddTransactionIncomeBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.text.SimpleDateFormat
import java.util.*

class AddTransactionIncome : Fragment(){
    private var _binding: FragmentAddTransactionIncomeBinding? = null
    private val binding get() = _binding!!

    var walletList = ArrayList<String>()
    val dateFormat = SimpleDateFormat("d/M/yyyy")

    val databaseReference: DatabaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddTransactionIncomeBinding.inflate(inflater,container,false)

        // For drop down list
        Home.listWallet.forEachIndexed{ index, w ->
            if(index == Home.listWallet.size-1) return@forEachIndexed
            this.walletList.add(w.nameWallet)
        }

        // Setting adapter dengan array wallet
        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)


        binding.dateText.setOnClickListener() {
                showCalendar()
        }
        binding.dateText.text = Editable.Factory.getInstance().newEditable(getCurrentDate())


        binding.addTransaction.setOnClickListener(){
            saveTransaction()
        }


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun showCalendar(){
        // default date today
        binding.dateText.text = Editable.Factory.getInstance().newEditable(getCurrentDate())

        //show date dialog
        var c = Calendar.getInstance()
        var y = c.get(Calendar.YEAR)
        var m = c.get(Calendar.MONTH)
        var d = c.get(Calendar.DAY_OF_MONTH)

        var datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, day ->
            var a = month+1 // Tambah satu karena Calendar.Month januari dimulai dari 0

            binding.dateText.text = Editable.Factory.getInstance().newEditable("" + day + "/" + a + "/" + year)
        }, y, m, d)
        datePicker.show()
    }

    private fun getCurrentDate():String{
        return dateFormat.format(Date())
    }

    private fun saveTransaction(){
        var a1 = binding.inputAmount.text.toString().trim()
        var a2 = binding.dateText.text.toString().trim()
        var a3 = binding.walletAutoComplete.text.toString().trim()
        var a4 = binding.inputNotes.text.toString().trim()
        var imgLinkWallet = ""
        val id = Firebase.database.reference.push().key!!

        var defaultImg = "https://firebasestorage.googleapis.com/v0/b/money-management-app-9810f.appspot.com/o/Category%2FBonus.png?alt=media&token=5e0a41ee-833e-42e0-8113-57099595d20b"
        var dateLong: Long = dateFormat.parse(a2).time


        if(a1.isEmpty() || a3.isEmpty() ){
            binding.inputAmount.error = "Please input amount!"
            binding.walletAutoComplete.error = "Please input wallet!"
        }
        else{
            val dataRef = databaseReference


            if (a1.isEmpty() || a3.isEmpty()) {
                binding.inputAmount.error = "Please input amount"
                binding.walletAutoComplete.error = "Please input wallet"
            }
            else{
                var addIncome: Long = 0
                if(a4 == "") a4 = "Income"

                Home.listWallet.forEach { w->
                    if(w.nameWallet == a3){
                        addIncome = w.saldo + a1.toLong()
                        a3 = w.id
                        imgLinkWallet = w.imageLink
                    }
                }

                dataRef.child("wallet").child("listWallet").child(a3).child("saldo").setValue(addIncome)
                val saving = SaveIncome(id, a1.toLong(),dateLong, a3, a4, defaultImg, imgLinkWallet)
                dataRef.child("transaksi").child("listIncome").child(id).setValue(saving)

                Toast.makeText(activity,"Transaction Saved", Toast.LENGTH_LONG).show()
                activity?.finish()

            }
        }
    }

}

