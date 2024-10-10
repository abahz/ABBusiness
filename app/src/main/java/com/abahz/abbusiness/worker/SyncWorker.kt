package com.abahz.abbusiness.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants
import com.abahz.abbusiness.utils.Constants.FACTURES
import com.abahz.abbusiness.utils.Constants.MY_MONTH
import com.abahz.abbusiness.utils.Constants.NOTES
import com.abahz.abbusiness.utils.Constants.ORDERS
import com.abahz.abbusiness.utils.Constants.PRODUCTS
import com.abahz.abbusiness.utils.Constants.reference
import com.abahz.abbusiness.utils.DBHelper

class SyncWorker(context: Context, params:WorkerParameters) :Worker(context,params) {
    private lateinit var dbHelper: DBHelper
    override fun doWork(): Result {
         dbHelper = DBHelper(applicationContext)
        syncProductsWithFirebase()
        syncOrdersWithFirebase()
        syncNotesWithFirebase()
        syncFacturesWithFirebase()
        return Result.success()

    }
    private fun syncFacturesWithFirebase() {
        for (factures in dbHelper.getFact()){
            val fact = Factures(
                ""+factures.client,
                total = factures.total,
                reduction = factures.reduction,
                ""+factures.date,
                ""+ factures.month,
                ""+0,
                ""+factures.id)
            Constants.factureRef().document(dbHelper.getShopUid()).collection(FACTURES).document(factures.id).set(fact)
        }
    }

    private fun syncProductsWithFirebase() {
        val products = dbHelper.getProd()
        for (product in products) {
            val ref = reference().child("$PRODUCTS/${product.name}.jpg")
            ref.putFile(Uri.parse(product.image)).addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    val firebaseProduct = Products(
                        ""+product.name,
                        ""+uri,
                        ""+product.unity,
                        price = product.price,
                        total = product.total,
                        qty = product.qty,
                        ""+0)
                    Constants.prodRef().document(dbHelper.getShopUid()).collection(PRODUCTS).document(product.name).set(firebaseProduct)
                }
            }
        }
    }

    private fun syncOrdersWithFirebase() {
        for (order in dbHelper.getOrd()) {
            val orders = Orders(
                ""+order.id,
                ""+order.name,
                ""+order.client,
                ""+order.date,
                qty = order.qty,
                total = order.total,
                ""+0)
            Constants.orderRef().document(dbHelper.getShopUid()).collection(ORDERS).document(order.id).set(orders)
        }
    }
    private fun syncNotesWithFirebase() {
        for (note in dbHelper.getNot()) {
            val notes = Notes(
                ""+note.id,
                ""+note.client,
                ""+note.note,
                ""+note.date,
                ""+note.month,
                price = note.price,
                ""+0)
            Constants.notesRef().document(dbHelper.getShopUid()).collection(NOTES).document(note.id).set(notes)
            Log.d("MY_LOG","db Notes ======> ${dbHelper.getNot()}")
        }
    }
}