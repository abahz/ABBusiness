package com.abahz.abbusiness.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants
import com.abahz.abbusiness.utils.Constants.FACTURES
import com.abahz.abbusiness.utils.Constants.NOTES
import com.abahz.abbusiness.utils.Constants.ORDERS
import com.abahz.abbusiness.utils.Constants.PRODUCTS
import com.abahz.abbusiness.utils.DBHelper

class DownloadSync(context: Context, params: WorkerParameters) : Worker(context, params) {
    private lateinit var dbHelper: DBHelper

    override fun doWork(): Result {
        dbHelper = DBHelper(applicationContext)
        try {
            downloadProducts()
            downloadOrders()
            downloadNotes()
            downloadFactures()
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun downloadProducts() {
        Constants.prodRef().document(dbHelper.getShopUid()).collection(PRODUCTS).get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    val dsList = value.documents
                    for (ds in dsList) {
                        val product = ds.toObject(Products::class.java)
                        if (product != null) {
                            dbHelper.addProduct(product)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun downloadOrders() {
        Constants.orderRef().document(dbHelper.getShopUid()).collection(ORDERS).get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    val dsList = value.documents
                    for (ds in dsList) {
                        val orders = ds.toObject(Orders::class.java)
                        if (orders != null) {
                            dbHelper.addOrder(orders)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun downloadNotes() {
        Constants.notesRef().document(dbHelper.getShopUid()).collection(NOTES).get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    val dsList = value.documents
                    for (ds in dsList) {
                        val notes = ds.toObject(Notes::class.java)
                        if (notes != null) {
                            dbHelper.addNotes(notes)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }

    private fun downloadFactures() {
        Constants.factureRef().document(dbHelper.getShopUid()).collection(FACTURES).get()
            .addOnSuccessListener { value ->
                if (value != null) {
                    val dsList = value.documents
                    for (ds in dsList) {
                        val factures = ds.toObject(Factures::class.java)
                        if (factures != null) {
                            dbHelper.addFacture(factures)
                        }
                    }
                }
            }.addOnFailureListener { exception ->
                exception.printStackTrace()
            }
    }
}
