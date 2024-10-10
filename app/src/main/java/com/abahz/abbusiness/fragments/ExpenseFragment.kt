package com.abahz.abbusiness.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog.Builder
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.abahz.abbusiness.MainActivity
import com.abahz.abbusiness.R
import com.abahz.abbusiness.adapters.NoteAdapter
import com.abahz.abbusiness.databinding.FragmentExpenseBinding
import com.abahz.abbusiness.interfaces.ClickButtons
import com.abahz.abbusiness.models.Notes
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.Constants.MY_MONTH
import com.abahz.abbusiness.utils.Constants.MY_YEAR
import com.abahz.abbusiness.utils.Constants.alert
import com.abahz.abbusiness.utils.DBHelper
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class ExpenseFragment : Fragment(), ClickButtons {
    private lateinit var binding: FragmentExpenseBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var calendar: Calendar
    private lateinit var listener: DatePickerDialog.OnDateSetListener
    private lateinit var adapter: NoteAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (context as MainActivity).searchToolbar(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentExpenseBinding.inflate(inflater, container, false)

        calendar = Calendar.getInstance()
        dbHelper = DBHelper(requireContext())
        binding.recycler.hasFixedSize()
        binding.recycler.layoutManager = GridLayoutManager(context,2)
        getAllData()

        binding.view1.visibility = View.GONE
        binding.selected.text = MY_YEAR
        binding.selected.isEnabled = false

        adapter = NoteAdapter(requireContext(),dbHelper.getNot())
        binding.back.setOnClickListener {
            (activity as? MainActivity)?.searchToolbar(false)
            binding.view1.visibility = View.GONE
        }

        binding.chip.check(R.id.all)
        binding.chip.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val chip: Chip? = group.findViewById(checkedIds[0])
                chip?.let {
                    when (it.id) {
                        R.id.all -> {
                            binding.selected.text = MY_YEAR
                            getAllData()
                            binding.selected.isEnabled = false
                        }
                        R.id.monthly -> {
                            binding.selected.text = MY_MONTH
                            binding.selected.isEnabled = false
                            getMonthly()
                        }
                        R.id.daily -> {
                            binding.selected.text = MY_DATE
                            binding.selected.isEnabled = true
                            getDaily()
                            openDialog()
                        }
                    }
                }
            }
        }

        listener = DatePickerDialog.OnDateSetListener { picker, y, m, d ->
            calendar.set(Calendar.YEAR, y)
            calendar.set(Calendar.MONTH, m)
            calendar.set(Calendar.DAY_OF_MONTH, d)
            val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
            binding.selected.text = date
            binding.recycler.adapter = NoteAdapter(requireContext(), dbHelper.getNotes(date))
        }

        binding.add.setOnClickListener {
            addExpenseDialog()
        }
        return binding.root
    }

    private fun openDialog() {
        binding.selected.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, listener,
                calendar.get(Calendar.YEAR),  // Utilisation du calendar initialisé
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    private fun getDaily() {
        binding.recycler.adapter = NoteAdapter(requireContext(), dbHelper.getNotes(MY_DATE))
    }

    private fun getMonthly() {
        binding.recycler.adapter = NoteAdapter(requireContext(), dbHelper.getNoteMonth(MY_MONTH))
    }

    private fun addExpenseDialog() {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.add_expense, null)
        val builder = Builder(requireContext(), R.style.FullScreenDialog).setView(view)
        val dialog = builder.create()
        dialog.show()
        val nameId = view.findViewById<EditText>(R.id.exp_name)
        val priceId = view.findViewById<EditText>(R.id.exp_amount)
        val noteId = view.findViewById<EditText>(R.id.exp_note)
        view.findViewById<ImageButton>(R.id.exp_back).setOnClickListener {
            dialog.dismiss()
        }
        view.findViewById<Button>(R.id.exp_button).setOnClickListener {
            val name = nameId.text.toString().trim()
            val price = priceId.text.toString().trim()
            val note = noteId.text.toString().trim()

            if (name.isEmpty() || price.isEmpty() || note.isEmpty()) {
                alert(
                    requireContext(),
                    "Cases vides !",
                    "Veuillez completer toutes les cases pour fournir une piece comptable adequate !"
                )
            } else {
                val notes = Notes(
                    "" + System.currentTimeMillis(),
                    "" + name,
                    "" + note,
                    "" + MY_DATE,
                    "" + MY_MONTH,
                    price = price,
                    "" + 1)
                dbHelper.addNotes(notes)
                onResume()
                dialog.dismiss()
            }
        }
    }

    private fun getAllData() {
        binding.recycler.adapter = NoteAdapter(requireContext(), dbHelper.getNotAll())
    }

    override fun onClickSearch() {
        binding.view1.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        (context as MainActivity).searchToolbar(false)
        binding.view1.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        getAllData()

    }
    fun refreshNotes() {
        val list = dbHelper.getNot()
        adapter.updateNotesList(list)
    }
}