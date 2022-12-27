package com.example.moneymanagementproject

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter

class ViewPagerAdapter(fm: FragmentManager ) : FragmentPagerAdapter(fm)  {
    override fun getCount(): Int {
        return 3
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> AddTransactionIncome()
            1 -> AddTransactionExpense()
            2 -> AddTransactionTransfer()
            else -> AddTransactionExpense()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "Income"
            1 -> return "Expense"
            2 -> return "Transfer"
        }
        return super.getPageTitle(position)
    }

}