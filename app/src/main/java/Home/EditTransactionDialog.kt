package Home

import android.app.DatePickerDialog
import android.content.ContentValues
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditTransactionDialog(private var list: TransactionDialog) : DialogFragment() {
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
        Home.listWallet.forEachIndexed{index, w ->
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
                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                    return@forEach
                }
            }
            else if(type == "listIncome"){
                if(it.id == list.id){
                    balanceOld = it.amount
                    balanceNew = binding.editTransactionAmount.text.toString().toLong()
                    bindingWalletOld = it.wallet
                    bindingWalletNew = getWalletID(binding.walletAutoComplete.text.toString())
                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                    return@forEach
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
                    databaseReference.child("transaksi").child(type).child(list.id).child("amount").setValue(balanceNew)
                    return@forEach
                }
            }
        }
        //Edit in Wallet

        if(type == "listTransfer") {
            //klo dibalik wallet from dan wallet to
            if(bindingWalletOld == bindingCateNew && bindingWalletNew == bindingCateOld){
                Home.listWallet.forEach {
                    if(it.id == bindingCateNew){
                        var amountWallet: Long = 0
                        if(balanceOld == balanceNew){
                            amountWallet = it.saldo + balanceNew * 2
                        }
                        else{

                            amountWallet = it.saldo + balanceOld + balanceNew
                            Log.d("editttCate", "saldoTujuan: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                        }
                        databaseReference.child("wallet").child("listWallet").child(bindingCateNew).child("saldo").setValue(amountWallet)
                        databaseReference.child("transaksi").child("listTransfer").child(list.id).child("imgLinkWalletTo").setValue(it.imageLink)
                        databaseReference.child("transaksi").child("listTransfer").child(list.id).child("walletTo").setValue(it.id)
                    }
                    else if(it.id == bindingWalletNew){
                        var amountWallet: Long = 0
                        if(balanceOld == balanceNew){
                            amountWallet = it.saldo - balanceNew * 2
                        }
                        else{
                            amountWallet = it.saldo - balanceOld - balanceNew
                            Log.d("editttwallet", "saldoAsal: ${it.saldo} old: $balanceOld new: $balanceNew amountwallet: $amountWallet")
                        }
                        databaseReference.child("wallet").child("listWallet").child(bindingWalletNew).child("saldo").setValue(amountWallet)
                        databaseReference.child("transaksi").child("listTransfer").child(list.id).child("imgLinkWalletFrom").setValue(it.imageLink)
                        databaseReference.child("transaksi").child("listTransfer").child(list.id).child("walletFrom").setValue(it.id)
                    }
                }
            }
            else adjustTransfer(bindingWalletOld, bindingWalletNew, bindingCateOld, bindingCateNew, balanceOld, balanceNew)
        }
        else{
            adjustWallet(bindingWalletOld, bindingWalletNew, balanceOld, balanceNew)
            if(type == "listTransaction") adjustCategory(bindingCateOld, bindingCateNew, balanceOld, balanceNew)
        }


        dismiss()

    }

    private fun adjustTransfer(bindingWalletFOld: String, bindingWalletFNew: String, bindingWalletTOld: String, bindingWalletTNew: String, balanceOld: Long, balanceNew: Long) {
        var amountWallet: Long = 0
        if(bindingWalletFOld == bindingWalletFNew && bindingWalletTOld == bindingWalletTNew){
            Home.listWallet.forEach {
                if(it.id == bindingWalletFOld){
                    amountWallet = it.saldo + balanceOld - balanceNew
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletFOld).child("saldo").setValue(amountWallet)
                }
                else if(it.id == bindingWalletTNew){
                    amountWallet = it.saldo + balanceNew - balanceOld
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletTNew).child("saldo").setValue(amountWallet)
                }
            }
        }
        else {
            Home.listWallet.forEach {
                if(it.id == bindingWalletFOld){
                    if(bindingWalletFOld != bindingWalletFNew) {
                        amountWallet = it.saldo + balanceOld
//                        var
//                        databaseReference.child("wallet").child("listWallet").child(binding.walletAutoComplete.text.toString()).child("saldo").setValue(getamountWallet)
                    }
                    else amountWallet = it.saldo + balanceOld - balanceNew
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletFOld).child("saldo").setValue(amountWallet)
                }
                else if(it.id == bindingWalletFNew){
                    if(bindingWalletFOld != bindingWalletFNew) amountWallet = it.saldo - balanceNew
                    else amountWallet = it.saldo + balanceOld - balanceNew
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletFNew).child("saldo").setValue(amountWallet)
                    databaseReference.child("transaksi").child("listTransfer").child(list.id).child("imgLinkWalletFrom").setValue(it.imageLink)
                    databaseReference.child("transaksi").child("listTransfer").child(list.id).child("walletFrom").setValue(bindingWalletFNew)
                }

                if(it.id == bindingWalletTOld){
                    if(bindingWalletTOld != bindingWalletTNew) amountWallet = it.saldo - balanceOld
                    else amountWallet = it.saldo - balanceOld + balanceNew
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletTOld).child("saldo").setValue(amountWallet)
                }
                else if(it.id == bindingWalletTNew){
                    if(bindingWalletTOld != bindingWalletTNew) amountWallet = it.saldo + balanceNew
                    else amountWallet = it.saldo - balanceOld + balanceNew
                    databaseReference.child("wallet").child("listWallet").child(bindingWalletTNew).child("saldo").setValue(amountWallet)
                    databaseReference.child("transaksi").child("listTransfer").child(list.id).child("imgLinkWalletTo").setValue(it.imageLink)
                    databaseReference.child("transaksi").child("listTransfer").child(list.id).child("walletTo").setValue(bindingWalletTNew)

                }



            }
        }
    }


    private fun adjustCategory(idOld: String, idNew:String,  balanceOld:Long, balanceNew: Long) {
        var amountCate: Long = 0

        if(idOld != idNew){
            Home.listCategory.forEach {
                if(it.id == idNew){
                    amountCate = it.expense + balanceNew
                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("imgLinkCategory").setValue(it.imgLink)
                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("cate").setValue(idNew)
                    //Ganti Notes jika notes sebelumny adalah default
                    if(binding.editTransactionNotes.text.toString() == list.notes){
                        databaseReference.child("transaksi").child("listTransaction").child(list.id).child("notes").setValue(it.nameCategory)
                    }
                    return@forEach
                }
                else if (it.id == idOld){
                    amountCate = it.expense - balanceOld
                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    return@forEach
                }
            }



        }
        else{
            Home.listCategory.forEach {
                if(it.id == idOld){
                    amountCate = it.expense - balanceOld + balanceNew
                    databaseReference.child("category").child("listCategory").child(idOld).child("expense").setValue(amountCate)
                    return@forEach
                }
            }
        }

    }

    private fun adjustWallet(idOld:String, idNew:String, balanceOld: Long , balanceNew: Long){
        var amountWallet: Long = 0
        if(idOld != idNew){
            Home.listWallet.forEach {
                if(it.id == idNew){
                    if(list.type == "income") amountWallet = it.saldo + balanceNew
                    else amountWallet = it.saldo - balanceNew
                    databaseReference.child("wallet").child("listWallet").child(idNew).child("saldo").setValue(amountWallet)
                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("imgLinkWallet").setValue(it.imageLink)
                    databaseReference.child("transaksi").child("listTransaction").child(list.id).child("wallet").setValue(idNew)

                }
                else if (it.id == idOld){
                    if(list.type == "income") amountWallet = it.saldo - balanceOld
                    else amountWallet = it.saldo + balanceOld
                    databaseReference.child("wallet").child("listWallet").child(idOld).child("saldo").setValue(amountWallet)
                }
            }
        }
        else{
            Home.listWallet.forEach {
                if(it.id == idOld){
                    if(list.type == "income") amountWallet = it.saldo - balanceOld + balanceNew
                    else amountWallet = it.saldo + balanceOld - balanceNew
                    databaseReference.child("wallet").child("listWallet").child(idOld).child("saldo").setValue(amountWallet)
                    return@forEach
                }
            }
        }

    }
}