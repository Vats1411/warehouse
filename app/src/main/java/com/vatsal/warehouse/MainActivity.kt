package com.vatsal.warehouse

import TouchHelper
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.*
import com.vatsal.warehouse.adapter.ProductAdapter
import com.vatsal.warehouse.model.ProductData
import com.vatsal.warehouse.screen.AddProductActivity
import com.vatsal.warehouse.util.InternetConnection
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: ProductAdapter
    private lateinit var mList: MutableList<ProductData>
    private var query: Query? = null
    private val listenerRegistration: ListenerRegistration? = null
    private lateinit var fabAddProduct: FloatingActionButton
    private lateinit var noInternet: TextView
    private lateinit var noDataFound: TextView
    private lateinit var etSearch: EditText
    lateinit var progressBar: ProgressBar
    private lateinit var btnBluetooth: Button
    private lateinit var searchList: MutableList<ProductData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // define firebase fireStore instance
        db = FirebaseFirestore.getInstance()

        // find views by their ids
        noDataFound = findViewById(R.id.tvNoData)
        noInternet = findViewById(R.id.tvNoInternet)
        fabAddProduct = findViewById(R.id.fabAddProduct)
        progressBar = findViewById(R.id.progress_bar)
        btnBluetooth = findViewById(R.id.btn_bluetooth)

        btnBluetooth.setOnClickListener {
            val addProductIntent = Intent(this, BluetoothActivity::class.java)
            startActivity(addProductIntent)
        }

        // action bar
        supportActionBar!!.title = getString(R.string.app_name)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))

        // fab btn click listener
        fabAddProduct.setOnClickListener {
            Log.e("TAG", "onCreate: ${mList.size}" )
            if(mList.size < 10){
                val addProductIntent = Intent(this, AddProductActivity::class.java)
                startActivity(addProductIntent)
            }else{
                Toast.makeText(applicationContext, "Please purchase annual subscription plan", Toast.LENGTH_SHORT).show()
            }
        }

        // recycler view setup
        recyclerView = findViewById(R.id.recycerlview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        mList = ArrayList()
        adapter = ProductAdapter(this@MainActivity, mList)

        if (InternetConnection.checkConnectivity(this)) {
            val itemTouchHelper = ItemTouchHelper(TouchHelper(adapter))
            itemTouchHelper.attachToRecyclerView(recyclerView)
            showData()
            recyclerView.adapter = adapter
            recyclerView.visibility = View.VISIBLE
            noInternet.visibility = View.GONE
            noDataFound.visibility = View.GONE

        } else {
            recyclerView.visibility = View.GONE
            noInternet.visibility = View.VISIBLE
            noDataFound.visibility = View.GONE
        }

        etSearch = findViewById(R.id.et_search)
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(
                charSequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                // using textWatcher we don't need to fetch text from edit text
                if (charSequence.toString().isNotEmpty()) {
                    // fetch all the data from user collections
                    query = db.collection("user")
                    showAdapter(query!!)
                } // This is used as if user erases the characters in the search field.
                else {
                    val reference: CollectionReference = db.collection("user")
                    // fetch product name from user collection
                    query = reference.orderBy("productName").startAt(charSequence.toString().trim())
                        .endAt(
                            charSequence.toString().trim() + "\uf8ff"
                        ) // name - the field for which you want to make search
                    showAdapter(query!!)
                }
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    // below function is used to show the data which get from fireStore
    fun showAdapter(q1: Query) {
        mList.clear()
        searchList = ArrayList()

        q1.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                for (document in task.result) {
                    val model: ProductData = document.toObject(ProductData::class.java)
                    Log.e("TAG", "showAdapter: ${model.productName}")
                    if (model.productName!!.lowercase()
                            .trim().startsWith(etSearch.text.toString().lowercase().trim())) {
                        searchList.add(model)
                    }
                }

                adapter = ProductAdapter(this@MainActivity, searchList)
                recyclerView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        this.recreate()
    }

    private fun showData() {
        progressBar.visibility = View.VISIBLE
        query = db.collection("user")
        query!!.addSnapshotListener { value, _ ->
            for (documentChange in value?.documentChanges!!) {
                if (documentChange.type === DocumentChange.Type.ADDED) {
                    val productDataModel: ProductData =
                        documentChange.document.toObject(ProductData::class.java)
                    mList.add(productDataModel)
                    adapter.notifyDataSetChanged()
                }
            }
            if (mList.size == 0) {
                recyclerView.visibility = View.GONE
                noInternet.visibility = View.GONE
                noDataFound.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                noInternet.visibility = View.GONE
                noDataFound.visibility = View.GONE
            }
            progressBar.visibility = View.GONE
            listenerRegistration?.remove()
        }
    }

    fun visible(view: View) {}
    fun list(view: View) {}
    fun off(view: View) {}
}