package com.example.moneymanagementproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding


class transaction : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    //create object class transactionviewmodel
    private lateinit var viewModel: TransactionViewModel

    //ambil kelas adapter
    private var adapter : transactionAdapter? = null

    //Arraylist transaksi
    private var arrayListTransaction: ArrayList<SaveData> = ArrayList()

    lateinit var listView: ListView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arrayListTransaction.add(SaveData("1","50000","10/10/2020", "Dompet", "Makan", "Meongg"))
        arrayListTransaction.add(SaveData("1","50000","10/10/2020", "Dompet", "Makan", "Meongg"))
        arrayListTransaction.add(SaveData("1","50000","10/10/2020", "Dompet", "Makan", "Meongg"))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)


        viewModel = ViewModelProvider(this)[TransactionViewModel::class.java]

        adapter = context?.let { transactionAdapter(it, arrayListTransaction) }
        binding.ListViewTransaction.adapter = adapter

        return binding.root
    }

    public fun addListTransaction(data: SaveData){
        arrayListTransaction.add(data)
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//
//
//        viewModel.savedata.observe(viewLifecycleOwner) {
//            if (it != null) {
//                adapter.addSaveData(it)
//            }
//        }
//
//        viewModel.getRealtimeUpdate()
//
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        _binding = null
//    }


}