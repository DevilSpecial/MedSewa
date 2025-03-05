package com.example.medsewa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medsewa.databinding.MedecingCardViewBinding

class AdapterMed(private val medList: ArrayList<Medecine>): RecyclerView.Adapter<AdapterMed.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = MedecingCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  medList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val med = medList[position]
        holder.bind(med)
    }

    class MyViewHolder(private val binding: MedecingCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(medicine: Medecine) {
            binding.medName.text = medicine.medText
            binding.tvTimes.text = "${medicine.numPills} Times Daily"
            binding.tvTaken.text =  "Day ${medicine.daysTaken} of ${medicine.totalDays}"
            updatePillsUI(medicine)
            binding.btnTaken.setOnClickListener {
                if (medicine.pillsTaken < medicine.numPills) {
                    medicine.pillsTaken++  // Increment doses taken
                    updatePillsUI(medicine)  // Refresh UI
                }
            }
        }

        private fun updatePillsUI(medicine: Medecine) {
            binding.pillContainer.removeAllViews()
            val context = binding.root.context

            for (i in 0 until medicine.numPills) {
                val pill = ImageView(context)
                pill.layoutParams = ViewGroup.LayoutParams(50, 50)

                // Initially gray, turns green when taken
                if (i < medicine.pillsTaken) {
                    pill.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pill_active))
                } else {
                    pill.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.pill_passive))
                }

                binding.pillContainer.addView(pill)
            }
        }
        }
    }

