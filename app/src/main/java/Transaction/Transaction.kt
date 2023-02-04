package Transaction

import Home.Home
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.moneymanagementproject.databinding.FragmentTransactionBinding
import kotlin.collections.ArrayList


class Transaction : Fragment() {

    private var _binding: FragmentTransactionBinding? = null
    private val binding get() = _binding!!

    //ambil kelas adapter
    private var adapter : TransactionAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = TransactionAdapter(context, arrayListTransaction)

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

        sortArray()
        checkDataIsChanged()
        return binding.root
    }

    companion object{
        var arrayListTransaction: ArrayList<TransactionData> = ArrayList()
        fun addTransaction(data: TransactionData){
            arrayListTransaction.add(data)
        }
    }

    private fun popUpEditDialog(i: Int) {
        val popupWindow = ViewTransactionDialog(Home.listTransaction[i], i)
        popupWindow.show((activity as AppCompatActivity).supportFragmentManager, "Pop Up View Wallet")
        checkDataIsChanged()
    }

    private fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }

    private fun sortArray(){
        arrayListTransaction.sortByDescending { it.date }
    }



}