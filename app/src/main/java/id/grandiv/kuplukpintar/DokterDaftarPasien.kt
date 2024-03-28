package id.grandiv.kuplukpintar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
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
    private lateinit var acceptedPatients: MutableList<AcceptedPatient>
    private lateinit var patientRequestAdapter: PatientRequestAdapter
    private lateinit var acceptedPatientAdapter: AcceptedPatientAdapter
    private lateinit var nomorSip: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dokter_daftar_pasien)

        db = Firebase.firestore

        // Get the ID of the logged-in doctor from the shared preferences
        val sharedPref = getSharedPreferences("MyPref", MODE_PRIVATE)
        nomorSip = sharedPref.getString("nomorSip", "") ?: ""

        patientRequestRecyclerView = findViewById(R.id.patient_request_recycler_view)
        patientRequestRecyclerView.layoutManager = LinearLayoutManager(this)

        acceptedPatientsRecyclerView = findViewById(R.id.accepted_patients_recycler_view)
        acceptedPatientsRecyclerView.layoutManager = LinearLayoutManager(this)

        patientRequests = mutableListOf()
        acceptedPatients = mutableListOf()

        patientRequestAdapter = PatientRequestAdapter(patientRequests, ::acceptPatientRequest, ::rejectPatientRequest)
        patientRequestRecyclerView.adapter = patientRequestAdapter

        acceptedPatientAdapter = AcceptedPatientAdapter(acceptedPatients)
        acceptedPatientsRecyclerView.adapter = acceptedPatientAdapter

        // Query Firestore to get the accepted patients for the current doctor
        db.collection("acceptedPatients")
            .whereEqualTo("nomorSip", nomorSip)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val patient = document.toObject(AcceptedPatient::class.java)
                    acceptedPatients.add(patient)
                }
                acceptedPatientAdapter.notifyDataSetChanged()

                // After fetching the accepted patients, fetch the patient requests
                db.collection("patientRequests")
                    .whereEqualTo("nomorSip", nomorSip)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val patientRequest = document.toObject(PatientRequest::class.java)

                            // Check if the patient request is already in the accepted patients list
                            val isAlreadyAccepted = acceptedPatients.any { it.email == patientRequest.email }

                            // If the patient request is not already accepted, add it to the list
                            if (!isAlreadyAccepted) {
                                patientRequests.add(patientRequest)
                            }
                        }
                        patientRequestAdapter.notifyDataSetChanged()
                    }
            }
    }

    private fun acceptPatientRequest(patientRequest: PatientRequest) {
        // Create a new AcceptedPatient object
        val acceptedPatient = AcceptedPatient(patientRequest.name, patientRequest.email, patientRequest.address, patientRequest.phoneNumber, patientRequest.nomorSip)

        // Add the AcceptedPatient to the acceptedPatients collection in Firestore
        db.collection("acceptedPatients")
            .add(acceptedPatient)
            .addOnSuccessListener {
                // Add the accepted patient to the local list and update the RecyclerView
                acceptedPatients.add(acceptedPatient)
                acceptedPatientAdapter.notifyDataSetChanged()
            }

        // Remove the patient request from the "patientRequests" collection in Firestore
        db.collection("patientRequests")
            .document(patientRequest.email) // Replace with the actual document ID
            .delete()
            .addOnSuccessListener {
                // Remove the patient request from the RecyclerView
                val index = patientRequests.indexOf(patientRequest)
                if (index != -1) {
                    patientRequestRecyclerView.removeViewAt(index)
                }

                // Remove the patient request from the local list and update the RecyclerView
                patientRequests.remove(patientRequest)
                patientRequestAdapter.notifyDataSetChanged()
            }
    }

    private fun rejectPatientRequest(patientRequest: PatientRequest) {
        // Remove the patient request from the list and update the RecyclerView
        patientRequests.remove(patientRequest)
        patientRequestAdapter.notifyDataSetChanged()
    }
}