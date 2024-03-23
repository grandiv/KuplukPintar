package id.grandiv.kuplukpintar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button

    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val daftarButton = findViewById<TextView>(R.id.daftar_button)
        daftarButton.setOnClickListener {
            val intent = Intent(this, DaftarActivity::class.java)
            startActivity(intent)
        }

        etEmail = findViewById(R.id.email_input)
        etPassword = findViewById(R.id.password_input)
        btnLogin = findViewById(R.id.login_button)

        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            db.collection("dokter")
                .whereEqualTo("akun.email", email)
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // No doctor found, check in "pasien" collection
                        db.collection("pasien")
                            .whereEqualTo("akun.email", email)
                            .get()
                            .addOnSuccessListener { pasienDocuments ->
                                if (pasienDocuments.isEmpty) {
                                    Toast.makeText(this, "No user found with this email", Toast.LENGTH_SHORT).show()
                                } else {
                                    // Check password for patient
                                    val pasien = pasienDocuments.documents[0]
                                    val pasienPassword = (pasien["akun"] as Map<*, *>)["password"] as String
                                    if (pasienPassword == password) {
                                        // Redirect to HomeFragment in MainActivity
                                        val intent = Intent(this, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    } else {
                                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                    } else {
                        // Check password for doctor
                        val dokter = documents.documents[0]
                        val dokterPassword = (dokter["akun"] as Map<*, *>)["password"] as String
                        if (dokterPassword == password) {
                            // Redirect to DokterDaftarPasienActivity
                            val intent = Intent(this, DokterDaftarPasienActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error occurred: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}