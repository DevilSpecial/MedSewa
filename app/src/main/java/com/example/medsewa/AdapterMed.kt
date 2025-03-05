package com.example.medsewa

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.medsewa.DataClasses.Medecine
import com.example.medsewa.databinding.MedicineCardViewBinding

class AdapterMed(private val medList: ArrayList<Medecine>): RecyclerView.Adapter<AdapterMed.MedViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedViewHolder {
        val binding = MedicineCardViewBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return  MedViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return  medList.size
    }

    override fun onBindViewHolder(holder: MedViewHolder, position: Int) {
        val med = medList[position]
        holder.bind(med)
    }

    class MedViewHolder(private val binding: MedicineCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
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

