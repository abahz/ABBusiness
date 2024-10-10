package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abahz.abbusiness.databinding.ActivitySettingBinding
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.DBHelper

class SettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingBinding
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status)
        initView()
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.daily.setOnClickListener { startActivity(Intent(this,ReportActivity::class.java)) }
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.shop.text = dbHelper.getShopName()
        binding.address.text = dbHelper.getShopAddress()
        binding.phone.text = dbHelper.getShopUid()
        val stock = dbHelper.getProdPriceTotal()+dbHelper.getTotalSales(MY_DATE)
        binding.stock.text = "$stock ${dbHelper.getShopDevise()}"
        binding.stockAfter.text = "${dbHelper.getProdPriceTotal()} ${dbHelper.getShopDevise()}"
        binding.sitJr.text = "Situation net du $MY_DATE"
        binding.vente.text = "${dbHelper.getTotalSales(MY_DATE)} ${dbHelper.getShopDevise()}"
        binding.depense.text = "${dbHelper.getTotalExpenses(MY_DATE)} ${dbHelper.getShopDevise()}"
        binding.reduc.text = "${dbHelper.getTotalReduction(MY_DATE)} ${dbHelper.getShopDevise()}"
    }
}