package Home

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.example.moneymanagementproject.R
import com.example.moneymanagementproject.databinding.FragmentEditTransactionDialogBinding
import com.example.moneymanagementproject.databinding.FragmentViewTransactionDialogBinding
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class EditTransactionDialog(private var list: TransactionDialog) : DialogFragment() {
    private var _binding : FragmentEditTransactionDialogBinding? = null
    private val binding get() = _binding!!
    private val databaseReference = Firebase.database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditTransactionDialogBinding.inflate(inflater, container, false)

        Glide.with(requireContext()).load(list.imageLinkCategory).into(binding.editTransactionImg)
        binding.editTransactionNotes.text = Editable.Factory.getInstance().newEditable(list.notes)
        binding.editTransactionAmount.text = Editable.Factory.getInstance().newEditable(NumberFormat.getInstance(Locale.US).format(list.amount))
        binding.editTransactionNotes.text = Editable.Factory.getInstance().newEditable(getDate(list.date))
//        binding.editTransactionWallet.

        // Inflate the layout for this fragment
        return binding.root
    }

    fun getDate(date: Long): String? {
        val format = SimpleDateFormat("d MMMM yyyy")
        return format.format(date)
    }

}