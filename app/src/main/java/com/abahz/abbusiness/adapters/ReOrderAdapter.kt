package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.databinding.ItemOrderBinding
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.models.ReOrder
import com.abahz.abbusiness.utils.DBHelper

class ReOrderAdapter(private val context: Context, private val list: List<ReOrder>):Adapter<ReOrderAdapter.MyHolder>() {
    private lateinit var dbHelper: DBHelper


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemOrderBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount()=list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        dbHelper = DBHelper(context)
        val orders = list[position]
        holder.binding.name.text = orders.name
        val prixUnitaire = orders.price/orders.qty
        holder.binding.qty.text = orders.qty.toString()+" x "+prixUnitaire+ " "+dbHelper.getShopDevise()

    }

    inner class MyHolder(var binding: ItemOrderBinding):ViewHolder(binding.root)
}