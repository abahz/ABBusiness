package com.abahz.abbusiness

import android.content.Intent
import android.os.Build
import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abahz.abbusiness.databinding.ActivityStartBinding
import com.abahz.abbusiness.utils.DBHelper

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var launcher: ActivityResultLauncher<Array<String>>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        launcher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            var allGranted = true
            for (granted in it.values) {
                allGranted = allGranted && granted
            }
            if (allGranted) {
                if (dbHelper.checkUser()) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }

            } else {
                Toast.makeText(this, "Il vous faut les prrmissions", Toast.LENGTH_SHORT).show()
            }
        }

        Handler(Looper.getMainLooper()).postDelayed({
            permissions()
        }, 1500)
    }

    private fun permissions() {
        val list = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list.add(Manifest.permission.BLUETOOTH_SCAN)
            list.add(Manifest.permission.BLUETOOTH_CONNECT)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            list.add(Manifest.permission.BLUETOOTH)
            list.add(Manifest.permission.BLUETOOTH_ADMIN)
            list.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            list.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (Build.VERSION.SDK_INT in Build.VERSION_CODES.Q..Build.VERSION_CODES.R) {
            list.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
        }

        if (list.isNotEmpty()) {
            launcher.launch(list.toTypedArray())
        }
    }
}