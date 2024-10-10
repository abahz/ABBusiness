package com.abahz.abbusiness.utils

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.abahz.abbusiness.models.Carts
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.models.ReOrder
import com.abahz.abbusiness.models.Users
import com.abahz.abbusiness.utils.Constants.ADDRESS
import com.abahz.abbusiness.utils.Constants.CARTS
import com.abahz.abbusiness.utils.Constants.CLIENT
import com.abahz.abbusiness.utils.Constants.CREATE_CARTS
import com.abahz.abbusiness.utils.Constants.CREATE_FACTURES
import com.abahz.abbusiness.utils.Constants.CREATE_NOTES
import com.abahz.abbusiness.utils.Constants.CREATE_ORDERS
import com.abahz.abbusiness.utils.Constants.CREATE_PRODUCTS
import com.abahz.abbusiness.utils.Constants.CREATE_USER
import com.abahz.abbusiness.utils.Constants.DATABASE
import com.abahz.abbusiness.utils.Constants.DATE
import com.abahz.abbusiness.utils.Constants.DEVISE
import com.abahz.abbusiness.utils.Constants.EMAIL
import com.abahz.abbusiness.utils.Constants.FACTURES
import com.abahz.abbusiness.utils.Constants.ID
import com.abahz.abbusiness.utils.Constants.IMAGE
import com.abahz.abbusiness.utils.Constants.ISYNC
import com.abahz.abbusiness.utils.Constants.MONTH
import com.abahz.abbusiness.utils.Constants.NAME
import com.abahz.abbusiness.utils.Constants.NOTE
import com.abahz.abbusiness.utils.Constants.NOTES
import com.abahz.abbusiness.utils.Constants.ORDERS
import com.abahz.abbusiness.utils.Constants.PASS
import com.abahz.abbusiness.utils.Constants.PRICE
import com.abahz.abbusiness.utils.Constants.PRODUCTS
import com.abahz.abbusiness.utils.Constants.QTY
import com.abahz.abbusiness.utils.Constants.REDUCTION
import com.abahz.abbusiness.utils.Constants.SHOP
import com.abahz.abbusiness.utils.Constants.TOTAL
import com.abahz.abbusiness.utils.Constants.UID
import com.abahz.abbusiness.utils.Constants.UNITY
import com.abahz.abbusiness.utils.Constants.USERS

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE, null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_USER)
        db?.execSQL(CREATE_PRODUCTS)
        db?.execSQL(CREATE_CARTS)
        db?.execSQL(CREATE_ORDERS)
        db?.execSQL(CREATE_NOTES)
        db?.execSQL(CREATE_FACTURES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(" DROP TABLE IF EXISTS $USERS ")
        db?.execSQL(" DROP TABLE IF EXISTS $PRODUCTS ")
        db?.execSQL(" DROP TABLE IF EXISTS $CARTS ")
        db?.execSQL(" DROP TABLE IF EXISTS $ORDERS ")
        db?.execSQL(" DROP TABLE IF EXISTS $NOTES ")
        db?.execSQL(" DROP TABLE IF EXISTS $FACTURES ")
        onCreate(db)
    }

    // ADD METHODS
    fun addUser(users: Users) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(UID, users.uid)
        value.put(SHOP, users.shop)
        value.put(ADDRESS, users.address)
        value.put(PASS, users.pass)
        value.put(DEVISE, users.devise)
        db.insert(USERS, null, value)
    }

    fun addProduct(products: Products) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(IMAGE, products.image)
        value.put(NAME, products.name)
        value.put(PRICE, products.price)
        value.put(QTY, products.qty)
        value.put(UNITY, products.unity)
        value.put(TOTAL, products.total)
        value.put(ISYNC, products.sync)
        db.insertWithOnConflict(PRODUCTS, null, value, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun addCart(carts: Carts) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(ID, carts.id)
        value.put(NAME, carts.name)
        value.put(IMAGE, carts.image)
        value.put(UNITY, carts.unity)
        value.put(QTY, carts.qty)
        value.put(TOTAL, carts.total)
        db.insertWithOnConflict(CARTS, null, value, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun addOrder(orders: Orders) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(ID, orders.id)
        value.put(NAME, orders.name)
        value.put(CLIENT, orders.client)
        value.put(DATE, orders.date)
        value.put(QTY, orders.qty)
        value.put(TOTAL, orders.total)
        value.put(ISYNC, orders.sync)
        db.insertWithOnConflict(ORDERS, null, value, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun addNotes(notes: Notes) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(ID, notes.id)
        value.put(PRICE, notes.price)
        value.put(NOTE, notes.note)
        value.put(CLIENT, notes.client)
        value.put(DATE, notes.date)
        value.put(MONTH, notes.month)
        value.put(ISYNC, notes.sync)
        db.insertWithOnConflict(NOTES, null, value, SQLiteDatabase.CONFLICT_REPLACE)
    }



    fun addFacture(factures: Factures) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(ID, factures.id)
        value.put(CLIENT, factures.client)
        value.put(DATE, factures.date)
        value.put(MONTH, factures.month)
        value.put(TOTAL, factures.total)
        value.put(REDUCTION, factures.reduction)
        value.put(ISYNC, factures.sync)
        db.insertWithOnConflict(FACTURES, null, value, SQLiteDatabase.CONFLICT_REPLACE) }

    // GET METHODS

    fun getProduct(): ArrayList<Products> {
        val db = readableDatabase
        val list = ArrayList<Products>()
        val cursor = db.rawQuery(" SELECT * FROM $PRODUCTS ", null)
        if (cursor.moveToFirst()) {
            do {
                val products = Products(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(UNITY)),
                    price = cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    total = cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    qty = cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC))
                )
                list.add(products)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getProd(): ArrayList<Products> {
        val db = readableDatabase
        val list = ArrayList<Products>()
        val cursor = db.rawQuery(" SELECT * FROM $PRODUCTS WHERE $ISYNC ='1'", null)
        if (cursor.moveToFirst()) {
            do {
                val products = Products(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(UNITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC))
                )
                list.add(products)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun searchProduct(query: String): ArrayList<Products> {
        val db = readableDatabase
        val list = ArrayList<Products>()
        val cursor = db.rawQuery(" SELECT * FROM $PRODUCTS WHERE $NAME LIKE '%$query%' ", null)
        if (cursor.moveToFirst()) {
            do {
                val products = Products(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(UNITY)),
                    "" +cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    "" +cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    "" +cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC))
                )
                list.add(products)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getCart(): ArrayList<Carts> {
        val db = readableDatabase
        val list = ArrayList<Carts>()
        val cursor = db.rawQuery(" SELECT * FROM $CARTS ", null)
        if (cursor.moveToFirst()) {
            do {
                val carts = Carts(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(UNITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QTY)))
                list.add(carts)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getOrder(date: String, client: String): ArrayList<Orders> {
        val db = readableDatabase
        val list = ArrayList<Orders>()
        val cursor = db.rawQuery(
            " SELECT * FROM $ORDERS WHERE $DATE =? AND $CLIENT =? ",
            arrayOf(date, client), null
        )
        if (cursor.moveToFirst()) {
            do {
                val orders = Orders(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    qty = cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    total = cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC))
                )
                list.add(orders)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getOrderDate(date: String): ArrayList<Orders> {
        val db = readableDatabase
        val list = ArrayList<Orders>()
        val cursor = db.rawQuery(
            " SELECT * FROM $ORDERS WHERE $DATE =? ",
            arrayOf(date), null
        )
        if (cursor.moveToFirst()) {
            do {
                val orders = Orders(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    qty = cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    total = cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(orders)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getOrd(): ArrayList<Orders> {
        val db = readableDatabase
        val list = ArrayList<Orders>()
        val cursor = db.rawQuery(
            " SELECT * FROM $ORDERS WHERE $ISYNC ='1' ",
            null
        )
        if (cursor.moveToFirst()) {
            do {
                val orders = Orders(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    qty = cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    total = cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(orders)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getNotes(date: String): ArrayList<Notes> {
        val db = readableDatabase
        val list = ArrayList<Notes>()
        val cursor = db.rawQuery(" SELECT * FROM $NOTES WHERE $DATE =? ", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                val notes = Notes(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NOTE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(notes)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getNoteMonth(date: String): ArrayList<Notes> {
        val db = readableDatabase
        val list = ArrayList<Notes>()
        val cursor = db.rawQuery(" SELECT * FROM $NOTES WHERE $MONTH =? ", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                val notes = Notes(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NOTE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(notes)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getNot(): ArrayList<Notes> {
        val db = readableDatabase
        val list = ArrayList<Notes>()
        val cursor = db.rawQuery(" SELECT * FROM $NOTES WHERE $ISYNC ='1' ",  null)
        if (cursor.moveToFirst()) {
            do {
                val notes = Notes(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NOTE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(notes)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getNotAll(): ArrayList<Notes> {
        val db = readableDatabase
        val list = ArrayList<Notes>()
        val cursor = db.rawQuery(" SELECT * FROM $NOTES ",  null)
        if (cursor.moveToFirst()) {
            do {
                val notes = Notes(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ID)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NOTE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)))
                list.add(notes)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }



    fun getFactures(date: String): ArrayList<Factures> {
        val list = ArrayList<Factures>()
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT  *  FROM $FACTURES WHERE $DATE =? ", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                val factures = Factures(
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(REDUCTION)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ID)))
                list.add(factures)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getFactMonth(date: String): ArrayList<Factures> {
        val list = ArrayList<Factures>()
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT  *  FROM $FACTURES WHERE $MONTH =? ", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                val factures = Factures(
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(REDUCTION)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ID)))
                list.add(factures)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun getFactAll(): ArrayList<Factures> {
        val list = ArrayList<Factures>()
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT  *  FROM $FACTURES ", null)
        if (cursor.moveToFirst()) {
            do {
                val factures = Factures(
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(REDUCTION)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ID)))
                list.add(factures)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }


    fun getFact(): ArrayList<Factures> {
        val list = ArrayList<Factures>()
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT  *  FROM $FACTURES WHERE $ISYNC ='1'", null)
        if (cursor.moveToFirst()) {
            do {
                val factures = Factures(
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(CLIENT)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(REDUCTION)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(DATE)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(MONTH)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ISYNC)),
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(ID)))
                list.add(factures)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    //USER DATA
    fun checkUser(): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT * FROM $USERS ", null)
        if (cursor.count > 0) {
            return true
        } else {
            cursor.close()
            close()
            return false
        }
    }

    fun getShopUid(): String {
        var name = ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $USERS", null)
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(UID))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return name
    }

    fun getShopDevise(): String {
        var name = ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $USERS", null)
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(DEVISE))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return name
    }

    fun getShopName(): String {
        var name = ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $USERS", null)
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(SHOP))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return name
    }

    fun getShopAddress(): String {
        var name = ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $USERS", null)
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(ADDRESS))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return name
    }



    fun getShopPass(): String {
        var name = ""
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $USERS", null)
        if (cursor.moveToFirst()) {
            do {
                name = cursor.getString(cursor.getColumnIndexOrThrow(PASS))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return name
    }

    fun getCountCart(): Int {
        var count = 0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $CARTS ", null)
        if (cursor.moveToFirst()) {
            do {
                count = cursor.count
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun editUser(
        uid: String,
        address: String,
        pass: String,
        email: String,
        devise: String,
    ) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(ADDRESS, address)
        value.put(PASS, pass)
        value.put(EMAIL, email)
        value.put(DEVISE, devise)
        db.update(USERS, value, "$UID=?", arrayOf(uid))
    }

    fun editNotes(
        id: String,
        price: Long,
        note: String,
        client: String,
        sync: String
    ) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(PRICE, price)
        value.put(NOTE, note)
        value.put(CLIENT, client)
        value.put(ISYNC, sync)
        db.update(NOTES, value, "$ID=?", arrayOf(id))
    }

    //PRODUCT DATA
    fun editProduct(
        name: String,
        price: Long,
        qty: Long,
        total: Long,
        sync: String
    ) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(PRICE, price)
        value.put(QTY, qty)
        value.put(TOTAL, total)
        value.put(ISYNC, sync)
        db.update(PRODUCTS, value, "$NAME=?", arrayOf(name))
    }

    fun checkPid(pid: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $CARTS WHERE $NAME =? ", arrayOf(pid), null)
        if (cursor.count > 0) {
            cursor.close()
            close()
            return true
        } else {
            cursor.close()
            close()
            return false
        }
    }
    fun checkProduct(pid: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PRODUCTS WHERE $NAME =? ", arrayOf(pid), null)
        if (cursor.count > 0) {
            cursor.close()
            close()
            return true
        } else {
            cursor.close()
            close()
            return false
        }
    }

    fun getProdQtyStock(id: String): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PRODUCTS WHERE $NAME =? ", arrayOf(id), null)
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getLong(cursor.getColumnIndexOrThrow(QTY))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun getProdPriceUnit(id: String): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PRODUCTS WHERE $NAME =? ", arrayOf(id), null)
        if (cursor.moveToFirst()) {
            do {
                count = cursor.getLong(cursor.getColumnIndexOrThrow(PRICE))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun getProdPriceTotal(): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $PRODUCTS ", null)
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getLong(cursor.getColumnIndexOrThrow(TOTAL))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun updateQty(id: String, qty: String, total: String) {
        val db = writableDatabase
        val value = ContentValues()
        value.put(QTY, qty)
        value.put(TOTAL, total)
        db.update(PRODUCTS, value, "$NAME=?", arrayOf(id))
    }

    fun getTotalSales(date: String): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $ORDERS WHERE $DATE =?", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getLong(cursor.getColumnIndexOrThrow(TOTAL))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }
    fun getTotalReduction(date: String): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $FACTURES WHERE $DATE =?", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getLong(cursor.getColumnIndexOrThrow(REDUCTION))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun getTotalExpenses(date: String): Long {
        var count = 0L
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $NOTES WHERE $DATE =?", arrayOf(date), null)
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getLong(cursor.getColumnIndexOrThrow(PRICE))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun deleteProduct(id: String) {
        val db = readableDatabase
        db.delete(PRODUCTS, "$NAME=?", arrayOf(id))
        close()
    }

    fun deleteCart(id: String) {
        val db = readableDatabase
        db.delete(CARTS, "$ID=?", arrayOf(id))
        close()
    }

    fun deleteNote(id: String) {
        val db = readableDatabase
        db.delete(NOTES, "$ID=?", arrayOf(id))
        close()
    }

    fun deleteOrder(id: String) {
        val db = readableDatabase
        db.delete(ORDERS, "$ID=?", arrayOf(id))
        close()
    }

    fun deleteFacture(id: String) {
        val db = readableDatabase
        db.delete(FACTURES, "$ID=?", arrayOf(id))
        close()
    }


    //CART DATA

    fun deleteAllCart() {
        val db = readableDatabase
        db.execSQL(" DELETE FROM $CARTS")
        db.close()
    }

    fun getTotalCart(): Double {
        var count = 0.0
        val db = readableDatabase
        val cursor = db.rawQuery(" SELECT * FROM $CARTS", null)
        if (cursor.moveToFirst()) {
            do {
                count += cursor.getLong(cursor.getColumnIndexOrThrow(TOTAL))
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count

    }

    //GET METHODE
    fun getProductById(id: String): ArrayList<Products> {
        val db = readableDatabase
        val list = ArrayList<Products>()
        val cursor = db.rawQuery(" SELECT * FROM $PRODUCTS WHERE $NAME =?", arrayOf(id), null)
        if (cursor.moveToFirst()) {
            do {
                val products = Products(
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(IMAGE)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(UNITY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(PRICE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(TOTAL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(QTY)),
                    "" + cursor.getString(cursor.getColumnIndexOrThrow(ISYNC))
                )
                list.add(products)
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return list
    }

    fun checkNewProd():Boolean{
        val cursor = readableDatabase.rawQuery("SELECT * FROM $PRODUCTS WHERE $ISYNC ='1'",null)
        if (cursor.count>0){
            cursor.close()
            return true
        }else{
            cursor.close()
            return false
        }
    }

    fun checkNewFact():Boolean{
        val cursor = readableDatabase.rawQuery("SELECT * FROM $FACTURES WHERE $ISYNC ='1'",null)
        if (cursor.count>0){
            cursor.close()
            return true
        }else{
            cursor.close()
            return false
        }
    }
    fun checkNewNote():Boolean{
        val cursor = readableDatabase.rawQuery("SELECT * FROM $NOTES WHERE $ISYNC ='1'",null)
        if (cursor.count>0){
            cursor.close()
            return true
        }else{
            cursor.close()
            return false
        }
    }
    fun getInvoiceNumber(): Int {
        var count = 0
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $FACTURES ", null)
        if (cursor.moveToFirst()) {
            do {
                count = cursor.count
            } while (cursor.moveToNext())
        }
        cursor.close()
        close()
        return count
    }

    fun getReOrder(date: String):ArrayList<ReOrder>{
        val db = readableDatabase
        val list = ArrayList<ReOrder>()
        val cursor = db.rawQuery("SELECT $NAME, SUM(QTY) AS $QTY, SUM(TOTAL) AS $TOTAL FROM $ORDERS WHERE $DATE =? GROUP BY $NAME ",
            arrayOf(date),null)
        if (cursor.moveToFirst()){
            do {
                val orders = ReOrder(
                    ""+cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                    qty = cursor.getDouble(cursor.getColumnIndexOrThrow(QTY)),
                    price = cursor.getDouble(cursor.getColumnIndexOrThrow(TOTAL)))
                list.add(orders)
            }while (cursor.moveToNext())
        }
        cursor.close()
        return list
    }
}