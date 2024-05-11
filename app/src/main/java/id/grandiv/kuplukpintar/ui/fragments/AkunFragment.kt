package id.grandiv.kuplukpintar.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.R
import android.util.Log

class AkunFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the role from the shared preferences or from the intent extras
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        val role = sharedPref?.getString("role", "defaultRole")

        // Inflate the layout for this fragment based on the role
        return if (role == "dokter") {
            inflater.inflate(R.layout.fragment_akun_dokter, container, false)
        } else {
            inflater.inflate(R.layout.fragment_akun_pasien, container, false)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the email from the shared preferences or from the intent extras
        val sharedPref = activity?.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val role = sharedPref?.getString("loggedInRole", "defaultRole")
        val email = sharedPref?.getString("loggedInEmail", "defaultEmail")

        Log.d("AkunFragment", "Role: $role, Email: $email")

        // Query the Firestore database to get the user data
        val db = Firebase.firestore
        db.collection(role!!)
            .whereEqualTo("akun.email", email)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("AkunFragment", "Documents: $documents")

                if (!documents.isEmpty) {
                    val user = documents.documents[0]
                    val akun = user["akun"] as Map<*, *>

                    Log.d("AkunFragment", "Akun: $akun")

                    if (role == "dokter") {
                        // Populate the EditText fields with the user data
                        val editTextNamaDenganGelar =
                            view.findViewById<EditText>(R.id.editTextNamaDenganGelar)
                        editTextNamaDenganGelar.setText(akun["nama (dengan gelar)"] as String)

                        val editTextInstasi = view.findViewById<EditText>(R.id.editTextInstasi)
                        editTextInstasi.setText(akun["instansi"] as String)

                        val editTextNomorSIP = view.findViewById<EditText>(R.id.editTextNomorSIP)
                        editTextNomorSIP.setText(akun["nomor sip"] as String)

                        val editTextNomorTelepon =
                            view.findViewById<EditText>(R.id.editTextNomorTelepon)
                        editTextNomorTelepon.setText(akun["nomor telepon"] as String)

                        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmail)
                        editTextEmail.setText(akun["email"] as String)

                        val editTextPassword = view.findViewById<EditText>(R.id.editTextPassword)
                        editTextPassword.setText(akun["password"] as String)

                        // Add a click listener to the "Ubah Profil" button
                        val btnUbahProfil = view.findViewById<Button>(R.id.btnUbahProfil)
                        btnUbahProfil.setOnClickListener {
                            // Get the current text of the EditText fields
                            val namaDenganGelar = editTextNamaDenganGelar.text.toString()
                            val instasi = editTextInstasi.text.toString()
                            val nomorSIP = editTextNomorSIP.text.toString()
                            val nomorTelepon = editTextNomorTelepon.text.toString()
                            val email = editTextEmail.text.toString()
                            val password = editTextPassword.text.toString()

                            // Create a map with the updated user data
                            val updatedAkun = hashMapOf(
                                "nama (dengan gelar)" to namaDenganGelar,
                                "instasi" to instasi,
                                "nomor sip" to nomorSIP,
                                "nomor telepon" to nomorTelepon,
                                "email" to email,
                                "password" to password
                            )

                            // Update the user data in Firestore
                            db.collection(role)
                                .document(user.id)
                                .update("akun", updatedAkun)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        context,
                                        "Profile updated successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        context,
                                        "Error updating profile: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else if (role == "pasien") {
                        // Populate the EditText fields with the user data
                    }
                }
            }
    }

    companion object {
        fun newInstance() = AkunFragment()
    }
}