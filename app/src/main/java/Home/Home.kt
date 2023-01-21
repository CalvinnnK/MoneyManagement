package Home

import Statistics.Category.Category
import Transaction.Transaction
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.MainActivity
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.*

class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapterW : WalletAdapter? = null
    private var adapterT : TransactionDialogAdapter? = null

    private var TotalBalance: Long = 0


    private var databaseReference: DatabaseReference = Firebase.database.reference

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
        adapterW = WalletAdapter(context,listWallet)
        binding.walletGrid.adapter = adapterW

        adapterW?.notifyDataSetChanged()
        binding.walletGrid.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            if(i == listWallet.size - 1){
                val popupWindow = AddWalletDialog()
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Wallet" )
            }
            else{
//                val popupWindow = EditWalletDialog()
//                popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
            }
        }


        binding.listViewTransactionHome.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            popUpEditDialog(i)
        }

        addPostEventListenerWallet(databaseReference)
        addPostEventListenerTransaction(databaseReference.child("transaksi"))


        //Adapter Transaction
        adapterT = TransactionDialogAdapter(context,listTransaction,5)
        binding.listViewTransactionHome.adapter = adapterT
        binding.listViewTransactionHome.isNestedScrollingEnabled = true


        checkDataIsChanged()

        return binding.root
    }


    companion object{
        var listWallet: ArrayList<Wallet> = ArrayList<Wallet>()
        var listCategory: ArrayList<Category> = ArrayList<Category>()
        var listTransaction: ArrayList<TransactionDialog> = ArrayList<TransactionDialog>()
    }

    private fun calculateTotalWallet() {
        TotalBalance = 0
        listWallet.forEach {
            TotalBalance += it.saldo
            Log.d("HOME", "" + it.nameWallet + it.saldo)
        }
        binding.totalAmount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(TotalBalance)
    }

    fun checkDataIsChanged(){
        adapterW?.notifyDataSetChanged()
        adapterT?.notifyDataSetChanged()
    }



    private fun addPostEventListenerWallet(postReference: DatabaseReference) {
        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listWallet.clear()
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    val post = snap.getValue<Wallet>()!!
                    addWallet(post)
                }
                Home.listWallet.add(Wallet("none","Add Wallet",0))
                calculateTotalWallet()

                listCategory.clear()
                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
                    val post = snap.getValue<Category>()!!
                    addCategory(post)
                }

            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
    }

    private fun addPostEventListenerTransaction(postReference: DatabaseReference) {
        var amount: Long = 0
        var date: Long = 0
        var wallet = ""
        var cate = ""
        var notes = ""
        var imgWallet = ""
        var imgCate = ""

        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // clean the array to avoid duplicates and also reset balance too
                listTransaction.clear()

                // Income
                for(snap : DataSnapshot in dataSnapshot.child("listIncome").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = "Income"
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog(snap.key!!, "income", amount, date, wallet, cate, notes, imgWallet, imgCate))
                    Log.d("KeyTest", "" + snap.key)
                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransaction").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("wallet").value.toString()
                    cate = snap.child("cate").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWallet").value.toString()
                    imgCate = snap.child("imgLinkCategory").value.toString()

                    addTransaction(TransactionDialog(snap.key!!, "expense", amount, date, wallet, cate, notes, imgWallet, imgCate))

                }

                for(snap : DataSnapshot in dataSnapshot.child("listTransfer").children){
                    amount = snap.child("amount").value.toString().toLong()
                    date = snap.child("date").value.toString().toLong()
                    wallet = snap.child("walletFrom").value.toString()
                    cate = snap.child("walletTo").value.toString()
                    notes = snap.child("notes").value.toString()
                    imgWallet = snap.child("imgLinkWalletFrom").value.toString()
                    imgCate = snap.child("imgLinkWalletTo").value.toString()

                    addTransaction(TransactionDialog(snap.key!!, "transfer", amount, date, wallet, cate, notes, imgWallet, imgCate))

                    sortArray()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }

        }
        postReference.addValueEventListener(postListener)
    }


    fun addTransaction(data: TransactionDialog){
        listTransaction.add(data)
        checkDataIsChanged()
    }

    fun addWallet(data: Wallet){
        Home.listWallet.add(data)
    }
    fun addCategory(data: Category){
        Home.listCategory.add(data)
    }


    fun sortArray(){
        listTransaction.sortByDescending { it.date }
        while(listTransaction.size > 5){
            listTransaction.removeAt(listTransaction.size-1)
        }
    }

    private fun popUpEditDialog(i : Int) {
        val popupWindow = ViewTransactionDialog(listTransaction[i])
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
    }

}
