package com.example.fueldrop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.fueldrop.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

class LoginActivity : AppCompatActivity() {
    private lateinit var  email: String
    private lateinit var  password: String
    private lateinit var  database: FirebaseDatabase
    private lateinit var  auth: FirebaseAuth

    private val binding:ActivityLoginBinding by lazy{
        ActivityLoginBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance()

        binding.loginbutton.setOnClickListener {
            email = binding.mail.text.toString()
            password = binding.pwd.text.toString()

            if(email.isBlank() || password.isBlank())
            {
                Toast.makeText(this,"Please Enter All Fields",Toast.LENGTH_SHORT).show()
            }
            else
            {
                logInAccount(email,password)
            }

        }

        binding.donthavebutton.setOnClickListener {
            val intent = Intent(this, SignActivity::class.java)
            startActivity(intent)
        }
    }

    private fun logInAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(this,"Successfully Logged in",Toast.LENGTH_SHORT).show()
                val user = auth.currentUser
                updateui(user)
            }
            else
            {
                Toast.makeText(this,"Wrong Email/Password",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateui(user: FirebaseUser?) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }
}