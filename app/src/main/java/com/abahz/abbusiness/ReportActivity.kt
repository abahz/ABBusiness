package com.abahz.abbusiness

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.abahz.abahzbusiness.adapter.ExpenseAdapter
import com.abahz.abbusiness.adapters.ProdAdapter
import com.abahz.abbusiness.adapters.ReOrderAdapter
import com.abahz.abbusiness.adapters.SalesAdapter
import com.abahz.abbusiness.databinding.ActivityReportBinding
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants.DAY_ONLY
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.Constants.MY_YEAR
import com.abahz.abbusiness.utils.Constants.rapport
import com.abahz.abbusiness.utils.DBHelper
import com.itextpdf.text.Document
import com.itextpdf.text.Font
import com.itextpdf.text.FontFactory
import com.itextpdf.text.PageSize
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfPCell
import com.itextpdf.text.pdf.PdfPTable
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream

class ReportActivity : AppCompatActivity() {
    private lateinit var binding: ActivityReportBinding
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        window.statusBarColor = ContextCompat.getColor(this, R.color.status)
        binding.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        initView()
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        binding.stockRv.hasFixedSize()
        binding.stockRv.layoutManager = LinearLayoutManager(this)
        binding.stockRv.adapter = ProdAdapter(this,dbHelper.getProduct())

        binding.venteRv.hasFixedSize()
        binding.venteRv.layoutManager = LinearLayoutManager(this)
        binding.venteRv.adapter = ReOrderAdapter(this,dbHelper.getReOrder(MY_DATE))

        binding.expRv.hasFixedSize()
        binding.expRv.layoutManager = LinearLayoutManager(this)
        binding.expRv.adapter = ExpenseAdapter(this,dbHelper.getNot())

        binding.reportTitle.text = "Rapport du $DAY_ONLY ${rapport()} $MY_YEAR"
        binding.stockTotal.text = "${dbHelper.getProdPriceTotal()} ${dbHelper.getShopDevise()}"
        binding.totalVente.text = "${dbHelper.getTotalSales(MY_DATE)} ${dbHelper.getShopDevise()}"


        binding.totalRevenue.text = "${dbHelper.getTotalSales( MY_DATE)} ${dbHelper.getShopDevise()}"
        binding.totalExpenses.text = "${dbHelper.getTotalExpenses(MY_DATE)} ${dbHelper.getShopDevise()}"
        binding.totalExp.text = "${dbHelper.getTotalExpenses( MY_DATE)} ${dbHelper.getShopDevise()}"
        binding.stockTotal.text = "${dbHelper.getProdPriceTotal()} ${dbHelper.getShopDevise()}"
        binding.totalReduction.text = "${dbHelper.getTotalReduction(MY_DATE)} ${dbHelper.getShopDevise()}"
        val theNet = dbHelper.getTotalSales(MY_DATE)-dbHelper.getTotalReduction(MY_DATE)
        binding.totalVenteNet.text = "$theNet ${dbHelper.getShopDevise()}"

        val net = dbHelper.getTotalSales( MY_DATE) - dbHelper.getTotalExpenses( MY_DATE)
        binding.netProfit.text = "$net ${dbHelper.getShopDevise()}"

