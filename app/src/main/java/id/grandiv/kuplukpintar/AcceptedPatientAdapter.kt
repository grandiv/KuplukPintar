package id.grandiv.kuplukpintar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AcceptedPatientAdapter(
    private val acceptedPatients: MutableList<AcceptedPatient>
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
    }

    override fun getItemCount() = acceptedPatients.size
}