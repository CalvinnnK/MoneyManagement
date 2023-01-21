package Statistic.Wallet

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moneymanagementproject.MainActivity
import com.example.moneymanagementproject.databinding.FragmentStatisticWalletBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticWallet : Fragment() {
    private var _binding: FragmentStatisticWalletBinding? = null
    private val binding get() = _binding!!

    private var listStatWallet: ArrayList<StatWallet> = ArrayList()

    private val databaseReference: DatabaseReference = Firebase.database.reference
    private var adapter : StatWalletAdapter? = null

    var calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMMM yyyy")
    private lateinit var dateInput : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        adapter = StatWalletAdapter(context,listStatWallet)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticWalletBinding.inflate(inflater, container, false)

        dateInput =  dateFormat.format(calendar.time)

        binding.statWalletDate.text = dateInput


        binding.statWalletLeft.setOnClickListener{
            calendar.add(Calendar.MONTH, -1)
            dateInput = dateFormat.format(calendar.time)
            binding.statWalletDate.text = dateInput
            listStatWallet.clear()

//            addPostEventListener(databaseReference)
            calculateStatWallet()
        }

        binding.statWalletRight.setOnClickListener{
            calendar.add(Calendar.MONTH, 1)
            dateInput = dateFormat.format(calendar.time)
            binding.statWalletDate.text = dateInput
            listStatWallet.clear()

//            addPostEventListener(databaseReference)
            calculateStatWallet()
        }

//        addPostEventListener(databaseReference)
        calculateStatWallet()
        checkDataIsChanged()
        binding.statWalletListView.adapter = adapter


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun calculateStatWallet(){
        this.listStatWallet.clear()

        var datefrom: Long = dateFormat.parse(dateInput).time
        calendar.add(Calendar.MONTH, 1)
        var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        calendar.add(Calendar.MONTH, -1)
        Log.d("StatWallet", "" + datefrom + " " + dateto)

        var income: Long = 0
        var expense: Long = 0

        Home.Home.listWallet.forEach{  w ->
            if(w.nameWallet == "Add Wallet"){
                return@forEach
            }

            income = 0
            expense = 0

            MainActivity.arrayListTransactionMain.forEach { t ->
                //condition to check transaction happened in desired month
                if(t.date >= datefrom && t.date <= dateto && ( t.wallet == w.id || t.cate == w.id)){
                    //calculate income per month for each wallet
                    if(t.type == "income"){
                        income += t.amount
                    }
                    //calculate expense per month for each wallet
                    else if(t.type == "expense"){
                        expense += t.amount
                    }
                    //calculate transfer per month for each wallet
                    else if(t.type == "transfer"){
                        if(t.wallet == w.id){
                            expense += t.amount
                        }
                        else if(t.cate == w.id){
                            income += t.amount
                        }
                        Log.d("StatW", "${w.nameWallet} ${w.id} wallet: ${t.wallet} cate: ${t.cate} " )
                    }
                }
            }

            addStatWallet(StatWallet(w.nameWallet,income-expense,income, expense, w.imageLink))
            Log.d("StatWallet", "" + w.nameWallet + " " + income + " " + expense  )
        }
    }


    fun addStatWallet(data: StatWallet){
        this.listStatWallet.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}