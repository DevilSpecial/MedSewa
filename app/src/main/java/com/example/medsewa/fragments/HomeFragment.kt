package com.example.medsewa.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.medsewa.AdapterMed
import com.example.medsewa.DataClasses.Medecine
import com.example.medsewa.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var adapter: AdapterMed
    private val medicines = arrayListOf(
        Medecine("Amoxicillin 500mg", 1, 3,0,5),
        Medecine("Paracetamol 650mg",  2, 2,0,4),
        Medecine("Paracetamol 650mg",  2, 2,0,4),
        Medecine("Paracetamol 650mg",  2, 2,0,4),
        Medecine("Cough Syrup",  0, 1,0,2)
    )

lateinit var  binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding= FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedPreferences = requireContext().getSharedPreferences("UserPrefs", MODE_PRIVATE)
        val savedName = sharedPreferences.getString("name", null)
        binding.textView.text=savedName
        adapter = AdapterMed(medicines)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

    }

}