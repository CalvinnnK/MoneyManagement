package Transaction

import Home.TransactionDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = Transaction_Adapter(context, arrayListTransaction)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)

        binding.ListViewTransaction.adapter = adapter

        Log.d("transaction","" + arrayListTransaction.size)
        getTransacrion()
        checkDataIsChanged()
        sortArray()
        return binding.root
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
//            arrayListTransaction.clear()
            arrayListTransaction.add(data)
        }
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }

    fun sortArray(){
        arrayListTransaction.sortByDescending { it.date }
    }


}