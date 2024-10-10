package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abahz.abbusiness.adapters.CartAdapter
import com.abahz.abbusiness.databinding.ActivityCartBinding
import com.abahz.abbusiness.interfaces.OnQuantityChangeListener
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.models.Orders
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.Constants.MY_MONTH
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.DBHelper
import java.util.UUID

class CartActivity : AppCompatActivity(), OnQuantityChangeListener {
    private lateinit var binding: ActivityCartBinding
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status)
        dbHelper = DBHelper(this)
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.cardRv.setHasFixedSize(true)
        binding.cardRv.layoutManager = LinearLayoutManager(this)
        getCarts()
        binding.addOrder.setOnClickListener { addOrder() }
    }

    private fun addOrder() {
        var totalFacture = 0.0
        val reduc = binding.reduc.text.toString()
        val client = binding.client.text.toString()
        if (client.isEmpty()) {
            alert(this, "Nom du client", "Ajouter le nom du client svp")
        } else {
            if (binding.reduc.text.isEmpty()) {
                binding.reduc.setText("0")
            } else {
                val fact = dbHelper.getInvoiceNumber() + 1
                for (carts in dbHelper.getCart()) {
                    val orders = Orders(
                        "" + UUID.randomUUID(),
                        "" + carts.name,
                        "" + client,
                        "" + MY_DATE,
                        qty = carts.qty,
                        total = carts.total,
                        "" + 1
                    )
                    dbHelper.addOrder(orders)
                    val myQty = (dbHelper.getProdQtyStock(carts.name) - carts.qty.toDouble())
                    val total = myQty * dbHelper.getProdPriceUnit(carts.name)
                    dbHelper.updateQty(carts.name, myQty.toString(), total.toString())
                    dbHelper.deleteAllCart()
                    totalFacture += carts.total.toDouble()
                }
                val totalNet = totalFacture - reduc.toDouble()
                val factures = Factures(
                    "" + client,
                    total = totalNet.toString(),
                    reduction = reduc,
                    "" + MY_DATE,
                    "" + MY_MONTH,
                    "" + 1,
                    "" + fact
                )
                dbHelper.addFacture(factures)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }


    private fun getCarts() {
        if (dbHelper.getCountCart() > 0) {
            binding.empty.visibility = View.GONE
            binding.cardRv.visibility = View.VISIBLE
            binding.bottom.visibility = View.VISIBLE
            binding.cardRv.adapter = CartAdapter(this, dbHelper.getCart())
        } else {
            binding.empty.visibility = View.VISIBLE
            binding.cardRv.visibility = View.GONE
            binding.bottom.visibility = View.GONE
        }
    }

    @SuppressLint("SetTextI18n")
    public override fun onResume() {
        super.onResume()
        getCarts()
    }

    override fun onQuantityChanged(totalAmount: Double) {
        binding.total.text = "$totalAmount ${dbHelper.getShopDevise()}"
    }
}