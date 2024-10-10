package com.abahz.abbusiness

class EditInvoiceActivity  {
   /* private lateinit var binding: ActivityEditInvoiceBinding
    private lateinit var dbHelper: DBHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditInvoiceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        dbHelper = DBHelper(this)
        val intent = intent
        val factures = intent.getSerializableExtra("model") as Factures?
        if (factures!=null){
            binding.cardRv.setHasFixedSize(true)
            binding.cardRv.layoutManager = LinearLayoutManager(this)
            getInvoices(factures)
            binding.addOrder.setOnClickListener { editOrder(factures) }
        }else{
            Toast.makeText(this, "Nadaa", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editOrder(factos: Factures) {
        var totalFacture = 0.0
        val reduc = binding.reduc.text.toString()
        val client = binding.client.text.toString()
        if (client.isEmpty()){
            alert(this,"Nom du client", "Ajouter le nom du client svp")
        }else{
            if (binding.reduc.text.isEmpty()){
                binding.reduc.setText("0")
            }else{
                for (carts in dbHelper.getOrder(factos.date,factos.client)){
                    val orders = Orders(
                        ""+ carts.id,
                        ""+carts.name,
                        ""+client,
                        ""+ factos.date,
                        qty = carts.qty,
                        total = carts.total,
                        ""+1)
                    dbHelper.addOrder(orders)
                    val myQty = (dbHelper.getProdQtyStock(carts.name) - carts.qty.toDouble())
                    val total = myQty * dbHelper.getProdPriceUnit(carts.name)
                    dbHelper.updateQty(carts.name, myQty.toString(), total.toString())
                    dbHelper.deleteAllCart()
                    totalFacture += carts.total.toDouble()
                }
                val totalNet = totalFacture-reduc.toDouble()
                val factures = Factures(
                    ""+client,
                    total = totalNet.toString(),
                    reduction =   reduc,
                    ""+ factos.date,
                    ""+ factos.month,
                    ""+1,
                    ""+factos.id)
                dbHelper.addFacture(factures)
                startActivity(Intent(this,MainActivity::class.java))
                finish()
            }
        }
    }

    private fun getInvoices(factures: Factures) {
        binding.empty.visibility = View.GONE
        binding.cardRv.visibility = View.VISIBLE
        binding.bottom.visibility = View.VISIBLE
        binding.cardRv.adapter = OrderCartAdapter(this,dbHelper.getOrder(factures.date,factures.client),this)
    }

    override fun onQuantityChanged(totalAmount: Double) {
        binding.total.text = "$totalAmount ${dbHelper.getShopDevise()}"
    }

    override fun onResume() {
        super.onResume()
        val intent = intent
        val factures = intent.getSerializableExtra("model") as Factures?
        if (factures!=null){
            getInvoices(factures)
            binding.total.text = "${factures.total} ${dbHelper.getShopDevise()} "
            binding.client.setText(factures.client)
            binding.reduc.setText(factures.reduction)
        }else{
            Toast.makeText(this, "Nadaa", Toast.LENGTH_SHORT).show()
        }
    }*/
}