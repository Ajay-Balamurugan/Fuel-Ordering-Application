package com.example.fueldrop.Fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fueldrop.CongratsBottomSheet
import com.example.fueldrop.PayOutActivity
import com.example.fueldrop.R
import com.example.fueldrop.adapter.cart_item_adapter
import com.example.fueldrop.databinding.CartItemBinding
import com.example.fueldrop.databinding.FragmentCartBinding
import com.example.fueldrop.model.cartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var fuelNames: MutableList<String>
    private lateinit var fuelPrices: MutableList<String>
    private lateinit var fuelImages: MutableList<String>
    private lateinit var fuelQuantities: MutableList<Int>
    private lateinit var cartAdapter: cart_item_adapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCartBinding.inflate(inflater,container,false)

        auth = FirebaseAuth.getInstance()
        retrieveCartrItems()


        binding.proceedButton.setOnClickListener {
            //get Order Details
            getOrderDetails()
        }



        return binding.root
    }

    private fun getOrderDetails() {
        val orderIdReference :DatabaseReference = database.reference.child("user").child(userId).child("cartItems")
        val fuelName = mutableListOf<String>()
        val fuelPrice = mutableListOf<String>()
        val fuelImage = mutableListOf<String>()
        //get total quantity of all fuel items ordered
        val fuelQuantities = cartAdapter.getUpdatedItemsQuantities()

        orderIdReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(fuelSnapshot in snapshot.children){
                    //get cart items to respective list
                    val orderItems = fuelSnapshot.getValue(cartItem::class.java)
                    //add items details into list
                    orderItems?.fuelName?.let{fuelName.add(it)}
                    orderItems?.fuelPrice?.let{fuelPrice.add(it)}
                    orderItems?.fuelImage?.let{fuelImage.add(it)}
                    orderItems?.fuelQuantity?.let{fuelQuantities.add(it)}
                }
                orderNow(fuelName,fuelPrice,fuelImage,fuelQuantities)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(),"Failed to Register Order. Please Try Again",Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun orderNow(
        fuelName: MutableList<String>,
        fuelPrice: MutableList<String>,
        fuelImage: MutableList<String>,
        fuelQuantities: MutableList<Int>) {

        if(isAdded && context != null){
            val intent = Intent(requireContext(),PayOutActivity::class.java)
            intent.putExtra("FuelItemName", fuelName as ArrayList<String>)
            intent.putExtra("FuelItemPrice", fuelPrice as ArrayList<String>)
            intent.putExtra("FuelItemImage", fuelImage as ArrayList<String>)
            intent.putExtra("FuelItemQuantities", fuelQuantities as ArrayList<Int>)
            startActivity(intent)
        }

    }

    private fun retrieveCartrItems() {
        //database reference
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid?:""
        val fuelReference :DatabaseReference = database.reference.child("user").child(userId).child("cartItems")

        fuelNames = mutableListOf()
        fuelPrices = mutableListOf()
        fuelImages = mutableListOf()
        fuelQuantities = mutableListOf()

        //fetch data from database
        fuelReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(fuelSnapshot in snapshot.children){
                    val cartItems = fuelSnapshot.getValue(cartItem::class.java)
                    cartItems?.fuelName?.let { fuelNames.add(it) }
                    cartItems?.fuelPrice?.let { fuelPrices.add(it) }
                    cartItems?.fuelImage?.let { fuelImages.add(it) }
                    cartItems?.fuelQuantity?.let { fuelQuantities.add(it) }
                }
                setAdapter()
            }

            private fun setAdapter() {
                cartAdapter = cart_item_adapter(requireContext(),fuelNames,fuelPrices,fuelImages,fuelQuantities)
                binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
                binding.cartRecyclerView.adapter = cartAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,"Data Could Not Be Fetched",Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {


    }
}