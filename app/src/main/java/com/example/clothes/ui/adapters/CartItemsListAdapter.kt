package com.example.clothes.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.R
import com.example.clothes.firestoreclass.FirestoreClass
import com.example.clothes.models.Cart
import com.example.clothes.ui.activities.CartListActivity
import com.example.clothes.utils.Constants
import com.example.clothes.utils.GlideLoader


open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<Cart>,
    private val updateCartItems:Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {

            GlideLoader(context).loadProductPicture(model.image, holder.iv_cart_item_image)

            holder.tv_cart_item_title.text = model.title
            holder.tv_cart_item_price.text = "$${model.price}"
            holder.tv_cart_quantity.text = model.cart_quantity

            if (model.cart_quantity == "0") {
                holder.ib_remove_cart_item.visibility = View.GONE
                holder.ib_add_cart_item.visibility = View.GONE

                if (updateCartItems) {
                    holder.ib_delete_cart_item.visibility = View.VISIBLE
                } else {
                    holder.ib_delete_cart_item.visibility = View.GONE
                }

                holder.tv_cart_quantity.text =
                    context.resources.getString(R.string.lbl_out_of_stock)

                holder.tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSnackBarError
                    )
                )
            } else {

                if (updateCartItems) {
                    holder.ib_remove_cart_item.visibility = View.VISIBLE
                    holder.ib_add_cart_item.visibility = View.VISIBLE
                    holder.ib_delete_cart_item.visibility = View.VISIBLE
                } else {

                    holder.ib_remove_cart_item.visibility = View.GONE
                    holder.ib_add_cart_item.visibility = View.GONE
                    holder.ib_delete_cart_item.visibility = View.GONE
                }

                holder.tv_cart_quantity.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorSecondaryText
                    )
                )
            }

            holder.ib_remove_cart_item.setOnClickListener {

                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {

                    val cartQuantity: Int = model.cart_quantity.toInt()

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    // Show the progress dialog.

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }

            holder.ib_add_cart_item.setOnClickListener {

                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {

                    val itemHashMap = HashMap<String, Any>()

                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    // Show the progress dialog.
                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }

            holder.ib_delete_cart_item.setOnClickListener {

                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.id)
            }
        }
    }

    /**
     * Gets the number of items in the list
     */
    override fun getItemCount(): Int {
        return list.size
    }



    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val iv_cart_item_image:ImageView = itemView.findViewById(R.id.iv_cart_item_image)
        val tv_cart_item_title:TextView = itemView.findViewById(R.id.tv_cart_item_title)
        val tv_cart_item_price:TextView = itemView.findViewById(R.id.tv_cart_item_price)
        val ib_remove_cart_item:ImageButton = itemView.findViewById(R.id.ib_remove_cart_item)
        val ib_add_cart_item:ImageButton = itemView.findViewById(R.id.ib_add_cart_item)
        val tv_cart_quantity:TextView = itemView.findViewById(R.id.tv_cart_quantity)
        val ib_delete_cart_item:ImageButton =itemView.findViewById(R.id.ib_delete_cart_item)



    }
}