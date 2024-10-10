package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.CartActivity
import com.abahz.abbusiness.databinding.ItemCartBinding
import com.abahz.abbusiness.models.Carts
import com.abahz.abbusiness.utils.DBHelper
import com.bumptech.glide.Glide

class CartAdapter(val context: Context, val list: ArrayList<Carts>):Adapter<CartAdapter.CartHolder>() {
    inner class CartHolder(val binding: ItemCartBinding): ViewHolder(binding.root)
    private lateinit var dbHelper: DBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartHolder {
        return CartHolder(ItemCartBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int =list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CartHolder, position: Int) {
        dbHelper = DBHelper(context)
        val car = list[position]
        val punitaire = car.total.toDouble()/car.qty.toDouble()
        var QTY = car.qty.toDouble()
        var ptotal = punitaire* QTY
        holder.binding.cartQty.text = " ${car.qty}"
        holder.binding.total.text = "${car.qty} x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
        holder.binding.price.text = "Prix Unit.:${punitaire} ${dbHelper.getShopDevise()}"
        holder.binding.name.text = car.name
        Glide.with(context).load(car.image).into(holder.binding.image)
        holder.itemView.setOnLongClickListener {
                Builder(context).setItems(arrayOf("supprimer")){ dial, witch ->
                when(witch){
                    0->{
                        dial.dismiss()
                        dbHelper.deleteCart(car.id)
                        (context as CartActivity).onResume()
                    }
                }
            }.create().show()
            true
        }

        holder.binding.addition.setOnClickListener {
            if (QTY>0){
                QTY++
                ptotal = punitaire* QTY
                holder.binding.cartQty.text = " $QTY"
                holder.binding.total.text = "${QTY} x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
            }
        }
        holder.binding.remove.setOnClickListener {
            if (QTY>1){
                QTY--
                ptotal = punitaire* QTY
                holder.binding.cartQty.text = " $QTY"
                holder.binding.total.text = "${QTY} x $punitaire ${dbHelper.getShopDevise()} = $ptotal ${dbHelper.getShopDevise()}"
            }else if (QTY==1.0){
                dbHelper.deleteCart(car.id)
                (context as CartActivity).onResume()
            }
        }
    }
}