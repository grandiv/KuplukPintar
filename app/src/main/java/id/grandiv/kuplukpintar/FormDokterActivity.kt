package id.grandiv.kuplukpintar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FormDokterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etInstansi: EditText
    private lateinit var etNomorSip: EditText
    private lateinit var etNomorTelp: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnDaftar: Button

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.form_dokter)

        val onBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val intent = Intent(this@FormDokterActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

        etName = findViewById(R.id.nama_input)
        etInstansi = findViewById(R.id.instansi_input)
        etNomorSip = findViewById(R.id.nomorsip_input)
        etNomorTelp = findViewById(R.id.nomortelp_input)
        etEmail = findViewById(R.id.email_input)
        etPassword = findViewById(R.id.password_input)
        btnDaftar = findViewById(R.id.daftardokter_button)

        btnDaftar.setOnClickListener {
            val sName = etName.text.toString().trim()
            val sInstansi = etInstansi.text.toString().trim()
            val sNomorSip = etNomorSip.text.toString().trim()
            val sNomorTelp = etNomorTelp.text.toString().trim()
            val sEmail = etEmail.text.toString().trim()
            val sPassword = etPassword.text.toString().trim()

            val akun = hashMapOf(
                "nama (dengan gelar)" to sName,
                "instansi" to sInstansi,
                "nomor sip" to sNomorSip,
                "nomor telepon" to sNomorTelp,
                "email" to sEmail,
                "password" to sPassword
            )

            val daftarPasien = hashMapOf<String, String>()

            val dokter = hashMapOf(
                "akun" to akun,
                "daftar pasien" to daftarPasien
            )

            db.collection("dokter")
                .add(dokter)
                .addOnSuccessListener {
                    Toast.makeText(this, "Berhasil mendaftar sebagai dokter", Toast.LENGTH_SHORT).show()
                    etName.text.clear()
                    etInstansi.text.clear()
                    etNomorSip.text.clear()
                    etNomorTelp.text.clear()
                    etEmail.text.clear()
                    etPassword.text.clear()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}