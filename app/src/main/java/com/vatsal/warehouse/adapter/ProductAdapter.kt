package com.vatsal.warehouse.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vatsal.warehouse.MainActivity
import com.vatsal.warehouse.model.ProductData
import com.vatsal.warehouse.screen.AddProductActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.vatsal.warehouse.R

class ProductAdapter(mainActivity: MainActivity, todoList: MutableList<ProductData>) :
    RecyclerView.Adapter<ProductAdapter.MyViewHolder>() {
    private var productDataList: MutableList<ProductData>
    private val activity: MainActivity
    private var firestore: FirebaseFirestore? = null

    init {
        this.productDataList = todoList
        activity = mainActivity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        firestore = FirebaseFirestore.getInstance()
        return MyViewHolder(view)
    }

    fun deleteTask(position: Int) {
        val productData: ProductData = productDataList[position]
        firestore!!.collection("user").document(productData.id.toString()).delete()
        productDataList.removeAt(position)
        notifyItemRemoved(position)
    }

    val context: Context
        get() = activity

   fun editTask(position: Int) {
       val productDataModel: ProductData = productDataList[position]
        val intent=Intent(context, AddProductActivity::class.java)
       intent.putExtra("dec",productDataModel.productDesc)
       intent.putExtra("name",productDataModel.productName)
       intent.putExtra("id", productDataModel.id)
       intent.putExtra("qty", Integer.parseInt(productDataModel.productQty.toString()))
       context.startActivity(intent)
        //val addNewTask = AddNewTask()
        //addNewTask.setArguments(bundle)
        //addNewTask.show(activity.supportFragmentManager, addNewTask.getTag())
   }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val productModel: ProductData = productDataList[position]
        holder.productNameTv.text = productModel.productName
        holder.productDesTv.text = productModel.productDesc
        holder.productqtyTv.text = productModel.productQty.toString()
    }

    override fun getItemCount(): Int {
        return productDataList.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productNameTv: TextView
        var productDesTv: TextView
        var productqtyTv: TextView
        init {
            productNameTv = itemView.findViewById(R.id.idProductName)
            productDesTv = itemView.findViewById(R.id.tvProductDes)
            productqtyTv = itemView.findViewById(R.id.tvProductQty)
        }
    }
}
