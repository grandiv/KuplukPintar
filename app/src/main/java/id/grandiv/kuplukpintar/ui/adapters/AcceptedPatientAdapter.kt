package id.grandiv.kuplukpintar.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.models.AcceptedPatient
import id.grandiv.kuplukpintar.R

interface OnPatientClickListener {
    fun onPatientClick(patient: AcceptedPatient)
}

class AcceptedPatientAdapter(
    private val acceptedPatients: MutableList<AcceptedPatient>,
    private val listener: OnPatientClickListener
) : RecyclerView.Adapter<AcceptedPatientAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tv_patient_name)
        val tvPatientAddress: TextView = view.findViewById(R.id.tv_patient_address)
        val tvPatientPhoneNumber: TextView = view.findViewById(R.id.tv_patient_phone_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_accepted_patient, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val patient = acceptedPatients[position]
        holder.tvPatientName.text = patient.name
        holder.tvPatientAddress.text = patient.address
        holder.tvPatientPhoneNumber.text = patient.phoneNumber

        // Set the click listener for the item view
        holder.itemView.setOnClickListener {
            listener.onPatientClick(patient)
        }
    }

    override fun getItemCount() = acceptedPatients.size
}