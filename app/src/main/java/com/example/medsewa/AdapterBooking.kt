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
import com.example.medsewa.databinding.BookDrCardBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AdapterBooking(
    private val context: Context,
    private val drList: ArrayList<Doctors>
) : RecyclerView.Adapter<AdapterBooking.DrViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private var patientEmail: String? = null

    init {
        getPatientEmailFromPrefs()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrViewHolder {
        val binding = BookDrCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DrViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return drList.size
    }

    override fun onBindViewHolder(holder: DrViewHolder, position: Int) {
        val doctor = drList[position]
        holder.bind(doctor)

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

    class DrViewHolder(val binding: BookDrCardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: Doctors) {
            binding.tvName.text = doctor.name
            binding.tvSpecial.text = doctor.speciality
            binding.tvYears.text = "${doctor.years} years of experience"
        }
    }

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
        datePicker.datePicker.minDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun showTimePicker(doctorId: String, selectedDate: String) {
        val allowedHours = (9..16).toList()
        val allowedMinutes = listOf(0, 30)

        val timePicker = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                if (hourOfDay !in allowedHours || minute !in allowedMinutes) {
                    Toast.makeText(context, "Please select a valid 30-minute slot between 9 AM and 5 PM.", Toast.LENGTH_SHORT).show()
                    showTimePicker(doctorId, selectedDate)
                } else {
                    val selectedTime = String.format("%02d:%02d", hourOfDay, minute)
                    saveAppointment(doctorId, selectedDate, selectedTime)
                }
            },
            9, 0, true
        )
        timePicker.show()
    }

    private fun saveAppointment(doctorId: String, date: String, time: String) {
        db.collection("appointments")
            .whereEqualTo("doctorId", doctorId)
            .whereEqualTo("date", date)
            .whereEqualTo("time", time)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
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
