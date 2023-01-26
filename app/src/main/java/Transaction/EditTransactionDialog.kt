package Transaction

import Home.Home
import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.MainActivity
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentEditTransactionDialogBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditTransactionDialog(private var list: TransactionDialog, private var position:Int) : DialogFragment() {
    private var _binding : FragmentEditTransactionDialogBinding? = null
    private val binding get() = _binding!!
    private val databaseReference = Firebase.database.reference

    private var walletList: ArrayList<String> = ArrayList()
    private var categoryList: ArrayList<String> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditTransactionDialogBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(list.imageLinkCategory).into(binding.editTransactionImg)
        binding.editTransactionNotes.text = Editable.Factory.getInstance().newEditable(list.notes)
        binding.editTransactionAmount.text = Editable.Factory.getInstance().newEditable(list.amount.toString())
        binding.editTransactionDate.text = Editable.Factory.getInstance().newEditable(getDate(list.date))
        binding.walletAutoComplete.text = Editable.Factory.getInstance().newEditable(getWallet(list.wallet))
        binding.categoryAutoComplete.text = Editable.Factory.getInstance().newEditable(getCategory(list.cate))

        binding.editTransactionDate.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                showCalendar()
            }
            true
        }

        // For drop down list
        Home.listWallet.forEachIndexed{ index, w ->
            if(index == Home.listWallet.size-1) return@forEachIndexed
            this.walletList.add(w.nameWallet)
        }
        Home.listCategory.forEach{ c ->
            this.categoryList.add(c.nameCategory)
        }


        val arrayAdapter1 = ArrayAdapter(requireContext(), R.layout.item_category, walletList)
        val arrayAdapter2 = ArrayAdapter(requireContext(), R.layout.item_category, categoryList)
        binding.walletAutoComplete.setAdapter(arrayAdapter1)
        if(list.type == "expense") binding.categoryAutoComplete.setAdapter(arrayAdapter2)
        else if(list.type == "transfer"){
            binding.categoryAutoComplete.text = Editable.Factory.getInstance().newEditable(getWallet(list.cate))
            binding.categoryAutoComplete.setAdapter(arrayAdapter1)
            binding.categoryTV.text = "Transfer:  "

        }


        arrayAdapter1.notifyDataSetChanged()
        arrayAdapter2.notifyDataSetChanged()

        binding.editTransactionExit.setOnClickListener{
            dismiss()
        }

        binding.editTransactionDone.setOnClickListener{
            saveTransaction()
        }

        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getWallet(id: String):String{
        Home.listWallet.forEach {
            if(it.id == id) return it.nameWallet
        }
        return "-"
    }

    private fun getWalletID(name: String):String{
        Home.listWallet.forEach {
            if(it.nameWallet == name) return it.id
        }
        return "-"
    }

    private fun getCategory(id: String):String{
        Home.listCategory.forEach {
            if(it.id == id) return it.nameCategory
        }
        return list.type
    }

    private fun getCategoryID(nameCategory: String): String {
        Home.listCategory.forEach {
            if(it.nameCategory == nameCategory) return it.id
        }
        return "-"
    }

    private fun showCalendar(){
        //show date dialog
        var c = Calendar.getInstance()
        var y = c.get(Calendar.YEAR)
        var m = c.get(Calendar.MONTH)
        var d = c.get(Calendar.DAY_OF_MONTH)

        var datePicker = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener{
                view, year, month, day ->
            var a = month+1 // Tambah satu karena Calendar.Month januari dimulai dari 0

            binding.editTransactionDate.text = Editable.Factory.getInstance().newEditable("" + day + "/" + a + "/" + year)
        }, y, m, d)
        datePicker.show()
    }

    fun getDate(date: Long): String? {
        val format = SimpleDateFormat("d/M/yyyy")
        return format.format(date).toString()
    }

    fun saveTransaction(){

        var type = ""
        if(list.type == "income") type = "listIncome"
        else if(list.type == "expense") type = "listTransaction"
        else if(list.type == "transfer") type = "listTransfer"

        var balanceOld: Long = 0
        var balanceNew: Long = 0
        var bindingWalletOld = ""
        var bindingWalletNew = ""
        var bindingCateOld = ""
        var bindingCateNew = ""
        //Edit in Transaction
        MainActivity.arrayListTransactionMain.forEach {
            if(type == "listTransaction"){
                if(it.id == list.id){
                    balanceOld = it.amount
                    balanceNew = binding.editTransactionAmount.text.toString().toLong()
                    bindingWalletOld = it.wallet
                    bindingWalletNew = getWalletID(binding.walletAutoComplete.text.toString())
                    bindingCateOld = it.cate
                    bindingCateNew = getCategoryID(binding.categoryAutoComplete.text.toString())
                    Log.d("EditTra", "$type $balanceOld $balanceNew $bindingWalletOld $bindingWalletNew")
//                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                }
            }
            else if(type == "listIncome"){
                if(it.id == list.id){
                    balanceOld = it.amount
                    balanceNew = binding.editTransactionAmount.text.toString().toLong()
                    bindingWalletOld = it.wallet
                    bindingWalletNew = getWalletID(binding.walletAutoComplete.text.toString())
//                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                }
            }
            else if(type == "listTransfer"){
                if(it.id == list.id){
                    balanceOld = it.amount
                    balanceNew = binding.editTransactionAmount.text.toString().toLong()
                    bindingWalletOld = it.wallet
                    bindingWalletNew = getWalletID(binding.walletAutoComplete.text.toString())
                    bindingCateOld = it.cate
                    bindingCateNew = getWalletID(binding.categoryAutoComplete.text.toString())
                    Log.d("EditTra", "$type $balanceOld $balanceNew $bindingWalletOld $bindingWalletNew")
//                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                }
            }
            Home.listTransaction[position].amount = balanceNew
            Home.syncTransactionDatabase(list)
        }
        //Edit in Wallet

        if(type == "listTransfer") {
                adjustTransfer(bindingWalletOld, bindingWalletNew, bindingCateOld, bindingCateNew, balanceOld, balanceNew)
        }
        else{
            adjustWallet(bindingWalletOld, bindingWalletNew, balanceOld, balanceNew)
            if(type == "listTransaction") adjustCategory(bindingCateOld, bindingCateNew, balanceOld, balanceNew)
        }
        dismiss()

    }

    private fun adjustTransfer(bindingWalletFOld: String, bindingWalletFNew: String, bindingWalletTOld: String, bindingWalletTNew: String, balanceOld: Long, balanceNew: Long) {
        var amountWallet: Long = 0

            Home.listWallet.forEachIndexed{ index, it ->
                if(it.id == bindingWalletFOld){
                    amountWallet = it.saldo + balanceOld
                    Home.listWallet[index].saldo = amountWallet
                    Home.syncWalletDatabase(it)
                    Log.d("editwalletFOLD", "${it.nameWallet} saldoAsal: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                }


                if(it.id == bindingWalletTOld){
                        amountWallet = it.saldo - balanceOld
                    Log.d("editwalletTOLD", "${it.nameWallet} saldoAsal: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletTOld).child("saldo").setValue(amountWallet)
                    Home.listWallet[index].saldo = amountWallet
                    Home.syncWalletDatabase(it)
                }
            }

            Home.listWallet.forEachIndexed { index, it ->
                if(it.id == bindingWalletFNew){
                    amountWallet = it.saldo - balanceNew
                    Log.d("editwalletFNew", "${it.nameWallet} saldoAsal: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                    Home.listWallet[index].saldo = amountWallet
                    Home.listTransaction[position].imageLinkWallet = it.imageLink
                    Home.listTransaction[position].wallet = bindingWalletFNew
                    Home.syncWalletDatabase(it)
                }
                if(it.id == bindingWalletTNew){
                    amountWallet = it.saldo + balanceNew
                    Log.d("editwalletTNew", "${it.nameWallet} saldoAsal: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                    Home.listWallet[index].saldo = amountWallet
                    Home.listTransaction[position].imageLinkCategory = it.imageLink
                    Home.listTransaction[position].cate = bindingWalletTNew
                    Home.syncWalletDatabase(it)
                }
            }
            //sync data to database
            Home.listTransaction[position].amount = balanceNew
        Home.syncTransactionDatabase(list)

    }


    private fun adjustCategory(idOld: String, idNew:String,  balanceOld:Long, balanceNew: Long) {
        var amountCate: Long = 0

        if(idOld != idNew){
            Home.listCategory.forEachIndexed { index, it ->
                if(it.id == idNew){
                    amountCate = it.expense + balanceNew
//                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    Home.listCategory[index].expense = amountCate
//                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("imgLinkCategory").setValue(it.imgLink)
                    Home.listTransaction[position].imageLinkCategory = it.imgLink
//                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("cate").setValue(idNew)
                    Home.listTransaction[position].cate = idNew
                    Home.syncCategoryDatabase(it)
                    //Ganti Notes jika notes sebelumny adalah default
                    if(binding.editTransactionNotes.text.toString() == list.notes){
//                        databaseReference.child("transaksi").child("listTransaction").child(list.id).child("notes").setValue(it.nameCategory)
                        Home.listTransaction[position].notes = it.nameCategory
                    }
                }
                else if (it.id == idOld){
                    amountCate = it.expense - balanceOld
//                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    Home.listCategory[index].expense = amountCate
                    Home.syncCategoryDatabase(it)
                }
            }

        }
        else{
            Home.listCategory.forEachIndexed { index, it ->
                if(it.id == idOld){
                    amountCate = it.expense - balanceOld + balanceNew
//                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    Home.listCategory[index].expense = amountCate
                    Home.syncCategoryDatabase(it)
                }
            }
        }
        Home.syncTransactionDatabase(list)

    }

    private fun adjustWallet(idOld:String, idNew:String, balanceOld: Long , balanceNew: Long){
        var amountWallet: Long = 0
        if(idOld != idNew){
            Home.listWallet.forEachIndexed { index, it ->
                if(it.id == idNew){
                    if(list.type == "income") amountWallet = it.saldo + balanceNew
                    else amountWallet = it.saldo - balanceNew
//                    databaseReference.child("wallet").child("listWallet").child(idNew).child("saldo").setValue(amountWallet)
                    Home.listWallet[index].saldo = amountWallet
//                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("imgLinkWallet").setValue(it.imageLink)
                    Home.listTransaction[position].imageLinkWallet = it.imageLink
//                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("wallet").setValue(idNew)
                    Home.listTransaction[position].wallet = idNew

                    Home.syncWalletDatabase(it)
                }
                else if (it.id == idOld){
                    if(list.type == "income") amountWallet = it.saldo - balanceOld
                    else amountWallet = it.saldo + balanceOld
//                    databaseReference.child("wallet").child("listWallet").child(idOld).child("saldo").setValue(amountWallet)
                    Home.listWallet[index].saldo = amountWallet
                    Home.syncWalletDatabase(it)
                }

            }
        }
        else{
            Home.listWallet.forEachIndexed { index, it ->
                if(it.id == idOld){
                    if(list.type == "income") amountWallet = it.saldo - balanceOld + balanceNew
                    else amountWallet = it.saldo + balanceOld - balanceNew
//                    databaseReference.child("wallet").child("listWallet").child(idOld).child("saldo").setValue(amountWallet)
                    Home.listWallet[index].saldo = amountWallet
                    Home.syncWalletDatabase(it)
                }
            }
        }
        Home.syncTransactionDatabase(list)
    }
}