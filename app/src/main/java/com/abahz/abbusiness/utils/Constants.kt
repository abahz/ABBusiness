package com.abahz.abbusiness.utils

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Constants {
    const val DATABASE = "Abahz.db"
    const val USERS = "Users"
    const val PRODUCTS = "Products"
    const val CARTS = "Carts"
    const val ORDERS = "Orders"
    const val NOTES = "Notes"
    const val FACTURES = "Factures"

    const val ID = "id"
    const val UID = "uid"
    const val EMAIL = "email"
    const val IMAGE = "image"
    const val NAME = "name"
    const val SHOP = "shop"
    const val PASS = "pass"
    const val ADDRESS = "address"
    const val CLIENT = "client"
    const val QTY = "qty"
    const val PRICE = "price"
    const val UNITY = "unity"
    const val MONTH = "month"
    const val DATE = "date"
    const val DEVISE = "devise"
    const val TOTAL = "total"
    const val NOTE = "note"
    const val REDUCTION = "reduction"
    const val ISYNC = "sync"
    //FIREBASE


    fun userRef(): CollectionReference = FirebaseFirestore.getInstance().collection(USERS)

    fun prodRef(): CollectionReference = FirebaseFirestore.getInstance().collection(PRODUCTS)

    fun orderRef(): CollectionReference = FirebaseFirestore.getInstance().collection(ORDERS)

    fun factureRef(): CollectionReference = FirebaseFirestore.getInstance().collection(FACTURES)

    fun notesRef(): CollectionReference = FirebaseFirestore.getInstance().collection(NOTES)

    fun reference(): StorageReference = FirebaseStorage.getInstance().reference
    fun alert(context: Context?, title: String?, message: String?) {
        if (context!=null){
          AlertDialog.Builder(context).apply {
              setTitle(title)
              setMessage(message)
              setPositiveButton("Oui"){dialo, wicth -> dialo.dismiss()
              }
            }.create().show()
        }
    }

    fun Progress(isProgress: Boolean, button: Button, progress: ProgressBar) {
        if (isProgress) {
            button.visibility = View.GONE
            progress.visibility = View.VISIBLE
        } else {
            button.visibility = View.VISIBLE
            progress.visibility = View.GONE
        }
    }

    @SuppressLint("SimpleDateFormat")
    var MY_YEAR: String = SimpleDateFormat("yyyy", Locale.getDefault()).format(Date())

    @SuppressLint("SimpleDateFormat")
    var DAY_ONLY: String = SimpleDateFormat("dd", Locale.getDefault()).format(Date())

    @SuppressLint("ConstantLocale")
    var MONTH_ONLY: String = SimpleDateFormat("MM", Locale.getDefault()).format(Date())

    @SuppressLint("SimpleDateFormat")
    var MY_DATE: String = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

    @SuppressLint("SimpleDateFormat")
    var MY_MONTH: String = SimpleDateFormat("MM/yyyy", Locale.getDefault()).format(Date())



    @SuppressLint("SimpleDateFormat", "ConstantLocale")
    val DATE_COMPLETE: String = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

    fun rapport() = when(MONTH_ONLY){
        "01"-> "Janvier"
        "02"-> "Fevrier"
        "03"-> "Mars"
        "04"-> "Avril"
        "05"-> "Mai"
        "06"-> "Juin"
        "07"-> "Juillet"
        "08"-> "Aout"
        "09"-> "Septembre"
        "10"-> "Octobre"
        "11"-> "Novembre"
        "12"-> "Décembre"
        else -> { " " }
    }
    var CREATE_USER = " CREATE TABLE $USERS (" +
            " $UID TEXT PRIMARY KEY, " +
            " $SHOP TEXT, " +
            " $ADDRESS TEXT, " +
            " $PASS TEXT, " +
            " $DEVISE TEXT )"

    var CREATE_PRODUCTS = " CREATE TABLE $PRODUCTS ( " +
            " $NAME TEXT PRIMARY KEY," +
            " $IMAGE TEXT," +
            " $PRICE INTEGER," +
            " $QTY INTEGER," +
            " $TOTAL INTEGER," +
            " $UNITY TEXT," +
            " $ISYNC TEXT  )"

    var CREATE_CARTS = " CREATE TABLE $CARTS ( " +
            " $ID TEXT PRIMARY KEY," +
            " $IMAGE TEXT," +
            " $NAME TEXT," +
            " $UNITY TEXT," +
            " $QTY INTEGER," +
            " $TOTAL INTEGER )"

    var CREATE_ORDERS = " CREATE TABLE $ORDERS ( " +
            " $ID TEXT PRIMARY KEY," +
            " $NAME TEXT," +
            " $CLIENT TEXT," +
            " $DATE TEXT," +
            " $QTY INTEGER," +
            " $TOTAL INTEGER," +
            " $ISYNC TEXT )"

    var CREATE_NOTES = " CREATE TABLE  $NOTES (" +
            " $ID  TEXT PRIMARY KEY , " +
            " $NOTE  TEXT, " +
            " $DATE  TEXT, " +
            " $MONTH TEXT," +
            " $CLIENT TEXT, " +
            " $PRICE INTEGER, " +
            " $ISYNC TEXT )"

    var CREATE_FACTURES = " CREATE TABLE $FACTURES (" +
            " $ID TEXT PRIMARY KEY , " +
            " $CLIENT  TEXT, " +
            " $TOTAL  INTEGER, " +
            " $MONTH TEXT," +
            " $DATE  TEXT, " +
            " $REDUCTION  TEXT, " +
            " $ISYNC TEXT )"
}