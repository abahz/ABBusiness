package com.abahz.abbusiness.worker

import android.content.Context
import android.os.Environment
import android.widget.Toast
import androidx.work.Worker
import androidx.work.WorkerParameters
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

class DownloadPDF(context: Context, params: WorkerParameters) : Worker(context, params) {
    private lateinit var dbHelper: DBHelper

    override fun doWork(): Result {
        dbHelper = DBHelper(applicationContext)
        try {
            val fileName = "$DAY_ONLY ${rapport()} ${MY_YEAR}.pdf"
            createPdf(fileName)
            return Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            return Result.failure()
        }
    }

    private fun createPdf(fileName: String) {
        val fontG = FontFactory.getFont(FontFactory.TIMES_ROMAN, 14F, Font.BOLD)
        val fontS = FontFactory.getFont(FontFactory.TIMES_ROMAN, 12F, Font.ITALIC)
        val fontGN = FontFactory.getFont(FontFactory.TIMES_ROMAN, 13F, Font.BOLD)

        val directory: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) ?: run {
            Toast.makeText(applicationContext, "Erreur lors de l'accès au répertoire de documents.", Toast.LENGTH_SHORT).show()
            return
        }
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

            val n = 0
            for (order in dbHelper.getOrderDate(MY_DATE)){
                val priceUn = order.total.toInt()/order.qty.toInt()
                table2.addCell("${n + 1}")
                table2.addCell(order.name)
                table2.addCell(order.qty.toString())
                table2.addCell(priceUn.toString()+" "+dbHelper.getShopDevise())
                table2.addCell(order.total.toString()+" "+dbHelper.getShopDevise())
            }

            val cell2 = PdfPCell(Paragraph("Total", fontG))
            cell2.colspan = 4
            table2.addCell(cell2)
            table2.addCell(Paragraph("${dbHelper.getTotalSales( MY_DATE)} ${dbHelper.getShopDevise()}",fontG))
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

            Toast.makeText(applicationContext, "PDF créé avec succès: $fileName", Toast.LENGTH_LONG).show()


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Erreur lors de la création du PDF.", Toast.LENGTH_SHORT).show()
        }
    }

}
