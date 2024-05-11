package com.example.clothes.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.clothes.R
import com.example.clothes.firestoreclass.FirestoreClass
import com.example.clothes.models.Address
import com.example.clothes.models.Cart
import com.example.clothes.models.Order
import com.example.clothes.models.Product
import com.example.clothes.ui.adapters.CartItemsListAdapter
import com.example.clothes.utils.ClothesButton
import com.example.clothes.utils.ClothesTextView
import com.example.clothes.utils.ClothesTextViewBold
import com.example.clothes.utils.Constants

class CheckoutActivity : BaseActivity() {


    private lateinit var toolbar_checkout_activity: Toolbar

    private lateinit var tv_product_items: ClothesTextView
    private lateinit var rv_cart_list_items: RecyclerView
    private lateinit var tv_selected_address: ClothesTextView
    private lateinit var ll_checkout_address_details: LinearLayout
    private lateinit var tv_checkout_address_type: ClothesTextView
    private lateinit var tv_checkout_full_name: ClothesTextViewBold
    private lateinit var tv_checkout_address: ClothesTextView
    private lateinit var tv_checkout_additional_note: ClothesTextView
    private lateinit var tv_checkout_other_details: ClothesTextView
    private lateinit var tv_checkout_mobile_number: ClothesTextView
    private lateinit var tv_items_receipt: ClothesTextView
    private lateinit var tv_checkout_sub_total: ClothesTextView
    private lateinit var tv_checkout_shipping_charge: ClothesTextView
    private lateinit var tv_checkout_total_amount: ClothesTextViewBold
    private lateinit var ll_checkout_place_order: LinearLayout
    private lateinit var tv_payment_mode: ClothesTextViewBold
    private lateinit var btn_place_order: ClothesButton


    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<Cart>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_checkout)
        toolbar_checkout_activity = findViewById(R.id.toolbar_checkout_activity)
        tv_product_items = findViewById(R.id.tv_product_items)
        rv_cart_list_items = findViewById(R.id.rv_cart_list_items)
        tv_selected_address = findViewById(R.id.tv_selected_address)
        ll_checkout_address_details = findViewById(R.id.ll_checkout_address_details)
        tv_checkout_address_type = findViewById(R.id.tv_checkout_address_type)
        tv_checkout_full_name = findViewById(R.id.tv_checkout_full_name)
        tv_checkout_address = findViewById(R.id.tv_checkout_address)
        tv_checkout_additional_note = findViewById(R.id.tv_checkout_additional_note)
        tv_checkout_other_details = findViewById(R.id.tv_checkout_other_details)
        tv_checkout_mobile_number = findViewById(R.id.tv_checkout_mobile_number)
        tv_items_receipt = findViewById(R.id.tv_items_receipt)
        tv_checkout_sub_total = findViewById(R.id.tv_checkout_sub_total)
        tv_checkout_shipping_charge = findViewById(R.id.tv_checkout_shipping_charge)
        tv_checkout_total_amount = findViewById(R.id.tv_checkout_total_amount)
        ll_checkout_place_order = findViewById(R.id.ll_checkout_place_order)
        tv_payment_mode = findViewById(R.id.tv_payment_mode)
        btn_place_order = findViewById(R.id.btn_place_order)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_checkout_mobile_number.text = mAddressDetails?.mobileNumber
        }

        btn_place_order.setOnClickListener {
            placeAnOrder()
        }

        getProductList()

    }
    private fun setupActionBar() {

        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }

    /**
     * A function to get product list to compare the current stock with the cart items.
     */
    private fun getProductList() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    /**
     * A function to get the success result of product list.
     *
     * @param productsList
     */
    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        mProductsList = productsList

        getCartItemsList()
    }

    /**
     * A function to get the list of cart items in the activity.
     */
    private fun getCartItemsList() {

        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    /**
     * A function to notify the success result of the cart items list from cloud firestore.
     *
     * @param cartList
     */
    fun successCartItemsList(cartList: ArrayList<Cart>) {

        // Hide progress dialog.
        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        tv_checkout_sub_total.text = "$$mSubTotal"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        tv_checkout_shipping_charge.text = "$10.0"

        if (mSubTotal > 0) {
            ll_checkout_place_order.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"
        } else {
            ll_checkout_place_order.visibility = View.GONE
        }
    }

    /**
     * A function to prepare the Order details to place an order.
     */
    private fun placeAnOrder() {

        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "10.0", // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )

        FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
    }

    /**
     * A function to notify the success result of the order placed.
     */
    fun orderPlacedSuccess() {

        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
    }

    /**
     * A function to notify the success result after updating all the required details.
     */
    fun allDetailsUpdatedSuccessfully() {

        // Hide the progress dialog.
        hideProgressDialog()

        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


}