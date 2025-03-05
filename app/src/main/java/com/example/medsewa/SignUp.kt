package com.example.medsewa

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.medsewa.DataClasses.Patient
import com.example.medsewa.databinding.ActivitySignUpBinding
import com.google.firebase.firestore.FirebaseFirestore

class SignUp : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private val db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        binding.signupButton.setOnClickListener {
            startSignup()
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun startSignup() {
        val name = binding.tvName.text.toString().trim()
        val email = binding.tvEmail.text.toString().trim()
        val password = binding.tvPassword.text.toString().trim()
        val number = binding.tvNumber.text.toString().trim()

        if (!validateInputs(name, email, number, password)) {
            Toast.makeText(this, "Please correct the errors above.", Toast.LENGTH_SHORT).show()
            return // Stops the signup process if validation fails
        }

        // Check if email or number already exists
        db.collection("patients")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { emailDocs ->
                if (!emailDocs.isEmpty) {
                    Toast.makeText(this, "Email already registered. Kindly login.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                db.collection("patients")
                    .whereEqualTo("number", number)
                    .get()
                    .addOnSuccessListener { numberDocs ->
                        if (!numberDocs.isEmpty) {
                            Toast.makeText(this, "Phone number already registered. Kindly login.", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        // If email and number are unique, proceed with signup
                        val patient = Patient(name, email, number, password)

                        db.collection("patients")
                            .add(patient)
                            .addOnSuccessListener { documentReference ->
                                Log.d("Firestore", "Patient added with ID: ${documentReference.id}")
                                Toast.makeText(this, "Signup successful!", Toast.LENGTH_SHORT).show()

                                // Store user details in SharedPreferences
                                saveUserToSharedPrefs(name, email, number, password)

                                // Proceed to the next page only if signup is successful
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firestore", "Error adding patient", e)
                                Toast.makeText(this, "Signup failed. Try again.", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Log.e("Firestore", "Error checking phone number", e)
                        Toast.makeText(this, "Error checking phone number. Try again.", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking email", e)
                Toast.makeText(this, "Error checking email. Try again.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInputs(name: String, email: String, number: String, password: String): Boolean {
        var isValid = true

        if (name.isEmpty()) {
            binding.tvName.error = "Name cannot be empty"
            isValid = false
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tvEmail.error = "Enter a valid email address"
            isValid = false
        }

        if (number.isEmpty() || !number.matches(Regex("\\d{10}"))) {
            binding.tvNumber.error = "Enter a valid 10-digit phone number"
            isValid = false
        }

        if (password.isEmpty() || password.length < 6) {
            binding.tvPassword.error = "Password must be at least 6 characters"
            isValid = false
        }

        return isValid
    }

    private fun saveUserToSharedPrefs(name: String, email: String, number: String, password: String) {
        val editor = sharedPreferences.edit()
        editor.putString("name", name)
        editor.putString("email", email)
        editor.putString("number", number)
        editor.putString("password", password)
        editor.apply()
    }
}
