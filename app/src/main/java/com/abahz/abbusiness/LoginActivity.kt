package com.abahz.abbusiness

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.abahz.abbusiness.databinding.ActivityLoginBinding
import com.abahz.abbusiness.models.Users
import com.abahz.abbusiness.utils.Constants.Progress
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.Constants.userRef
import com.abahz.abbusiness.utils.DBHelper
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        auth = FirebaseAuth.getInstance()
        binding.switchTo.setOnClickListener { startActivity(Intent(this,RegisterActivity::class.java)) }
        binding.button.setOnClickListener {
            Progress(true, binding.button, binding.progress)
            val phone = binding.phone.text.toString().trim()
            val pass = binding.pass.text.toString().trim()
            if (phone.isEmpty()||pass.isEmpty()){
                Progress(false, binding.button, binding.progress)
                alert(this,"Cases vides !","Completer toutes les cases svp !")
            }else{
                userRef().document(phone).get().addOnSuccessListener {
                    if (it.exists()){
                        val users = it.toObject(Users::class.java)
                        if (users!=null){
                            if (pass==users.pass){
                                val email = "$phone@gmail.com"
                                dbHelper.addUser(users)
                                auth.signInWithEmailAndPassword(email,pass).addOnSuccessListener {
                                    auth.currentUser
                                    Progress(false, binding.button, binding.progress)
                                    startActivity(Intent(this,MainActivity::class.java))
                                    finish()
                                }
                            }else{
                                Progress(false, binding.button, binding.progress)
                                alert(this,"Incompatibilite","Mot de passe incorrect, Veuillez tapez un mot de passe correct svp !")
                            }
                        }
                    }else{
                        Progress(false, binding.button, binding.progress)
                        alert(this,"Incompatibilite","Une boutique avec cette email n'existe pas, Veuillez tapez un email et un mot de passe correct svp !")
                    }
                }
            }
        }
    }
}