package com.example.medsewa

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medsewa.DataClasses.Doctors
import com.example.medsewa.databinding.ActivityDoctorsListBinding
import com.google.firebase.firestore.FirebaseFirestore

class DoctorsListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDoctorsListBinding
    private lateinit var adapter: AdapterBooking
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDoctorsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val selectedSpecialization = intent.getStringExtra("SPECIALIZATION")
        binding.textView2.text= selectedSpecialization
        selectedSpecialization?.let {
            fetchDoctors(it)
        }

    }
    private fun fetchDoctors(specialization: String) {
        db.collection("doctors")
            .whereEqualTo("speciality", specialization) // Fetch only matching specialization
            .get()
                .addOnSuccessListener { documents ->
                val doctorList = arrayListOf<Doctors>()
                for (document in documents) {
                    val name = document.getString("name") ?: ""
                    val years = document.getLong("years")?.toInt() ?: 0
                    val speciality = document.getString("speciality") ?: ""

                    doctorList.add(Doctors(name, years, speciality))
                }
                setupRecyclerView(doctorList)

            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Error fetching doctors", exception)            }
    }

    private fun setupRecyclerView(doctors: List<Doctors>) {
        val adapter = AdapterBooking(doctors as ArrayList<Doctors>)
        binding.recyclerView.layoutManager= LinearLayoutManager(this)
        binding.recyclerView.adapter=adapter
    }
}