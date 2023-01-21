package Home

import android.os.Parcel
import android.os.Parcelable

class TransactionDialog(
    val id: String = "",
    val type: String = "",
    val amount: Long = 0,
    val date: Long = 0,
    val wallet: String = "",
    val cate: String = "",
    val notes: String = "",
    val imageLinkWallet: String = "",
    val imageLinkCategory: String = "") {
}
