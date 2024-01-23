package com.example.fueldrop.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fueldrop.R
import com.example.fueldrop.databinding.MenuFuelItemBinding
import com.example.fueldrop.model.cartItem
import com.example.fueldrop.model.menuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MenuAdapter(private val menuItemsName: List<menuItem>,private val menuItemsPrice: List<menuItem>,private val requireContext: Context) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuFuelItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MenuViewHolder(binding)
    }



    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItemsName.size

    inner class MenuViewHolder(private val binding: MenuFuelItemBinding): RecyclerView.ViewHolder(binding.root) {
        private lateinit var auth: FirebaseAuth
        fun bind(position: Int) {
            binding.apply {
                menuFuelName.text = menuItemsName[position].fuelname
                menuFuelPrice.text = menuItemsPrice[position].fuelprice
                menuFuelImage.setImageResource(R.drawable.fuel)
                auth = FirebaseAuth.getInstance()

                //add to cart functionality
                binding.addtocart.setOnClickListener {
                    val database = FirebaseDatabase.getInstance().reference
                    val userId = auth.currentUser?.uid?:""
                    //create a cartItem object
                    val cartItem = cartItem(menuItemsName[position].fuelname,menuItemsPrice[position].fuelprice,menuFuelImage.toString(),1)
                    //save the fuel item to database
                    database.child("user").child(userId).child("cartItems").push().setValue(cartItem).addOnSuccessListener {
                        Toast.makeText(requireContext,"Item Added to Cart",Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(requireContext,"Unable to Add Item to Cart",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
