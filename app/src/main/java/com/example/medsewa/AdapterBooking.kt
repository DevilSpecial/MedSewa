package com.example.medsewa

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.medsewa.AdapterMed.MedViewHolder
import com.example.medsewa.DataClasses.Doctors
import com.example.medsewa.DataClasses.Medecine
import com.example.medsewa.databinding.BookDrCardBinding
import com.example.medsewa.databinding.DoctorCardViewBinding
import com.example.medsewa.databinding.MedicineCardViewBinding

class AdapterBooking(private val drList: ArrayList<Doctors>): RecyclerView.Adapter<AdapterBooking.DrViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterBooking.DrViewHolder {
        val binding = BookDrCardBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  DrViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return drList.size
    }

    override fun onBindViewHolder(holder: AdapterBooking.DrViewHolder, position: Int) {
        val dr = drList[position]
        holder.bind(dr)
    }


    class DrViewHolder(private val binding: BookDrCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctors: Doctors) {
            binding.tvName.text = doctors.name
            binding.tvSpecial.text = doctors.speciality
            binding.tvYears.text = "${doctors.years} years of experience"
        }
    }}