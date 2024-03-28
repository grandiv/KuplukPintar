package id.grandiv.kuplukpintar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientRequestAdapter(
    private val patientRequests: List<PatientRequest>,
    private val onAccept: (PatientRequest) -> Unit,
    private val onReject: (PatientRequest) -> Unit
) : RecyclerView.Adapter<PatientRequestAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPatientName: TextView = view.findViewById(R.id.tv_patient_name)
        val tvPatientEmail: TextView = view.findViewById(R.id.tv_patient_email)
        val btnAccept: Button = view.findViewById(R.id.btn_accept)
        val btnReject: Button = view.findViewById(R.id.btn_reject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_patient_request, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val patientRequest = patientRequests[position]
        holder.tvPatientName.text = patientRequest.name
        holder.tvPatientEmail.text = patientRequest.email
        holder.btnAccept.setOnClickListener { onAccept(patientRequest) }
        holder.btnReject.setOnClickListener { onReject(patientRequest) }
    }

    override fun getItemCount() = patientRequests.size
}