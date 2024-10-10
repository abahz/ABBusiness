package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.Manifest
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abahz.abbusiness.adapters.OrderAdapter
import com.abahz.abbusiness.databinding.ActivityInvoiceBinding
import com.abahz.abbusiness.models.Factures
import com.abahz.abbusiness.utils.Constants.DATE_COMPLETE
import com.abahz.abbusiness.utils.Constants.MY_YEAR
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.DBHelper
import com.dantsu.escposprinter.EscPosPrinter
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections

class InvoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceBinding
    private lateinit var dbHelper: DBHelper
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        window.statusBarColor = ContextCompat.getColor(this, R.color.status)

        val intent = intent
        val factures = intent.getSerializableExtra("model") as Factures
        binding.invMyDate.text = factures.date
        binding.invoiceRv.setHasFixedSize(true)
        binding.invoiceRv.layoutManager = LinearLayoutManager(this)
        binding.invoiceRv.adapter = OrderAdapter(this,dbHelper.getOrder(factures.date,factures.client))
        binding.invTotal.text = "${factures.total} ${dbHelper.getShopDevise()}"
        binding.invCustomer.text = "Mr/Mdm/Org: ${factures.client}"
        binding.invPhone.text = dbHelper.getShopUid()
        binding.invShop.text = dbHelper.getShopName()
        binding.invAddress.text = dbHelper.getShopAddress()

        binding.button.setOnClickListener { printInvoice(factures) }
    }

    private fun printInvoice(factures: Factures) {
        try {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.BLUETOOTH) !=(PackageManager.PERMISSION_GRANTED)){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.BLUETOOTH),100)
            }else{
                val connection = BluetoothPrintersConnections.selectFirstPaired()
                if (connection!=null){
                    val printer = EscPosPrinter(connection,203,48f,32)
                    var text = ""
                    text+= "[L]<b><font size ='wide' >FACTURE No: "+factures.id+ " /"+MY_YEAR+"</font></b>\n"+
                            "[L]<b><font size ='normal' >"+dbHelper.getShopName().uppercase()+"</font></b>\n"+
                            "[L]<b><font size ='normal' >"+dbHelper.getShopUid()+"</font></b>\n"+
                            "[L]<b><font size ='normal' >"+dbHelper.getShopAddress()+"</font></b>\n"+"\n"+
                            "[L]<b><font size ='normal' >Mr/Mdm/Org: ${factures.client.uppercase()}</font></b>\n"+
                            "[C]--------------------------------\n"+
                            "[L]<b>Description " + "[R]Qte/Prix unit.</b>\n"+
                            "[C]--------------------------------\n"
                    for (order in dbHelper.getOrder(factures.date,factures.client)){
                        val prixUnitaire = order.total.toDouble()/order.qty.toDouble()

                        text+= "[L]<b><font size ='normal' >"+order.name+" [R]"+order.qty+ " x "+prixUnitaire+" "+dbHelper.getShopDevise() +"\n"

                    }
                    text+= "[C]--------------------------------\n"+
                            "[L]<b>REDUCTION: " +"[R]"+ factures.reduction +" "+ dbHelper.getShopDevise()+ "</b>\n" +
                            "[L]<b>TOTAL: " +"[R]"+ factures.total +" "+ dbHelper.getShopDevise()+ "</b>\n" +
                            "[C]--------------------------------\n"+
                            "[C]" + DATE_COMPLETE+ "\n"
                    printer.printFormattedText(text)
                }
            }
        }catch (e:Exception){
            alert(this,"Pas d'association !","Activez Bluetooth et/ou allumer votre imprimante mobile !")
        }
    }
}