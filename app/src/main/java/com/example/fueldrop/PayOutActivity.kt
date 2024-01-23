package com.example.fueldrop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.fueldrop.databinding.ActivityPayOutBinding
import com.example.fueldrop.model.orderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PayOutActivity : AppCompatActivity() {
    lateinit var binding:ActivityPayOutBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var name: String
    private lateinit var address: String
    private lateinit var phone: String
    private lateinit var total: String
    private lateinit var fuelItemName: ArrayList<String>
    private lateinit var fuelItemPrice: ArrayList<String>
    private lateinit var fuelItemImage: ArrayList<String>
    private lateinit var fuelItemQuantities: ArrayList<Int>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userId :String



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPayOutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //initialize firebase
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().getReference()

        //set the user details in payout activity
        setUserData()


        //calculating total price of the order
        //val intent = intent
        fuelItemName  = intent.getStringArrayListExtra("FuelItemName") as ArrayList<String>
        fuelItemPrice = intent.getStringArrayListExtra("FuelItemPrice") as ArrayList<String>
        fuelItemImage = intent.getStringArrayListExtra("FuelItemImage") as ArrayList<String>
        fuelItemQuantities = intent.getIntegerArrayListExtra("FuelItemQuantities") as ArrayList<Int>

        val totalAmount: String =  "₹ "+ calculateTotalAmount().toString()
        binding.totalAmount.isEnabled = false
        binding.totalAmount.setText(totalAmount)

        binding.backbutton.setOnClickListener {
            finish()
        }



        binding.PlaceMyOrder.setOnClickListener {
            //store the order in database
            name = binding.name.text.toString().trim()
            address = binding.address.text.toString().trim()
            phone = binding.phone.text.toString().trim()
            total = binding.totalAmount.text.toString()
            if(name.isBlank() || address.isBlank() || phone.isBlank()){
                Toast.makeText(this,"Please Fill All Details",Toast.LENGTH_SHORT).show()
            }
            else{
                placeOrder()
            }

        }
    }

    private fun placeOrder() {
        userId = auth.currentUser?.uid?:""
        val time = System.currentTimeMillis()
        val itempushkey = databaseReference.child("OrderDetails").push().key
        val orderDetails = orderDetails(userId,name,fuelItemName,fuelItemPrice,fuelItemQuantities,address,phone,time,itempushkey,total)
        val orderReference =  databaseReference.child("OrderDetails").child(itempushkey!!)
        orderReference.setValue(orderDetails).addOnSuccessListener {
            val bottomsheetdialog = CongratsBottomSheet()
            bottomsheetdialog.show(supportFragmentManager,"Test")
            removeItemFromCart()
        }.addOnFailureListener {
            Toast.makeText(this,"Failed to Place Order",Toast.LENGTH_SHORT)
        }
    }

    private fun removeItemFromCart() {
        val cartItemsReference = databaseReference.child("user").child(userId).child("cartItems")
        cartItemsReference.removeValue()
    }

    private fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for(i in 0 until fuelItemPrice.size){
            var price = fuelItemPrice[i]
            val lastChar = price.last()
            val priceIntValue = if(lastChar == '₹'){
                price.dropLast(1).toInt()
            }else
            {
                price.toInt()
            }
            var quantity = fuelItemQuantities[i]
            totalAmount += priceIntValue * quantity
        }
        return totalAmount
    }

    private fun setUserData() {
        val user = auth.currentUser
        if(user != null){
            val userid = user.uid
            val userReference = databaseReference.child("user").child(userid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists()){
                        val username = snapshot.child("name").getValue(String::class.java)?:""
                        val useraddress = snapshot.child("address").getValue(String::class.java)?:""
                        val userphone = snapshot.child("phone").getValue(String::class.java)?:""
                        binding.apply {
                            name.setText(username)
                            address.setText(useraddress)
                            phone.setText(userphone)
                        }
                    }

                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }
    }
}