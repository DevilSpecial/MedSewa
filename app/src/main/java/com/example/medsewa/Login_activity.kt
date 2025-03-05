package com.example.medsewa

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medsewa.databinding.ActivityLoginBinding
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // **Check if user is already logged in**
        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPref.getString("email", null)

        if (savedEmail != null) {
            // If user data exists, go directly to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnSignup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnLogin.setOnClickListener {
            checkUser()
        }
    }

    private fun checkUser() {
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.password.text.toString().trim()

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        // Query Firestore to find user by email
        db.collection("patients").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val document = documents.documents[0] // Get the first matching document
                    val storedPassword = document.getString("password")

                    if (storedPassword == password) {
                        // **Fetch user details**
                        val name = document.getString("name") ?: ""
                        val number = document.getString("number") ?: ""

                        // **Save user details in SharedPreferences**
                        val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putString("email", email)
                            putString("name", name)
                            putString("number", number)
                            putString("password", password)
                            apply()
                        }

                        Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Incorrect password!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User not found! Kindly SignUp", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error fetching user data", Toast.LENGTH_SHORT).show()
            }
    }
}
