package com.example.fueldrop.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.fueldrop.R
import com.example.fueldrop.databinding.CartItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class cart_item_adapter (
    private val context: Context,
    private val cartItemNames:MutableList<String>,
    private val cartItemPrices:MutableList<String>,
    private var cartItemImages:MutableList<String>,
    private val cartItemQuantity:MutableList<Int>
): RecyclerView.Adapter<cart_item_adapter.cartViewHolder>() {
    //Create Firebase Instance
    private val auth = FirebaseAuth.getInstance()

    init{
        //initialize firebase
        val databse = FirebaseDatabase.getInstance()
        val userid = auth.currentUser?.uid?:""

        val cartItemNumber = cartItemNames.size

        itemQuantities = IntArray(cartItemNumber){1}
        cartItemsReference = databse.reference.child("user").child(userid).child("cartItems")
    }
    companion object{
        private var itemQuantities: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cartViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return cartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: cartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = cartItemNames.size


    fun getUpdatedItemsQuantities(): MutableList<Int> {
        val itemQuantity = mutableListOf<Int>()
        itemQuantity.addAll(cartItemQuantity)
        return itemQuantity
    }

    inner class cartViewHolder(private val binding: CartItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
        binding.apply {
            val quantity = itemQuantities[position]
            cartitemname.text = cartItemNames[position]
            cartitemprice.text = cartItemPrices[position]
            cartitemimage.setImageResource(R.drawable.fuel)
            cartitemquantity.text = quantity.toString()


            minusbutton.setOnClickListener {
                decreaseQuantity(position)
            }

            plusbutton.setOnClickListener {
                increaseQuantity(position)
            }

            deletebutton.setOnClickListener {
                val itemPosition = adapterPosition
                if(itemPosition != RecyclerView.NO_POSITION){
                    deleteitem(itemPosition)
                }
            }

        }
        }
        private fun decreaseQuantity(position: Int){
            if(itemQuantities[position]>1){
                itemQuantities[position]--
                cartItemQuantity[position] = itemQuantities[position]
                binding.cartitemquantity.text = itemQuantities[position].toString()
            }
        }

        private fun increaseQuantity(position: Int){
            if(itemQuantities[position]<10){
                itemQuantities[position]++
                cartItemQuantity[position] = itemQuantities[position]
                binding.cartitemquantity.text = itemQuantities[position].toString()
            }
        }

        private fun deleteitem(position: Int){
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve){uniqueKey ->
                if(uniqueKey != null){
                    removeItem(position,uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if(uniqueKey != null){
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cartItemNames.removeAt(position)
                    cartItemImages.removeAt(position)
                    cartItemPrices.removeAt(position)
                    cartItemQuantity.removeAt(position)
                    Toast.makeText(context,"Item Removed Succesfully",Toast.LENGTH_SHORT)
                    //update item quantity
                    itemQuantities = itemQuantities.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position,cartItemNames.size)
                }.addOnFailureListener {
                    Toast.makeText(context,"Failed to Delete Item",Toast.LENGTH_SHORT)
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete:(String?) -> Unit){
            cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    var uniqueKey:String?=null
                    //loop for snapshot children
                    snapshot.children.forEachIndexed { index, dataSnapshot ->
                        if(index == positionRetrieve){
                            uniqueKey = dataSnapshot.key
                            return@forEachIndexed
                        }
                    }
                    onComplete(uniqueKey)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}

