package com.abahz.abbusiness

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abahz.abbusiness.adapters.Adapter
import com.abahz.abbusiness.databinding.ActivityMainBinding
import com.abahz.abbusiness.fragments.ExpenseFragment
import com.abahz.abbusiness.fragments.HomeFragment
import com.abahz.abbusiness.fragments.OrderFragment
import com.abahz.abbusiness.interfaces.AddToCart
import com.abahz.abbusiness.interfaces.ClickButtons
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.DBHelper
import com.abahz.abbusiness.worker.DownloadSync
import com.abahz.abbusiness.worker.SyncWorker
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity(), AddToCart {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: Adapter
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        window.statusBarColor = ContextCompat.getColor(this,R.color.status)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.status)
        adapter = Adapter(this)
        binding.viewpager.adapter = adapter
        TabLayoutMediator(binding.tablayout,binding.viewpager){ tab, position ->
            when(position){
                0-> tab.text = "Acceuil"
                1-> tab.text = "Ventes"
                2-> tab.text = "Dépenses"
            }
        }.attach()

        binding.moreId.setOnClickListener {
            startActivity(Intent(this,SettingActivity::class.java))
        }

        binding.toCart.setOnClickListener {
            startActivity(Intent(this,CartActivity::class.java))
        }

        binding.searchId.setOnClickListener {
            val currentFrg = adapter.getFragment(binding.viewpager.currentItem)
            if (currentFrg is ClickButtons){
                currentFrg.onClickSearch()
                binding.toolbar.visibility = View.GONE
            }
        }
    }


    fun searchToolbar(isProgress: Boolean) {
        if (::binding.isInitialized) {
            if (isProgress) {
                binding.toolbar.visibility = View.GONE
            } else {
                binding.toolbar.visibility = View.VISIBLE
            }
        }
    }

    override fun onCartItemUpdated(itemCount: Int) {
        binding.toCart.text = "${dbHelper.getCountCart()}"
    }

    override fun onResume() {
        super.onResume()
        binding.toCart.text = "${dbHelper.getCountCart()}"
        if (dbHelper.checkNewProd()){
            startUpload()
            startDownload()
        }
        if (dbHelper.checkNewNote()){
            startUpload()
            startDownload()
        }
        if (dbHelper.checkNewFact()){
            startUpload()
            startDownload()
        }
        if (dbHelper.getFactures(MY_DATE).isEmpty()){
            startUpload()
            startDownload()
        }
        if (dbHelper.getProduct().isEmpty()){
            startUpload()
            startDownload()
        }
        if (dbHelper.getNotes(MY_DATE).isEmpty()){
            startUpload()
            startDownload()
        }
    }
    private fun startUpload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val uploadRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(uploadRequest)
    }

    private fun startDownload() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val downloadRequest = OneTimeWorkRequestBuilder<DownloadSync>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueue(downloadRequest)

    }


}