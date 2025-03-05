package com.example.medsewa

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.medsewa.DataClasses.Doctors
import com.example.medsewa.databinding.DoctorCardViewBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdapterDoctors(
    private val context: Context,
    private val drList: ArrayList<Doctors>
) : RecyclerView.Adapter<AdapterDoctors.DrViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private var patientEmail: String? = null

    init {
        getPatientEmailFromPrefs()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrViewHolder {
        val binding = DoctorCardViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return drList.size
    }

    override fun onBindViewHolder(holder: DrViewHolder, position: Int) {
        val doctor = drList[position]
        holder.bind(doctor)

        // Fetch Firestore ID when clicking "Book"
        holder.binding.btnBook.setOnClickListener {
            getDoctorId(doctor.name) { doctorId ->
                if (doctorId != null) {
                    showDatePicker(doctorId)
                } else {
                    Toast.makeText(context, "Doctor ID not found!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class DrViewHolder(val binding: DoctorCardViewBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: Doctors) {
            binding.tvName.text = doctor.name
            binding.tvSpecial.text = doctor.speciality
            binding.tvYears.text = "${doctor.years} years of experience"
        }
    }

    // Step 1: Get the doctor’s Firestore ID
    private fun getDoctorId(doctorName: String, callback: (String?) -> Unit) {
        db.collection("doctors").whereEqualTo("name", doctorName).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val doctorId = documents.documents[0].id
                    callback(doctorId)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    // Step 2: Open Date Picker
    private fun showDatePicker(doctorId: String) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedDate = "$dayOfMonth-${month + 1}-$year"
                showTimePicker(doctorId, selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() // Disable past dates
        datePicker.show()
    }

    // Step 3: Open Time Picker
    private fun showTimePicker(doctorId: String, selectedDate: String) {
        val allowedHours = (9..16).toList() // Hours from 9 AM to 4 PM
        val allowedMinutes = listOf(0, 30) // Only 00 and 30 minutes

        val calendar = Calendar.getInstance()
        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                // Ensure selection is only in allowed slots
                if (hourOfDay !in allowedHours || minute !in allowedMinutes) {
                    Toast.makeText(context, "Please select a valid 30-minute slot between 9 AM and 5 PM.", Toast.LENGTH_SHORT).show()
                    showTimePicker(doctorId, selectedDate) // Reopen picker if invalid time is selected
                } else {
                    val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    saveAppointment(doctorId, selectedDate, selectedTime)
                }
            },
            9, 0, true
        )

        timePicker.show()
    }


    // Step 4: Save Appointment to Firestore
    private fun saveAppointment(doctorId: String, date: String, time: String) {
        db.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    // No existing appointment for this doctor at the selected time → Proceed with booking

                    db.collection("patients").whereEqualTo("email", patientEmail).get()
                        .addOnSuccessListener { patientDocs ->
                            if (!patientDocs.isEmpty) {
                                val patientId = patientDocs.documents[0].id

                                val appointment = hashMapOf(
                                    "doctorId" to doctorId,
                                    "patientId" to patientId,
                                    "date" to date,
                                    "time" to time
                                )

                                db.collection("appointments").add(appointment)
                                    .addOnSuccessListener {
                                        Toast.makeText(context, "Appointment booked successfully!", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(context, "Error booking appointment!", Toast.LENGTH_SHORT).show()
                                    }
                            } else {
                                Toast.makeText(context, "Patient ID not found!", Toast.LENGTH_SHORT).show()
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error fetching patient ID!", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // An appointment already exists for this doctor at the selected time
                    Toast.makeText(context, "Time slot already booked! Please choose another time.", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error checking existing appointments!", Toast.LENGTH_SHORT).show()
            }
    }


    private fun getPatientEmailFromPrefs() {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        patientEmail = sharedPreferences.getString("email", null)
    }
}