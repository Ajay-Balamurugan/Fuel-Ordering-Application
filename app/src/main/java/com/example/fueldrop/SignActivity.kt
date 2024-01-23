package com.example.fueldrop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fueldrop.databinding.ActivityLoginBinding
import com.example.fueldrop.databinding.ActivitySignBinding
import com.example.fueldrop.model.userModel
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class SignActivity : AppCompatActivity() {

    private lateinit var  email: String
    private lateinit var  password: String
    private lateinit var  name: String
    private lateinit var  database: DatabaseReference
    private lateinit var  auth: FirebaseAuth

    private val binding: ActivitySignBinding by lazy{
        ActivitySignBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = Firebase.database.reference

        binding.createbutton.setOnClickListener {
            name = binding.username.text.toString()
            password = binding.password.text.toString()
            email = binding.email.text.toString()

            if(email.isBlank() || password.isBlank() || name.isBlank()){
                Toast.makeText(this,"Please Enter All Fields",Toast.LENGTH_SHORT).show();
            }
            else
            {
                createAccount(email,password)
            }

        }

        binding.alreadyhavebutton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
            task ->
            if(task.isSuccessful)
            {
                Toast.makeText(this,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                saveUserData()
                startActivity(Intent(this,LoginActivity::class.java))
                finish()
            }
            else{
                Toast.makeText(this,"Account Creation Failed",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData() {
        name = binding.username.text.toString()
        password = binding.password.text.toString()
        email = binding.email.text.toString()

        val user = userModel(name,email,password)
        val userId = FirebaseAuth.getInstance() .currentUser!!.uid
        database.child("user").child(userId).setValue(user)

    }
}