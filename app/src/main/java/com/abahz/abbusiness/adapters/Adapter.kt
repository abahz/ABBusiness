package com.abahz.abbusiness.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.abahz.abbusiness.fragments.ExpenseFragment
import com.abahz.abbusiness.fragments.HomeFragment
import com.abahz.abbusiness.fragments.OrderFragment
import java.lang.IndexOutOfBoundsException

class Adapter(activity:FragmentActivity):FragmentStateAdapter(activity) {
private val list: MutableList<Fragment> = mutableListOf()
    init {
        list.add(HomeFragment())
        list.add(OrderFragment())
        list.add(ExpenseFragment())
    }
    override fun getItemCount(): Int = list.size

    override fun createFragment(position: Int): Fragment =list[position]

    fun getFragment(position: Int):Fragment{
        return if (position in 0 until itemCount){
            list[position]
        }else{
            throw IndexOutOfBoundsException("Position $position is out of bounds" )
        }
    }

}