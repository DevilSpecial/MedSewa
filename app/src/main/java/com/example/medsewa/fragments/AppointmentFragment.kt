package com.example.medsewa.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medsewa.AdapterDoctors
import com.example.medsewa.DataClasses.Doctors
import com.example.medsewa.DoctorsListActivity
import com.example.medsewa.databinding.FragmentAppointmentBinding
import com.google.firebase.firestore.FirebaseFirestore

class AppointmentFragment : Fragment() {
    private val db = FirebaseFirestore.getInstance()
    lateinit var binding: FragmentAppointmentBinding
    private lateinit var adapter: AdapterDoctors
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppointmentBinding.inflate(layoutInflater)

        return binding.root
    }

    private fun fetchDoctors() {
        db.collection("doctors")
            .limit(3)  // Fetch only the top 5 doctors
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
                Log.e("Firestore", "Error fetching doctors", exception)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnAppointment.setOnClickListener{
            showSpecializationDialog()
        }
        fetchDoctors()
    }
    private fun showSpecializationDialog() {
        val specializations = arrayOf("Cardiologist", "Urologist", "Neurologist", "Dermatologist", "Orthopedic", "General Physician")

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select Specialization")
        builder.setItems(specializations) { _, which ->
            val selectedSpecialization = specializations[which]
            // Handle selection (e.g., navigate to DoctorListActivity)
            val intent = Intent(requireContext(), DoctorsListActivity::class.java)
            intent.putExtra("SPECIALIZATION", selectedSpecialization)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
    private fun setupRecyclerView(doctors: List<Doctors>) {
        val adapter = AdapterDoctors(doctors as ArrayList<Doctors>)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter
    }


}