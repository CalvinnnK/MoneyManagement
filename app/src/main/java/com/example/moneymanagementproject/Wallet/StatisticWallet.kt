package com.example.moneymanagementproject.Wallet

import com.example.moneymanagementproject.Transaction.Transaction
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.moneymanagementproject.databinding.FragmentStatisticWalletBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class StatisticWallet : Fragment() {
    private var _binding: FragmentStatisticWalletBinding? = null
    private val binding get() = _binding!!

    private var listStatWallet: ArrayList<StatWallet> = ArrayList()

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
            reduceMonth()
            dateInput = dateFormat.format(calendar.time)
            binding.statWalletDate.text = dateInput
            listStatWallet.clear()


            calculateStatWallet()
        }

        binding.statWalletRight.setOnClickListener{
            addMonth()
            dateInput = dateFormat.format(calendar.time)
            binding.statWalletDate.text = dateInput
            listStatWallet.clear()


            calculateStatWallet()
        }


        calculateStatWallet()
        checkDataIsChanged()
        binding.statWalletListView.adapter = adapter


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun reduceMonth(){
        calendar.add(Calendar.MONTH, -1)
    }

    private fun addMonth(){
        calendar.add(Calendar.MONTH, 1)
    }


    private fun calculateStatWallet(){
        this.listStatWallet.clear()

        var datefrom: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        addMonth()
        var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        reduceMonth()

        var income: Long = 0
        var expense: Long = 0

        com.example.moneymanagementproject.Home.Home.listWallet.forEach{ w ->
            if(w.nameWallet == "Add Wallet"){
                return@forEach
            }

            income = 0
            expense = 0

            Transaction.arrayListTransaction.forEach { t ->
                //condition to check transaction happened in desired month
                if(t.date >= datefrom && t.date < dateto && ( t.wallet == w.id || t.cate == w.id)){
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

                    }
                }
            }

            addStatWallet(StatWallet(w.nameWallet,income-expense,income, expense, w.imageLink))

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