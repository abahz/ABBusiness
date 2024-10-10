package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog.Builder
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abahz.abbusiness.InvoiceActivity
import com.abahz.abbusiness.R
import com.abahz.abbusiness.databinding.ItemInvoiceBinding
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.interfaces.OnQuantityChangeListener
import com.abahz.abbusiness.utils.Constants.FACTURES
import com.abahz.abbusiness.utils.Constants.ORDERS
import com.abahz.abbusiness.utils.Constants.factureRef
import com.abahz.abbusiness.utils.Constants.orderRef
import com.abahz.abbusiness.utils.DBHelper

class FactureAdapter(
    var context: Context,
    private var list: ArrayList<Factures>,
    private val listener: OnQuantityChangeListener // On passe le listener ici
) : RecyclerView.Adapter<FactureAdapter.FactureHolder>() {

    inner class FactureHolder(var binding: ItemInvoiceBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var dbHelper: DBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FactureHolder {
        return FactureHolder(ItemInvoiceBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int = list.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: FactureHolder, position: Int) {
        dbHelper = DBHelper(context)
        val factures = list[position]

        // Affichage des informations de la facture
        holder.binding.number.text = "FACTURE No: ${factures.id}"
        holder.binding.price.text = "Total: ${factures.total} ${dbHelper.getShopDevise()}"
        holder.binding.client.text = "Client: ${factures.client}"
        holder.binding.date.text = factures.date

        // Gestion de l'événement "click" pour ouvrir les détails de la facture
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, InvoiceActivity::class.java).putExtra("model", factures))
        }

        // Gestion de l'événement "long click" pour modifier la facture
        holder.itemView.setOnLongClickListener {
            Builder(context).setItems(arrayOf("Modifier","Supprimer")) { dialog, which ->
                dialog.dismiss()
                when (which) {
                    0 -> openDialogEdit(factures)
                    1->{
                        notifyItemRemoved(position)
                        list.remove(factures)
                        dbHelper.deleteFacture(factures.id)
                        factureRef().document(dbHelper.getShopUid()).collection(FACTURES).document(factures.id).delete()
                        for (order in dbHelper.getOrder(factures.date,factures.client)){
                            orderRef().document(dbHelper.getShopUid()).collection(ORDERS).document(order.id).delete()
                            dbHelper.deleteOrder(order.id)
                        }

                    }
                }
            }.create().show()
            true
        }
    }

    private fun openDialogEdit(factures: Factures) {
        val view = LayoutInflater.from(context).inflate(R.layout.edit_invoice, null)
        val builder = Builder(context,R.style.FullScreenDialog).setView(view)
        val dialog = builder.create()
        dialog.show()

        val recycler = view.findViewById<RecyclerView>(R.id.recycler_i)
        view.findViewById<ImageButton>(R.id.back_i).setOnClickListener { dialog.dismiss() }
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)

        recycler.adapter = OrderCartAdapter(context, dbHelper.getOrder(factures.date, factures.client), listener = listener)
    }
}
