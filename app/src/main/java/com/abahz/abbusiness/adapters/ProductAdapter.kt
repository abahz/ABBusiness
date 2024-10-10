package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.utils.widget.MotionButton
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.R
import com.abahz.abbusiness.databinding.ItemProductBinding
import com.abahz.abbusiness.fragments.HomeFragment
import com.abahz.abbusiness.interfaces.AddToCart
import com.abahz.abbusiness.models.Carts
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants.PRODUCTS
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.Constants.prodRef
import com.abahz.abbusiness.utils.Constants.reference
import com.abahz.abbusiness.utils.DBHelper
import com.bumptech.glide.Glide

class ProductAdapter(var context: Context, private var list:ArrayList<Products>,private val addToCart: AddToCart):Adapter<ProductAdapter.ProdHolder>() {
    inner class ProdHolder(var binding:ItemProductBinding):ViewHolder(binding.root)
    private lateinit var dbHelper: DBHelper
    private var cartItemCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdHolder {
        return ProdHolder(ItemProductBinding.inflate(LayoutInflater.from(context),parent,false))
    }


    override fun getItemCount(): Int =list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateProductList(newList: ArrayList<Products>) {
        list = newList
        notifyDataSetChanged() // Notify adapter about the dataset change
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProdHolder, position: Int) {
        dbHelper = DBHelper(context)
         val products = list[position]
        holder.binding.name.text = products.name
        holder.binding.price.text = "${products.price} ${dbHelper.getShopDevise()}"
        holder.binding.qty.text = "En stock: ${products.qty} ${products.unity}"
        Glide.with(context).load(products.image).into(holder.binding.image)
        var QTY = 1.0
        val PRICE = products.price
        var TOTAL = QTY *PRICE.toDouble()
        holder.binding.total.text = "$QTY x ${products.price} ${dbHelper.getShopDevise()}  = $TOTAL ${dbHelper.getShopDevise()}"
        holder.binding.addBtn.setOnClickListener {
            if (QTY<(products.qty.toDouble()-1)){
                QTY++
                TOTAL = QTY*PRICE.toDouble()
                holder.binding.editQty.text = QTY.toString()
                holder.binding.price.text = "$TOTAL ${dbHelper.getShopDevise()}"
                holder.binding.total.text = "$QTY x ${products.price} ${dbHelper.getShopDevise()} = $TOTAL ${dbHelper.getShopDevise()}"
            }else{
                alert(context, products.name,"Il n'y a que ${products.qty} en stock ")
            }
        }
        holder.binding.rmvBtn.setOnClickListener {
            if (QTY>1.0){
                QTY--
                TOTAL = QTY*PRICE.toDouble()
                holder.binding.editQty.text = QTY.toString()
                holder.binding.price.text = "$TOTAL ${dbHelper.getShopDevise()}"
                holder.binding.total.text = "$QTY x ${products.price} ${dbHelper.getShopDevise()} = $TOTAL ${dbHelper.getShopDevise()}"
            }else{
                alert(context, products.name,"Il n'y a que ${products.qty} au panier ")
            }
        }

        holder.binding.addButton.setOnClickListener {
            if (dbHelper.checkPid(products.name)){
                alert(context,products.name, "Il est deja au panier svp, ajouter un autre ou tape sur modifier le panier")
            }else{
                val carts = Carts(
                    ""+System.currentTimeMillis(),
                    ""+products.name,
                    ""+products.image,
                    ""+products.unity,
                    total = TOTAL.toString(),
                    qty = QTY.toString())
                dbHelper.addCart(carts)
                cartItemCount++
                addToCart.onCartItemUpdated(cartItemCount)
                QTY = 1.0
                holder.binding.editQty.text = QTY.toString()
            }

        }

        holder.itemView.setOnLongClickListener {
            val options = arrayOf("Modifier", "Supprimer")
            AlertDialog.Builder(context).setItems(options){ dialog , witch ->
                when (witch){
                    0-> {
                        openEditDialog(products, position)
                    }
                    1->{
                        prodRef().document(dbHelper.getShopUid()).collection(PRODUCTS).document(products.name).delete().addOnSuccessListener {
                            reference().child("$PRODUCTS/${products.name}.jpg").delete()
                        }
                        dialog.dismiss()
                        list.remove(products)
                        notifyItemRemoved(position)
                        dbHelper.deleteProduct(products.name)


                    }
                }
            }.create().show()
            true
        }
    }

    @SuppressLint("SetTextI18n")
    private fun openEditDialog(product: Products, position: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.edit_product, null)
        val build = AlertDialog.Builder(context, R.style.FullScreenDialog).setView(view)
        val dialog = build.create()
        dialog.show()

        val nameEt = view.findViewById<TextView>(R.id.cartName)
        nameEt.text = "Modification ${product.name}"
        val priceEt = view.findViewById<EditText>(R.id.prdPrice)
        val qtyEt = view.findViewById<EditText>(R.id.prdQty)
        view.findViewById<ImageButton>(R.id.prdBack).setOnClickListener { dialog.dismiss() }

        priceEt.setText(product.price)
        qtyEt.setText(product.qty)

        // Set click listener for the button to update the product
        view.findViewById<MotionButton>(R.id.prdButton).setOnClickListener {
            val price = priceEt.text.toString().trim()
            val qty = qtyEt.text.toString().trim()

            if (price.isEmpty() || qty.isEmpty()) {
                Toast.makeText(context, "Veuillez entrer toutes les données", Toast.LENGTH_SHORT).show()
            } else {
                val total = qty.toInt() * price.toLong()
                dbHelper.editProduct(
                    "" + product.name,
                    price = price.toLong(),
                    qty = qty.toLong(),
                    total = total,
                    "" + 1)
                val map = mapOf(
                    "price" to price,
                    "qty" to qty,
                    "total" to total,
                    "sync" to 0)
                prodRef().document(dbHelper.getShopUid()).collection(PRODUCTS).document(product.name).update(map)

                product.price = price
                product.qty = qty

                notifyItemChanged(position)

                (context as? HomeFragment)?.refreshProductList()
                dialog.dismiss()
            }
        }
    }


}