package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog.Builder
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.abahz.abbusiness.databinding.ActivityAddBinding
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants.Progress
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.DBHelper
import com.bumptech.glide.Glide

class AddActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddBinding
    private lateinit var dbHelper: DBHelper
    private var imageUri: Uri? = null
    private lateinit var unityList: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status)
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.addImage.setOnClickListener {
            launcher.launch(
                Intent(Intent.ACTION_OPEN_DOCUMENT).setType(
                    "image/*"
                )
            )
        }


        binding.unity.setOnClickListener { openAlertUnity() }

        binding.button.setOnClickListener {
            Progress(true, binding.button, binding.progress)
            val name = binding.name.text.toString().trim()
            val price = binding.price.text.toString().trim()
            val qty = binding.qty.text.toString().trim()
            val unity = binding.unity.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || qty.isEmpty() || unity.isEmpty()) {
                alert(this, "Cases vides !", " Veuillez completez toutes les cases SVP !")
                Progress(false, binding.button, binding.progress)
            } else if (imageUri == null) {
                alert(this, "Image vide", "Ajouter l'image svp")
                Progress(false, binding.button, binding.progress)
            } else if (dbHelper.checkProduct(name)) {
                Progress(false, binding.button, binding.progress)
                alert(
                    this,
                    "Incompatibilite",
                    "$name est deja en stock, vous pouvez modifier la quantite et le prix ce dernier, ou bien ajouter des produits similaires !"
                )
            } else {
                val total = qty.toDouble() * price.toDouble()
                val products = Products(
                    "" + name,
                    "" + imageUri,
                    "" + unity,
                    price = price,
                    total = total.toString(),
                    qty = qty,
                    "" + 1)
                dbHelper.addProduct(products)
                Progress(false, binding.button, binding.progress)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }

    @SuppressLint("InflateParams", "SetTextI18n")
    private fun openAlertUnity() {
        val view = LayoutInflater.from(this).inflate(R.layout.item_unity,null)
        val builder = Builder(this,R.style.FullScreenDialog).setView(view)
        val dialog = builder.create()
        val listView = view.findViewById<ListView>(R.id.listAlert)
        unityList = arrayListOf("Pièces",
            "Cartons",
            "Sacs",
            "Douzaines",
            "Sixaines",
            "Boxs",
            "Kilogrammes",
            "Litres",
            "Bidons",
            "Grammes",
            "Autres")

        dialog.show()
        listView.adapter = ArrayAdapter(this, R.layout.item_row, unityList)
        listView.setOnItemClickListener { adapterView, view, i, l ->
            binding.unity.text = adapterView.getItemAtPosition(i).toString()
            dialog.dismiss()
        }

    }

    private var launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                imageUri = it.data!!.data!!
                contentResolver.takePersistableUriPermission(
                    imageUri!!,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Glide.with(this).load(imageUri).into(binding.addImage)
            }

        }
}