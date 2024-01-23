package com.example.fueldrop.adapter

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fueldrop.databinding.FuelItemBinding
import com.example.fueldrop.model.cartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class fuel_item_adapter(private val items: List<String>, private val price:List<String>, private val image:List<Int>) : RecyclerView.Adapter<fuel_item_adapter.FuelViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FuelViewHolder {
        return FuelViewHolder(FuelItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: FuelViewHolder, position: Int) {
        val item = items[position]
        val images = image[position]
        val price = price[position]
        holder.bind(item,price,images)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class FuelViewHolder (private val binding: FuelItemBinding) : RecyclerView.ViewHolder(binding.root){
        private val imagesView = binding.imageView5
        fun bind(item: String, price: String, images: Int) {
            binding.fuelname.text = item
            binding.price.text = price
            imagesView.setImageResource(images)
        }
    }
}