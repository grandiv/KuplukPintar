package id.grandiv.kuplukpintar.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.models.PatientRequest
import id.grandiv.kuplukpintar.R

class FormPasienActivity : AppCompatActivity() {
    private lateinit var etNamaLengkap: EditText
    private lateinit var etAlamatLengkap: EditText
    private lateinit var etNomorTelp: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spDokterPengawas: Spinner
    private lateinit var btnDaftar: Button

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_pasien)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@FormPasienActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        etNamaLengkap = findViewById(R.id.nama_input)
        etAlamatLengkap = findViewById(R.id.alamat_input)
        etNomorTelp = findViewById(R.id.nomortelp_input)
        etEmail = findViewById(R.id.email_input)
        etPassword = findViewById(R.id.password_input)
        spDokterPengawas = findViewById(R.id.dokterpengawas_input)
        btnDaftar = findViewById(R.id.daftarpasien_button)

        displayDoctorSpinner()

        btnDaftar.setOnClickListener {
            val sNamaLengkap = etNamaLengkap.text.toString().trim()
            val sAlamatLengkap = etAlamatLengkap.text.toString().trim()
            val sNomorTelp = etNomorTelp.text.toString().trim()
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()
            val sDokterPengawas = spDokterPengawas.selectedItem.toString()

            val akun = hashMapOf(
                "nama lengkap" to sNamaLengkap,
                "alamat lengkap" to sAlamatLengkap,
                "nomor telepon" to sNomorTelp,
                "email" to sEmail,
                "password" to sPassword,
                "dokter pengawas" to sDokterPengawas
            )

            val pasien = hashMapOf(
                "akun" to akun,
            )

            db.collection("pasien")
                .add(pasien)
                .addOnSuccessListener {
                    // Get the ID of the selected doctor
                    val nomorSip = getnomorSip(sDokterPengawas)

                    // Create a PatientRequest object
                    val patientRequest = PatientRequest(sNamaLengkap, sEmail, sAlamatLengkap, sNomorTelp, nomorSip)

                    // Add the PatientRequest to the patientRequests collection in Firestore
                    db.collection("patientRequests")
                        .add(patientRequest)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Berhasil mendaftar sebagai pasien", Toast.LENGTH_SHORT).show()
                            etNamaLengkap.text.clear()
                            etAlamatLengkap.text.clear()
                            etNomorTelp.text.clear()
                            etEmail.text.clear()
                            etPassword.text.clear()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun displayDoctorSpinner() {
        val doctorRef = db.collection("dokter")
        doctorRef.get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val doctorList = mutableListOf<String>()

                    for (document in querySnapshot.documents) {
                        val akunMap = document.data?.get("akun") as? Map<*, *>
                        val doctorName = akunMap?.get("nama (dengan gelar)") as? String
                        if (doctorName != null) {
                            doctorList.add(doctorName)
                        }
                    }

                    val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, doctorList)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spDokterPengawas.adapter = adapter
                } else {
                    Toast.makeText(this, "No doctors available", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Request Failed", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getnomorSip(doctorName: String): String {
        var nomorSip = ""

        db.collection("dokter")
            .whereEqualTo("nama (dengan gelar)", doctorName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    nomorSip = document.id
                }
            }

        return nomorSip
    }
}