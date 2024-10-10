package com.abahz.abahzbusiness.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.databinding.ItemExpenseBinding
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.utils.DBHelper

class ExpenseAdapter(private val context: Context, private val list: List<Notes>):Adapter<ExpenseAdapter.MyHolder>() {
   private lateinit var dbHelper: DBHelper


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemExpenseBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount()=list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        dbHelper = DBHelper(context)
        val journal = list[position]
        holder.binding.tvName.text = journal.client
        holder.binding.tvPrice.text = journal.price.toString() + " "+dbHelper.getShopDevise()
        holder.binding.tvNote.text = journal.note
    }

    inner class MyHolder(var binding: ItemExpenseBinding):ViewHolder(binding.root)
}