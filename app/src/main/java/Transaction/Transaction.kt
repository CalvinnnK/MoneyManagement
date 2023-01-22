package Transaction

import Home.Home
import Home.TransactionDialog
import Home.ViewTransactionDialog
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
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import kotlin.collections.ArrayList


class Transaction : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    //ambil kelas adapter
    private var adapter : Transaction_Adapter? = null
    private var databaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = Transaction_Adapter(context, arrayListTransaction)

//        addPostEventListenerTransaction(databaseReference.child("transaksi"))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        binding.ListViewTransaction.adapter = adapter

        binding.ListViewTransaction.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            popUpEditDialog(i)
        }



//        getTransacrion()
        checkDataIsChanged()
        sortArray()
        return binding.root
    }

    private fun popUpEditDialog(i: Int) {
        val popupWindow = ViewTransactionDialog(Home.listTransaction[i], i)
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
        checkDataIsChanged()
    }

    private fun getTransacrion() {
        arrayListTransaction.clear()
        MainActivity.arrayListTransactionMain.forEach {
            addTransaction(it)
        }
    }

    companion object{
        var arrayListTransaction: ArrayList<TransactionDialog> = ArrayList()
        fun addTransaction(data: TransactionDialog){
            arrayListTransaction.add(data)
        }
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }

    fun sortArray(){
        arrayListTransaction.sortByDescending { it.date }
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
                arrayListTransaction.clear()
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

}