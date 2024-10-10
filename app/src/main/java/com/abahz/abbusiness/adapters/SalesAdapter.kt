package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.databinding.ItemRapportBinding
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.utils.DBHelper

class SalesAdapter(private val context: Context, private val list: List<Orders>):Adapter<SalesAdapter.MyHolder>() {
    private lateinit var binding: ItemRapportBinding
    private lateinit var dbHelper: DBHelper


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        binding = ItemRapportBinding.inflate(LayoutInflater.from(context),parent,false)
        return MyHolder(binding.root)
    }

    override fun getItemCount()=list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        dbHelper = DBHelper(context)
        val orders = list[position]
        val priceUn = orders.total.toInt()/orders.qty.toInt()
        for (prod in dbHelper.getProductById(orders.name)){
            holder.name.text = prod.name
        }
        holder.price.text = priceUn.toString() + " "+dbHelper.getShopDevise()
        holder.qty.text = orders.qty
        holder.total.text = orders.total + " "+dbHelper.getShopDevise()

    }

    inner class MyHolder(itemView: View):ViewHolder(itemView){
        val name = binding.tvProduct
        val price = binding.tvPrice
        val qty = binding.tvQuantity
        val total = binding.tvTotal
    }
}