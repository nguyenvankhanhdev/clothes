package com.example.clothes.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.R
import com.example.clothes.models.Product
import com.example.clothes.ui.activities.ProductDetailsActivity
import com.example.clothes.ui.fragment.ProductsFragment
import com.example.clothes.utils.ClothesTextView
import com.example.clothes.utils.ClothesTextViewBold
import com.example.clothes.utils.Constants
import com.example.clothes.utils.GlideLoader

open class MyProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<Product>,
    private val fragment: ProductsFragment,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            GlideLoader(context).loadProductPicture(model.image, holder.itemImage)
            holder.itemName.text = model.title
            holder.itemPrice.text = "$${model.price}"
            holder.deleteButton.setOnClickListener{
                fragment.deleteProduct(model.product_id)
            }
            holder.itemView.setOnClickListener {
                // Launch Product details screen.
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                context.startActivity(intent)
            }


        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val itemImage: ImageView = itemView.findViewById(R.id.iv_item_image)
        val itemName: ClothesTextViewBold = itemView.findViewById(R.id.tv_item_name)
        val itemPrice: ClothesTextView = itemView.findViewById(R.id.tv_item_price)
        val deleteButton: ImageButton = itemView.findViewById(R.id.ib_delete_product)
    }
}