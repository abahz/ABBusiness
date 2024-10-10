package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.databinding.ItemCartBinding
import com.abahz.abbusiness.interfaces.OnQuantityChangeListener
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.utils.DBHelper
import com.bumptech.glide.Glide

class OrderCartAdapter(
    private val context: Context,
    private val list: List<Orders>,
    private val listener: OnQuantityChangeListener
) : Adapter<OrderCartAdapter.MyHolder>() {
    private lateinit var dbHelper: DBHelper


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(ItemCartBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount() = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        dbHelper = DBHelper(context)
        val orders = list[position]
        val punitaire = orders.total.toDouble() / orders.qty.toDouble()
        var QTY = orders.qty.toDouble()
        var ptotal = punitaire * QTY
        holder.binding.cartQty.text = " ${orders.qty}"
        holder.binding.total.text =
            "${orders.qty} x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
        holder.binding.price.text = "Prix Unit.:${punitaire} ${dbHelper.getShopDevise()}"
        holder.binding.name.text = orders.name
        for (prod in dbHelper.getProductById(orders.name)) {
            Glide.with(context).load(prod.image).into(holder.binding.image)
        }
        holder.itemView.setOnLongClickListener {
            Builder(context).setItems(arrayOf("supprimer")) { dial, witch ->
                when (witch) {
                    0 -> {
                        dial.dismiss()
                        dbHelper.deleteOrder(orders.id)
                        notifyItemRemoved(position)
                    }
                }
            }.create().show()
            true
        }

        holder.binding.addition.setOnClickListener {
            if (QTY > 0) {
                QTY++
                ptotal = punitaire * QTY
                holder.binding.cartQty.text = " $QTY"
                holder.binding.total.text =
                    "$QTY x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
                updateTotal(ptotal)
            }
        }
        holder.binding.remove.setOnClickListener {
            if (QTY > 1) {
                QTY--
                ptotal = punitaire * QTY
                holder.binding.cartQty.text = " $QTY"
                holder.binding.total.text =
                    "$QTY x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
                updateTotal(ptotal)
            } else if (QTY == 1.0) {
                dbHelper.deleteOrder(orders.id)
                notifyItemRemoved(position)
            }
        }
    }

    var total = 0.0
    private fun updateTotal(ptotal: Double) {
        total += ptotal
        listener.onQuantityChanged(total)
    }


    inner class MyHolder(var binding: ItemCartBinding) : ViewHolder(binding.root)
}