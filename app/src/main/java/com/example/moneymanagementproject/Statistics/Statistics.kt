package com.example.moneymanagementproject.Statistics

import com.example.moneymanagementproject.Transaction.Transaction
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanagementproject.Category.AddCategory
import com.example.moneymanagementproject.Category.Category
import com.example.moneymanagementproject.Home.AddWalletDialog
import com.example.moneymanagementproject.Home.EditCategoryDialog
import com.example.moneymanagementproject.Home.EditWalletDialog
import com.example.moneymanagementproject.Home.Home
import com.example.moneymanagementproject.databinding.FragmentStatisticsBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class Statistics : Fragment() {
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private var listStatCategory: ArrayList<Category> = ArrayList()

    private var adapter : StatisticsAdapter? = null

    private var calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("MMMM yyyy")
    private lateinit var dateInput : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater,container, false)

        dateInput =  dateFormat.format(calendar.time)

        binding.statCateDate.text = dateInput

        binding.statCateLeft.setOnClickListener{

            reduceMonth()
            dateInput = dateFormat.format(calendar.time)
            binding.statCateDate.text = dateInput
            listStatCategory.clear()
            calculateExpense()
        }


        binding.statCateRight.setOnClickListener{
            addMonth()
            dateInput = dateFormat.format(calendar.time)
            binding.statCateDate.text = dateInput
            listStatCategory.clear()
            calculateExpense()
        }


        binding.statCateListView.onItemClickListener = AdapterView.OnItemClickListener{ _, _, i, _ ->
            if(i == listStatCategory.size - 1){
                val popupWindow = AddCategory()
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Category" )
            }
            else{
                val popupWindow = EditCategoryDialog(listStatCategory[i], i)
                popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up View Category" )
            }
        }


        calculateExpense()

        adapter = StatisticsAdapter(context,listStatCategory)
        binding.statCateListView.adapter = adapter




        checkDataIsChanged()

        return binding.root
    }

    private fun addMonth(){
        this.calendar.add(Calendar.MONTH, 1)
    }

    private fun reduceMonth(){
        this.calendar.add(Calendar.MONTH, -1)
    }


    private fun calculateExpense(){
        var datefrom: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        addMonth()
        var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        reduceMonth()



        var expensePerCategory:Long = 0

        com.example.moneymanagementproject.Home.Home.listCategory.forEach { c ->
            expensePerCategory = 0
            Transaction.arrayListTransaction.forEach { t ->
                if(c.id == t.cate && t.date >= datefrom && t.date < dateto){
                    expensePerCategory += t.amount
                }
                Log.d("Statistic_tanggal", "from: $datefrom to $dateto ${t.date} ${t.cate}} ")
            }
            addStatCate(Category(c.id, c.nameCategory, expensePerCategory, c.imgLink))
        }
        //for add Category button
        addStatCate(Category("add","Add Category", 0, ""))
    }

    fun addStatCate(data: Category){
        this.listStatCategory.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}