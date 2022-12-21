package com.example.moneymanagementproject

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.moneymanagementproject.databinding.FragmentAddTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentHomeBinding
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding


class Home : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var adapter : WalletAdapter? = null
    private var listWallet: ArrayList<Wallet> = ArrayList<Wallet>()

    private lateinit var mainViewModel: MainViewModel


//        (activity as MainActivity?)!!.getWallet()
//    var adapter = ArrayAdapter(requireContext(),R.layout.item_wallet,listWallet)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)





    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)


//        listWallet.add(Wallet("Bank",0))
//        listWallet.add(Wallet("Go Pay",0))
//        listWallet.add(Wallet("OVO",0))
//        listWallet.add(Wallet("Shopee Pay",0))
//        listWallet.add(Wallet("Dana", 0))


        adapter = WalletAdapter(context,listWallet)
        binding.walletGrid.adapter = adapter
        adapter?.notifyDataSetChanged()

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        mainViewModel.arrayListData.observe(viewLifecycleOwner) {
            //Perform desired operation with the array list
            it.forEach(){
                Log.d("forEach ","" + it.nameWallet)
                listWallet.add(Wallet(it.nameWallet, it.saldo))
            }
        }

//        Log.d("Testing Home", "Done added " + listWallet[1].nameWallet )
        Log.d("Testing Home", "Done added " + listWallet.count())


        return binding.root
    }

    fun checkDataIsCahnged(){
        adapter?.notifyDataSetChanged()
    }

    fun getWalletArrayList(){

    }

}

