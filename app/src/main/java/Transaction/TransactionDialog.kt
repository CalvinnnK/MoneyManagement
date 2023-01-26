package Transaction

import android.os.Parcel
import android.os.Parcelable

class TransactionDialog(
    var id: String = "",
    var type: String = "",
    var amount: Long = 0,
    var date: Long = 0,
    var wallet: String = "",
    var cate: String = "",
    var notes: String = "",
    var imageLinkWallet: String = "",
    var imageLinkCategory: String = "") {
}
