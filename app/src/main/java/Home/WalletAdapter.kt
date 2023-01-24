package Home

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
import java.text.NumberFormat
import java.util.*

class WalletAdapter(private val context: Context?,  private val arrayList: ArrayList<Wallet>) :
    BaseAdapter() {
    override fun getCount(): Int {
        return arrayList.size
    }

    override fun getItem(p0: Int): Any {
        return p0
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        var listItemViewAddWallet = convertView

        if(position < arrayList.size-1){
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_wallet, parent, false)
            var nameWallet : TextView= listItemView!!.findViewById(R.id.name_wallet)
            var saldoWallet : TextView= listItemView!!.findViewById(R.id.saldo_wallet)
            var imgLink : ImageView = listItemView!!.findViewById(R.id.img_wallet)
            nameWallet.text = arrayList[position].nameWallet
            saldoWallet.text = "Rp " + NumberFormat.getInstance(Locale.US).format(arrayList[position].saldo)
            Glide.with(context!!).load(arrayList[position].imageLink).into(imgLink)
            Log.d("walletAdapterInner1", "" + position + " " + arrayList[position].nameWallet)
        }
        else{
            listItemView = LayoutInflater.from(context).inflate(R.layout.item_addwallet, parent, false)
            var nameWallet : TextView = listItemView!!.findViewById(R.id.addWalletButton)
            nameWallet.text = arrayList[position].nameWallet
            Log.d("walletAdapterInner2", "" + position + " " + arrayList[position].nameWallet)
        }



        return listItemView
    }

}