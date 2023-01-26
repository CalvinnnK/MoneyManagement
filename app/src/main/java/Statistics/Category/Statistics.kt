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
    private val databaseReference: DatabaseReference = Firebase.database.reference

    private var adapter : StatisticsAdapter? = null

    var calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("MMMM yyyy")
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
            calendar.add(Calendar.MONTH, -1)
            dateInput = dateFormat.format(calendar.time)
            binding.statCateDate.text = dateInput
            listStatCategory.clear()

            var datefrom: Long = dateFormat.parse(dateInput).time
            calendar.add(Calendar.MONTH, 1)
            var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
            calendar.add(Calendar.MONTH, -1)
            Log.d("Date", "" + datefrom + " " + dateto)

//            addPostEventListener(databaseReference)
            calculateExpense()
        }


        binding.statCateRight.setOnClickListener{
            calendar.add(Calendar.MONTH, 1)
            dateInput = dateFormat.format(calendar.time)
            binding.statCateDate.text = dateInput
            listStatCategory.clear()

            var datefrom: Long = dateFormat.parse(dateInput).time
            calendar.add(Calendar.MONTH, 1)
            var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
            calendar.add(Calendar.MONTH, -1)
            Log.d("Date", "" + datefrom + " " + dateto)

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

    private fun calculateExpense(){
        var datefrom: Long = dateFormat.parse(dateInput).time
        calendar.add(Calendar.MONTH, 1)
        var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        calendar.add(Calendar.MONTH, -1)

        Log.d("Statistics", "isi categry " + Home.Home.listCategory.size)
        Log.d("Statistics", "isi trans " + Transaction.arrayListTransaction.size)


        var expensePerCategory:Long = 0

        Home.Home.listCategory.forEach { c ->
            expensePerCategory = 0

            MainActivity.arrayListTransactionMain.forEach { t ->
                if(c.id == t.cate && t.date >= datefrom && t.date <= dateto){
                    expensePerCategory += t.amount
                }
                Log.d("Statistics inner", "" + expensePerCategory + " " + c.id + c.nameCategory)
            }
            addStatCate(Category(c.id, c.nameCategory, expensePerCategory, c.imgLink))
        }
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        var id = ""
        var name = ""
        var expensePerCategory: Long = 0
        var imgLink = ""

        var datefrom: Long = dateFormat.parse(dateInput).time
        calendar.add(Calendar.MONTH, 1)
        var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
        calendar.add(Calendar.MONTH, -1)

        Home.Home.listCategory.forEach { c ->
            expensePerCategory = 0

            Transaction.arrayListTransaction.forEach { t ->
                if(c.id == t.id && t.date >= datefrom && t.date <= dateto){
                    expensePerCategory += t.amount
                }
                Log.d("Statistics inner", "" + expensePerCategory + " " + name)
            }
            addStatCate(Category(c.id, c.nameCategory, expensePerCategory, c.imgLink))
        }




//        // [START post_value_event_listener]
//        val postListener = object : ValueEventListener {
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                var datefrom: Long = dateFormat.parse(dateInput).time
//                calendar.add(Calendar.MONTH, 1)
//                var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
//                calendar.add(Calendar.MONTH, -1)
//
//                //Ngitung expense tiap category
//                for(snap : DataSnapshot in dataSnapshot.child("category").child("listCategory").children){
//                    expensePerCategory = 0
//                    id = snap.key.toString()
//                    name = snap.child("nameCategory").value.toString()
//                    imgLink = snap.child("imgLink").value.toString()
//
//                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransaction").children){
//
//                        if(id == snap1.child("cate").value.toString() &&
//                            snap1.child("date").value.toString().toLong() >=  datefrom &&
//                            snap1.child("date").value.toString().toLong() <= dateto){
//                            expensePerCategory += snap1.child("amount").value.toString().toLong()
//                        }
//
//                    }
//
//                    Log.d("Statistics outer", "" + snap.child("expense").value.toString() + " " + name)
//                }
//
//
//            }
//            override fun onCancelled(databaseError: DatabaseError) {
//                // Getting Post failed, log a message
//                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
//            }
//
//
//        }
//        postReference.addValueEventListener(postListener)

        // [END post_value_event_listener]
    }
    fun addStatCate(data: Category){
        this.listStatCategory.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}