        binding.printPDF.setOnClickListener {
            generatePDF()
        }
    }
    private fun generatePDF() {
        val fontG = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14F, Font.BOLD)
        val fontS = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12F, Font.ITALIC)
        val fontGN = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13F, Font.BOLD)

        val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) ?: run {
            Toast.makeText(this, "Erreur lors de l'accès au répertoire de documents.", Toast.LENGTH_SHORT).show()
            return
        }
        val fileName = "ABB $DAY_ONLY ${rapport()}"
        val file = File(directory, "$fileName.pdf")

        try {
            val document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(file))
            document.open()


            val paragraph = Paragraph()
            paragraph.add(Paragraph("Boutique : ${dbHelper.getShopName()}", fontG))
            paragraph.add(Paragraph("Adresse : ${dbHelper.getShopAddress()}", fontS))
            paragraph.add(Paragraph("Téléphone : ${dbHelper.getShopUid()} \n", fontS))
            paragraph.add(Paragraph("Rapport du $DAY_ONLY ${rapport()} $MY_YEAR \n\n", fontG))
            paragraph.add(Paragraph("1. Inventaire de stock du $DAY_ONLY ${rapport()} $MY_YEAR \n\n", fontGN))

            val table = PdfPTable(5)
            table.widthPercentage = 100f
            table.setWidths(floatArrayOf(0.4f,3f, 1f, 2f, 2f))
            table.addCell(Paragraph("No",fontG))
            table.addCell(Paragraph("Articles",fontG))
            table.addCell(Paragraph("Quantité",fontG))
            table.addCell(Paragraph("Prix unitaire",fontG))
            table.addCell(Paragraph("Prix total",fontG))

            for ((x, prod: Products) in dbHelper.getProduct().withIndex()) {
                table.addCell("${x + 1}")
                table.addCell(prod.name)
                table.addCell(prod.qty.toString())
                table.addCell("${prod.price} ${dbHelper.getShopDevise()}")
                table.addCell("${prod.total} ${dbHelper.getShopDevise()}")
            }

            val cell3 = PdfPCell(Paragraph("Total", fontG))
            cell3.colspan = 4
            table.addCell(cell3)
            table.addCell(Paragraph("${dbHelper.getProdPriceTotal()} ${dbHelper.getShopDevise()}",fontG))
            paragraph.add(table)

            paragraph.add(Paragraph("\n"))
            paragraph.add(Paragraph("2. Ventes totales du $DAY_ONLY ${rapport()} $MY_YEAR \n\n", fontGN))

            val table2 = PdfPTable(5)
            table2.widthPercentage = 100f
            table2.setWidths(floatArrayOf(0.5f,3f, 1f, 2f, 2f))
            table2.addCell(Paragraph("No",fontG))
            table2.addCell(Paragraph("Articles",fontG))
            table2.addCell(Paragraph("Quantité",fontG))
            table2.addCell(Paragraph("Prix unitaire",fontG))
            table2.addCell(Paragraph("Prix total",fontG))

            var n = 0
            for (order in dbHelper.getReOrder(MY_DATE)){
                n++
                val priceUn = order.price.toInt()/order.qty.toInt()
                table2.addCell("$n")
                table2.addCell(order.name)
                table2.addCell(order.qty.toString())
                table2.addCell(priceUn.toString()+" "+dbHelper.getShopDevise())
                table2.addCell(order.price.toString()+" "+dbHelper.getShopDevise())
            }
            val cell0 = PdfPCell(Paragraph("Total Equivant: ", fontG))
            cell0.colspan = 4
            table2.addCell(cell0)
            table2.addCell(Paragraph("${dbHelper.getTotalSales(MY_DATE)} ${dbHelper.getShopDevise()}",fontG))

            val theNet = dbHelper.getTotalSales(MY_DATE)-dbHelper.getTotalReduction(MY_DATE)
            val cell2 = PdfPCell(Paragraph("Reduction: ", fontG))
            cell2.colspan = 4
            table2.addCell(cell2)
            table2.addCell(Paragraph("${dbHelper.getTotalReduction(MY_DATE)} ${dbHelper.getShopDevise()}",fontG))

            // Essaie
            val cell4 = PdfPCell(Paragraph("Total: ", fontG))
            cell4.colspan = 4
            table2.addCell(cell4)
            table2.addCell(Paragraph("$theNet ${dbHelper.getShopDevise()}",fontG))
            paragraph.add(table2)


            paragraph.add(Paragraph("\n"))
            paragraph.add(Paragraph("3. Dépenses totales du $DAY_ONLY ${rapport()} $MY_YEAR \n\n", fontGN))

            val table3 = PdfPTable(4)
            table3.widthPercentage = 100f
            table3.setWidths(floatArrayOf(0.4f,3f, 3f, 2f))
            table3.addCell(Paragraph("No",fontG))
            table3.addCell(Paragraph("Personnes",fontG))
            table3.addCell(Paragraph("Notes",fontG))
            table3.addCell(Paragraph("Montant",fontG))
            for ((m, expenses: Notes) in dbHelper.getNotes( MY_DATE).withIndex()) {
                table3.addCell("${m + 1}")
                table3.addCell(expenses.client)
                table3.addCell(expenses.note)
                table3.addCell(expenses.price.toString()+" "+dbHelper.getShopDevise())
            }
            val cell = PdfPCell(Paragraph("Total", fontG))
            cell.colspan = 3
            table3.addCell(cell)
            table3.addCell("${dbHelper.getTotalExpenses(MY_DATE)} ${dbHelper.getShopDevise()}")
            paragraph.add(table3)

            paragraph.add(Paragraph("\n"))
            paragraph.add(Paragraph("4. Résultat financier du $DAY_ONLY ${rapport()} $MY_YEAR \n\n", fontGN))


            val net = dbHelper.getTotalSales( MY_DATE) - dbHelper.getTotalExpenses( MY_DATE)
            paragraph.add(Paragraph("Revenus tautaux: ${dbHelper.getTotalSales( MY_DATE)} ${dbHelper.getShopDevise()}",fontG))
            paragraph.add(Paragraph("Dépenses tautaux: ${dbHelper.getTotalExpenses( MY_DATE)} ${dbHelper.getShopDevise()}",fontG))
            paragraph.add(Paragraph("Bénéfice net: $net ${dbHelper.getShopDevise()}",fontG))


            document.add(paragraph)

            document.close()

            Toast.makeText(this, "PDF créé avec succès: $fileName", Toast.LENGTH_LONG).show()

            openPDF(file)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Erreur lors de la création du PDF.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openPDF(file: File) {
        try {
            val uri: Uri = FileProvider.getUriForFile(this, "com.abahz.abahz.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}