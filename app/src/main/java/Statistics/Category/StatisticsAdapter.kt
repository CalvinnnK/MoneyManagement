package Statistics.Category

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class StatisticsAdapter(private val context: Context?, private val arrayList: ArrayList<Category>) : BaseAdapter()  {

    private lateinit var name: TextView
    private lateinit var percent: TextView
    private lateinit var balance: TextView
    private lateinit var img: ImageView

    private var total: Double = 0.0
    var p:Double = 0.0

    private val databaseReference = Firebase.database.reference

    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertview: View?, parent: ViewGroup?): View {
        var convertView = convertview
        convertView = LayoutInflater.from(context).inflate(R.layout.item_stat_category, parent, false)
        total = 0.0

        name = convertView.findViewById(R.id.item_stat_nameCategory)
        percent = convertView.findViewById(R.id.item_stat_percentage)
        balance = convertView.findViewById(R.id.item_stat_expense)
        img = convertView.findViewById(R.id.item_stat_image)


        arrayList.forEach {
            total += it.expense
        }

        p = arrayList[position].expense * 100 / total
        if( p.isNaN()) p = 0.0
        // Pembulatan 2 desimal untuk percentage
        val solution:Double = String.format("%.2f", p).toDouble()

        name.text = arrayList[position].nameCategory
        percent.text = solution.toString() + "%"
        balance.text = "Rp " + arrayList[position].expense.toString()
        Glide.with(context!!).load(arrayList[position].imgLink).into(img)
        Log.d("StatAdapter", "" + arrayList[position].expense)

        return convertView
    }

}
