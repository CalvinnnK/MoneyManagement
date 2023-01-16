package Statistic.Wallet

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

            addPostEventListener(databaseReference)
        }

        binding.statWalletRight.setOnClickListener{
            calendar.add(Calendar.MONTH, 1)
            dateInput = dateFormat.format(calendar.time)
            binding.statWalletDate.text = dateInput
            listStatWallet.clear()

            addPostEventListener(databaseReference)
        }


        addPostEventListener(databaseReference)
        checkDataIsChanged()

        binding.statWalletListView.adapter = adapter


        // Inflate the layout for this fragment
        return binding.root
    }

    private fun addPostEventListener(postReference: DatabaseReference) {
        var name = ""
        var imgLink = ""
        var income: Long = 0
        var expense: Long = 0


        // [START post_value_event_listener]
        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var datefrom: Long = dateFormat.parse(dateInput).time
                calendar.add(Calendar.MONTH, 1)
                var dateto: Long = dateFormat.parse(dateFormat.format(calendar.time)).time
                calendar.add(Calendar.MONTH, -1)
                Log.d("StatWallet", "" + datefrom + " " + dateto)


                // Get Post object and use the values to update the UI
                for(snap : DataSnapshot in dataSnapshot.child("wallet").child("listWallet").children){
                    name = ""
                    income = 0
                    expense = 0

                    name =  snap.child("nameWallet").value.toString()
                    imgLink = snap.child("imageLink").value.toString()

                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransaction").children){
                        if(snap1.child("wallet").value.toString() == snap.child("nameWallet").value.toString()
                            && snap1.child("date").value.toString().toLong() > datefrom
                            && snap1.child("date").value.toString().toLong() < dateto){
                            expense += snap1.child("amount").value.toString().toLong()
//                            Log.d("AAA loop transaksi","" + snap1.child("notes").value.toString())
                        }
                    }

                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listIncome").children){
                        if(snap1.child("wallet").value.toString() == snap.child("nameWallet").value.toString()
                            && snap1.child("date").value.toString().toLong() > datefrom
                            && snap1.child("date").value.toString().toLong() < dateto){
                            income += snap1.child("amount").value.toString().toLong()
//                            Log.d("AAA loop income","" + snap1.child("notes").value.toString())
                        }
                    }
                    for(snap1 : DataSnapshot in dataSnapshot.child("transaksi").child("listTransfer").children){
                        if(snap1.child("walletFrom").value.toString() == snap.child("nameWallet").value.toString()
                            && snap1.child("date").value.toString().toLong() > datefrom
                            && snap1.child("date").value.toString().toLong() < dateto){
                            expense += snap1.child("amount").value.toString().toLong()
//                            Log.d("AAA loop transfer","" + snap1.child("notes").value.toString())
                        }
                        else if(snap1.child("walletTo").value.toString() == snap.child("nameWallet").value.toString()
                            && snap1.child("date").value.toString().toLong() > datefrom
                            && snap1.child("date").value.toString().toLong() < dateto){
                            income += snap1.child("amount").value.toString().toLong()
//                            Log.d("AAA loop transfer","" + snap1.child("notes").value.toString())
                        }
                    }
                    addStatWallet(StatWallet(name,income-expense,income, expense, imgLink))
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(ContentValues.TAG, "loadPost:onCancelled", databaseError.toException())
            }
        }

        postReference.addValueEventListener(postListener)
        // [END post_value_event_listener]
    }
    fun addStatWallet(data: StatWallet){
        this.listStatWallet.add(data)
        checkDataIsChanged()
    }

    fun checkDataIsChanged(){
        adapter?.notifyDataSetChanged()
    }


}