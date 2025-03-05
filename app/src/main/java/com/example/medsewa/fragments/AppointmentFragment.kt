package com.example.medsewa.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medsewa.AdapterDoctors
import com.example.medsewa.AdapterMed
import com.example.medsewa.DataClasses.Doctors
import com.example.medsewa.R
import com.example.medsewa.databinding.FragmentAppointmentBinding

class AppointmentFragment : Fragment() {
    lateinit var binding: FragmentAppointmentBinding
    private lateinit var adapter: AdapterDoctors
    val doctorsList = arrayListOf(
        Doctors("Dr. Aakash Sharma", 12, "Cardiologist"),
        Doctors("Dr. Priya Mehta", 8, "Dermatologist"),
        Doctors("Dr. Rohan Verma", 15, "Neurologist"),
        Doctors("Dr. Neha Kapoor", 10, "Pediatrician")
    )


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAppointmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter= AdapterDoctors(doctorsList)
        binding.recyclerView.layoutManager=LinearLayoutManager(requireContext())
        binding.recyclerView.adapter=adapter
    }

}