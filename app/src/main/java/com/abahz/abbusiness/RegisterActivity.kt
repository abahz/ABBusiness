package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.app.AlertDialog.Builder
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abahz.abbusiness.databinding.ActivityRegisterBinding
import com.abahz.abbusiness.models.Users
import com.abahz.abbusiness.utils.Constants.Progress
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.Constants.userRef
import com.abahz.abbusiness.utils.DBHelper
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbHelper: DBHelper
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
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
        binding.switchTo.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        auth = FirebaseAuth.getInstance()

        binding.devise.setOnClickListener {
            Builder(this).setItems(arrayOf("Fc","$")){dialog,witch->
                dialog.dismiss()
                when(witch){
                    0-> binding.devise.text = "Fc"
                    1-> binding.devise.text = "$"
                }
            }.create().show()
        }

        binding.button.setOnClickListener {
            Progress(true,binding.button, progress = binding.progress)
            val name = binding.name.text.toString().trim()
            val phone = binding.phone.text.toString().trim()
            val pass = binding.pass.text.toString().trim()
            val address = binding.address.text.toString().trim()
            val devise = binding.devise.text.toString().trim()

            if (name.isEmpty()||phone.isEmpty()||pass.isEmpty()||address.isEmpty()||devise.isEmpty()){
                Progress(false, binding.button, binding.progress)
                alert(this,"Cases vides !","Completer toutes les cases svp !")
            }else{
                userRef().document(phone).get().addOnSuccessListener {
                    if (it.exists()){
                        Progress(false, binding.button, binding.progress)
                        alert(this,"Incompatibilite !","Une boutique avec cette email existe, Veuillez entre un autre email plus sur !")
                    }else{
                        Progress(false, binding.button, binding.progress)
                        val email = "$phone@gmail.com"
                        auth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener {
                            val users = Users(
                                ""+phone,
                                ""+name,
                                ""+address,
                                ""+pass,
                                ""+devise)
                            userRef().document(phone).set(users).addOnSuccessListener {
                                dbHelper.addUser(users)
                                startActivity(Intent(this,MainActivity::class.java))
                                finish()
                            }
                        }
                    }
                }
            }
        }
    }
}