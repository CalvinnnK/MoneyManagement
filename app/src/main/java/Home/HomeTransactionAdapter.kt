package Home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import java.text.NumberFormat
import java.util.*

class HomeTransactionAdapter(private val context: Context?, private val arrayList: ArrayList<TransactionDialog>) : BaseAdapter(){

    private lateinit var notes : TextView
    private lateinit var amount : TextView
    private lateinit var wallet : ImageView
    private lateinit var cate : ImageView

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
        convertView = LayoutInflater.from(context).inflate(R.layout.recycler_view_transaction, parent, false)
        notes = convertView.findViewById(R.id.trans_text)
        amount = convertView.findViewById(R.id.trans_amount)
        wallet = convertView.findViewById(R.id.trans_wallet)
        cate = convertView.findViewById(R.id.trans_ic)

        notes.text = arrayList[position].notes
        amount.text = "Rp " + NumberFormat.getInstance(Locale.US).format(arrayList[position].amount)

        Glide.with(context!!).load(arrayList[position].imageLinkCategory).into(cate)
        Glide.with(context!!).load(arrayList[position].imageLinkWallet).into(wallet)

        return convertView
    }


}