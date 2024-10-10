package com.abahz.abbusiness.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.abahz.abbusiness.AddActivity
import com.abahz.abbusiness.MainActivity
import com.abahz.abbusiness.adapters.ProductAdapter
import com.abahz.abbusiness.databinding.FragmentHomeBinding
import com.abahz.abbusiness.interfaces.AddToCart
import com.abahz.abbusiness.interfaces.ClickButtons
import com.abahz.abbusiness.models.Products
import com.abahz.abbusiness.utils.Constants.MY_DATE
import com.abahz.abbusiness.utils.DBHelper

class HomeFragment : Fragment(), ClickButtons {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var addToCart: AddToCart
    private lateinit var adapter: ProductAdapter
    private var originalList = ArrayList<Products>() // Liste complète des produits

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        dbHelper = DBHelper(requireContext())

        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(context)

        // Récupérer tous les produits de la base de données
        originalList = dbHelper.getProduct() as ArrayList<Products> // Assurez-vous d'utiliser un type mutable

        // Initialiser l'adaptateur avec la liste complète
        adapter = ProductAdapter(requireContext(), originalList, addToCart)
        binding.recycler.adapter = adapter

        setupClickListeners()
        setupSearchView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as? MainActivity)?.searchToolbar(false)
        binding.view2.visibility = View.GONE
    }

    private fun setupClickListeners() {
        binding.add.setOnClickListener {
            startActivity(Intent(context, AddActivity::class.java))
        }

        binding.back.setOnClickListener {
            (activity as? MainActivity)?.searchToolbar(false)
            binding.view2.visibility = View.GONE
        }
    }

    private fun setupSearchView() {
        binding.search2.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    Log.d("MY_LOG","list=====>$newText")
                    searchQuery(newText)
                } else {
                    restoreOriginalList() // Restaurer la liste complète
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    Log.d("MY_LOG","list2=====>$query")
                    searchQuery(query)
                }
                return true
            }
        })
    }

    private fun restoreOriginalList() {
        adapter.updateProductList(originalList)
    }

    private fun searchQuery(query: String) {
        val filteredList = dbHelper.searchProduct(query)
        adapter.updateProductList(filteredList)
    }

    override fun onClickSearch() {
        if (::binding.isInitialized) {
            binding.view2.visibility = View.VISIBLE
        } else {
            Log.e("HomeFragment", "binding n'est pas encore initialisé")
        }
    }

    override fun onPause() {
        super.onPause()
        (activity as? MainActivity)?.searchToolbar(false)
        binding.view2.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddToCart) {
            addToCart = context
        } else {
            throw RuntimeException("$context doit d'abord implémenter AddToCart")
        }
    }

    fun refreshProductList() {
        val list = dbHelper.getProduct()
        adapter.updateProductList(list)
    }
}


