package com.abahz.abbusiness.fragments

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abahz.abbusiness.MainActivity
import com.abahz.abbusiness.R
import com.abahz.abbusiness.adapters.FactureAdapter
import com.abahz.abbusiness.databinding.FragmentOrderBinding
import com.abahz.abbusiness.interfaces.ClickButtons
import com.abahz.abbusiness.interfaces.OnQuantityChangeListener
import com.abahz.abbusiness.utils.Constants.DAY_ONLY
import com.abahz.abbusiness.utils.Constants.MONTH_ONLY
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.Constants.MY_MONTH
import com.abahz.abbusiness.utils.Constants.MY_YEAR
import com.abahz.abbusiness.utils.DBHelper
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OrderFragment : Fragment(), ClickButtons {

    private lateinit var binding: FragmentOrderBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var calendar: Calendar
    private lateinit var count: OnQuantityChangeListener
    private lateinit var listener: DatePickerDialog.OnDateSetListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (context as MainActivity).searchToolbar(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        dbHelper = DBHelper(requireContext())
        calendar = Calendar.getInstance()

        // Initialisation de la variable `count`
        count = object : OnQuantityChangeListener {

            override fun onQuantityChanged(totalAmount: Double) {
                //TODO("Not yet implemented")
                Log.d("OrderFragment", "Quantité totale: $totalAmount")
            }
        }

        binding.recycler.hasFixedSize()
        binding.recycler.layoutManager = LinearLayoutManager(context)
        getAllData()  // Utilisation de count correctement initialisée

        binding.view1.visibility = View.GONE
        binding.selected.text = MY_YEAR
        binding.selected.isEnabled = false

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
            binding.recycler.adapter = FactureAdapter(requireContext(), dbHelper.getFactures(date), count)
        }

        return binding.root
    }

    private fun openDialog() {
        binding.selected.setOnClickListener {
            val dialog = DatePickerDialog(
                requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, listener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()
        }
    }

    private fun getDaily() {
        binding.recycler.adapter = FactureAdapter(requireContext(), dbHelper.getFactures(MY_DATE), count)
    }

    private fun getMonthly() {
        binding.recycler.adapter = FactureAdapter(requireContext(), dbHelper.getFactMonth(MY_MONTH), count)
    }

    private fun getAllData() {
        binding.recycler.adapter = FactureAdapter(requireContext(), dbHelper.getFactAll(), count)
    }

    override fun onPause() {
        super.onPause()
        (context as MainActivity).searchToolbar(false)
        binding.view1.visibility = View.GONE
    }

    override fun onClickSearch() {
        binding.view1.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
        getAllData()
    }
}

