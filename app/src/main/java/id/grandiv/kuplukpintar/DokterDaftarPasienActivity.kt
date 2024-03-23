package id.grandiv.kuplukpintar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DokterDaftarPasienActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var patientRequestRecyclerView: RecyclerView
    private lateinit var acceptedPatientsRecyclerView: RecyclerView
    private lateinit var patientRequests: MutableList<PatientRequest>
    private lateinit var acceptedPatients: MutableList<PatientRequest>
    private lateinit var patientRequestAdapter: PatientRequestAdapter
    private lateinit var acceptedPatientAdapter: AcceptedPatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dokter_daftar_pasien)

        db = Firebase.firestore

        patientRequestRecyclerView = findViewById(R.id.patient_request_recycler_view)
        acceptedPatientsRecyclerView = findViewById(R.id.accepted_patients_recycler_view)

        patientRequests = mutableListOf()
        acceptedPatients = mutableListOf()

        patientRequestAdapter = PatientRequestAdapter(patientRequests, ::acceptPatientRequest, ::rejectPatientRequest)
        patientRequestRecyclerView.adapter = patientRequestAdapter

        acceptedPatientAdapter = AcceptedPatientAdapter(acceptedPatients)
        acceptedPatientsRecyclerView.adapter = acceptedPatientAdapter

        // Query Firestore to get the patient requests and accepted patients
        db.collection("patientRequests")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val patientRequest = document.toObject(PatientRequest::class.java)
                    patientRequests.add(patientRequest)
                }
                patientRequestAdapter.notifyDataSetChanged()
            }

        db.collection("acceptedPatients")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val patient = document.toObject(PatientRequest::class.java)
                    acceptedPatients.add(patient)
                }
                acceptedPatientAdapter.notifyDataSetChanged()
            }
    }

    private fun acceptPatientRequest(patientRequest: PatientRequest) {
        // Add the patient's reference to the "daftar pasien" field in the "dokter" collection
        val docRef = db.collection("dokter").document("uniqueID")
        docRef.update("daftar pasien", FieldValue.arrayUnion(patientRequest.email))

        // Remove the patient request from the list and update the RecyclerView
        patientRequests.remove(patientRequest)
        patientRequestAdapter.notifyDataSetChanged()
    }

    private fun rejectPatientRequest(patientRequest: PatientRequest) {
        // Remove the patient request from the list and update the RecyclerView
        patientRequests.remove(patientRequest)
        patientRequestAdapter.notifyDataSetChanged()
    }
}

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

class AcceptedPatientAdapter(
    private val acceptedPatients: List<PatientRequest>
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