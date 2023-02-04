package Statistics.Category

import Transaction.Transaction
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymanagementproject.MainActivity
import com.example.moneymanagementproject.databinding.FragmentStatisticsBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
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
//
//            var datefrom: Long = dateFormat.parse(dateInput).time
//            addMonth()
//            var dateto: Long = dateFormat.parse(dateInput).time
//            reduceMonth()
//            Log.d("Date", "" + datefrom + " " + dateto)


            calculateExpense()
        }


        binding.statCateRight.setOnClickListener{
            addMonth()
            dateInput = dateFormat.format(calendar.time)
            binding.statCateDate.text = dateInput
            listStatCategory.clear()

//            var datefrom: Long = dateFormat.parse(dateInput).time
//            addMonth()
//            var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
//            reduceMonth()
//            Log.d("Date", "" + datefrom + " " + dateto)

            calculateExpense()
        }

        binding.addCategory.setOnClickListener{
            val popupWindow = AddCategory()
            popupWindow.show((activity as AppCompatActivity).supportFragmentManager,"Pop Up Add Category" )
        }

        calculateExpense()
        adapter = StatisticsAdapter(context,listStatCategory)
        binding.statCateListView.adapter = adapter




        checkDataIsChanged()
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addMonth(){
        this.calendar.add(Calendar.MONTH, 1)
    }

    private fun reduceMonth(){
        this.calendar.add(Calendar.MONTH, -1)
    }


    private fun calculateExpense(){
        var datefrom: Long = dateFormat.parse(dateInput).time
        addMonth()
        var dateto: Long = dateFormat.parse(dateInput).time
        reduceMonth()

//        Log.d("Statistics", "isi categry " + Home.Home.listCategory.size)
//        Log.d("Statistics", "isi trans " + Transaction.arrayListTransaction.size)

        var expensePerCategory:Long = 0

        Home.Home.listCategory.forEach { c ->
            expensePerCategory = 0

            MainActivity.arrayListTransactionMain.forEach { t ->
                if(c.id == t.cate && t.date >= datefrom && t.date <= dateto){
                    expensePerCategory += t.amount
                }
//                Log.d("Statistics inner", "" + expensePerCategory + " " + c.id + c.nameCategory)
            }
            addStatCate(Category(c.id, c.nameCategory, expensePerCategory, c.imgLink))
        }
    }

    fun addStatCate(data: Category){
        this.listStatCategory.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}