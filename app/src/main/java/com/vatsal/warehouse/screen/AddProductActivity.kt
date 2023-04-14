package com.vatsal.warehouse.screen

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vatsal.warehouse.MainActivity
import com.vatsal.warehouse.model.ProductData
import com.vatsal.warehouse.util.InternetConnection
import com.google.firebase.firestore.FirebaseFirestore
import com.vatsal.warehouse.R
import java.util.*

class AddProductActivity : AppCompatActivity() {
    lateinit var productName: EditText
    lateinit var productQty: EditText
    lateinit var productDescription: EditText
    lateinit var btnSubmite: Button
    private lateinit var db: FirebaseFirestore
    lateinit var progressBar: ProgressBar
    lateinit var id: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        //init method
        initMethod()
    }

    @SuppressLint("RestrictedApi")
    private fun initMethod() {
        db = FirebaseFirestore.getInstance()
        productName = findViewById(R.id.edProductName)
        productQty = findViewById(R.id.edQty)
        productDescription = findViewById(R.id.edDescription)
        progressBar = findViewById(R.id.progress_bar)
        supportActionBar!!.title = getString(R.string.addproduct)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#000000")))
        //setup edit text data
        if (this.intent.extras != null) {
            supportActionBar!!.title = getString(R.string.editproduct)
            productName.setText(intent.getStringExtra("name"))
            productQty.setText(intent.getIntExtra("qty", 0).toString())
            productDescription.setText(intent.getStringExtra("dec"))
            id = intent.getStringExtra("id").toString()
        }
        btnSubmite = findViewById(R.id.btnSubmit)
        if (this.intent.extras != null) {
            btnSubmite.setText(R.string.save)
        } else {
            btnSubmite.setText(R.string.submit)
        }
        btnSubmite.setOnClickListener {

            //check internet connection
            if (InternetConnection.checkConnectivity(this)) {

                //validation
                if (productName.text.toString().trim().isEmpty() && productQty.text.toString()
                        .trim().isEmpty() && productDescription.text.toString().trim().isEmpty()
                ) {
                    Toast.makeText(this, "Fill the all details.", Toast.LENGTH_SHORT).show()
                }
                //update and add data to firebase
                else {
                    progressBar.visibility = View.VISIBLE
                    if (intent.getStringExtra("id").toString() == "null") {
                        var idStr = UUID.randomUUID().toString()
                        addProductMethod(
                            idStr,
                            productName.text.toString().trim(),
                            Integer.parseInt(productQty.text.toString().trim()),
                            productDescription.text.toString().trim()
                        )
                    } else {
                        progressBar.visibility = View.VISIBLE
                        addProductMethod(
                            id,
                            productName.text.toString().trim(),
                            Integer.parseInt(productQty.text.toString().trim()),
                            productDescription.text.toString().trim()
                        )
                    }

                }
            } else {

                Toast.makeText(this, "Please check your network.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    //add data to firebase method
    private fun addProductMethod(
        id: String,
        productName: String,
        productQty: Int,
        productDes: String
    ) {
// Add a new document with a generated ID
        db.collection("user")
            .document(id).set(
                ProductData(
                    id, productName = productName,
                    productQty = productQty,
                    productDesc = productDes
                )
            )
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(this, "Successfully.", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    startActivity(Intent(this, MainActivity::class.java))
                } else {
                    Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                }

            }.addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}