package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.databinding.ItemProdBinding
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.DBHelper

class ProdAdapter(var context: Context, private var list:ArrayList<Products>):Adapter<ProdAdapter.ProdHolder>() {
    inner class ProdHolder(var binding: ItemProdBinding):ViewHolder(binding.root)
    private lateinit var dbHelper: DBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdHolder {
        return ProdHolder(ItemProdBinding.inflate(LayoutInflater.from(context),parent,false))
    }
    override fun getItemCount(): Int =list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProdHolder, position: Int) {
        dbHelper = DBHelper(context)
        val products = list[position]
        holder.binding.tvProduct.text = products.name
        holder.binding.tvPrice.text = "${products.price} ${dbHelper.getShopDevise()}"
        holder.binding.tvQuantity.text = "${products.qty}"
        holder.binding.tvTotal.text = "${products.total} ${dbHelper.getShopDevise()}"
    }



}