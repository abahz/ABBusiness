package com.abahz.abbusiness.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.Builder
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.abahz.abbusiness.R
import com.abahz.abbusiness.databinding.ItemNoteBinding
import com.abahz.abbusiness.fragments.ExpenseFragment
import com.abahz.abbusiness.fragments.HomeFragment
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.Constants.MY_MONTH
import com.abahz.abbusiness.utils.Constants.NOTES
import com.abahz.abbusiness.utils.Constants.PRODUCTS
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.Constants.notesRef
import com.abahz.abbusiness.utils.Constants.prodRef
import com.abahz.abbusiness.utils.Constants.reference
import com.abahz.abbusiness.utils.DBHelper

class NoteAdapter(val context:Context, private var list:ArrayList<Notes>):Adapter<NoteAdapter.NoteHolder>() {
    inner class NoteHolder(val binding:ItemNoteBinding):ViewHolder(binding.root)
    private lateinit var dbHelper:DBHelper

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
        return NoteHolder(ItemNoteBinding.inflate(LayoutInflater.from(context),parent,false))
    }

    override fun getItemCount(): Int =list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateNotesList(newList: ArrayList<Notes>) {
        list = newList
        notifyDataSetChanged()
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoteHolder, position: Int) {
        dbHelper = DBHelper(context)
        val notes = list[position]
        holder.binding.noteTitle.text = notes.client
        holder.binding.noteDesc.text = notes.note
        holder.binding.noteDate.text = notes.date
        holder.binding.notePrice.text = "Coût: ${notes.price} ${dbHelper.getShopDevise()}"

        holder.itemView.setOnLongClickListener {
            val options = arrayOf("Modifier", "Supprimer")
            Builder(context).setItems(options){ dialog, witch ->
                dialog.dismiss()
                when (witch){
                    0-> openEditDialog(notes,position)
                    1->{
                        list.remove(notes)
                        notifyItemRemoved(position)
                        dbHelper.deleteNote(notes.id)
                        notesRef().document(dbHelper.getShopUid()).collection(NOTES).document(notes.id).delete().addOnSuccessListener {
                        }
                    }
                }
            }.create().show()
            true
        }
    }

    private fun openEditDialog(notes: Notes,position: Int) {
        val view = LayoutInflater.from(context).inflate(R.layout.add_expense, null)
        val builder = Builder(context, R.style.FullScreenDialog).setView(view)
        val dialog = builder.create()
        dialog.show()
        val nameId = view.findViewById<EditText>(R.id.exp_name)
        val priceId = view.findViewById<EditText>(R.id.exp_amount)
        val noteId = view.findViewById<EditText>(R.id.exp_note)
        view.findViewById<ImageButton>(R.id.exp_back).setOnClickListener {
            dialog.dismiss()
        }
        nameId.setText(notes.client)
        noteId.setText(notes.note)
        priceId.setText(notes.price.toString())
        view.findViewById<Button>(R.id.exp_button).setOnClickListener {
            val name = nameId.text.toString().trim()
            val price = priceId.text.toString().trim()
            val note = noteId.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || note.isEmpty()) {
                alert(
                    context,
                    "Cases vides !",
                    "Veuillez completer toutes les cases pour fournir une piece comptable adequate !"
                )
            } else {
                dbHelper.editNotes(
                    ""+notes.id,
                    price = price.toLong(),
                    ""+note,
                     client = name,
                    ""+1)
                dialog.dismiss()
                val map = mapOf(
                    "client" to name,
                    "price" to price,
                    "note" to note,
                    "sync" to 0)
                notesRef().document(dbHelper.getShopUid()).collection(NOTES).document(notes.id).update(map)

                notes.price = price
                notes.note = note
                notes.client = name

                notifyItemChanged(position)

                (context as? ExpenseFragment)?.refreshNotes()
                dialog.dismiss()
            }
        }
    }
}