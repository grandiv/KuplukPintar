package id.grandiv.kuplukpintar.ui.fragments

import android.content.Context
import android.content.Intent
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
import android.widget.ArrayAdapter
import android.widget.Spinner
import id.grandiv.kuplukpintar.ui.activities.SignInActivity

class AkunFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Get the role from the shared preferences or from the intent extras
//        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
//        val role = sharedPref?.getString("role", "defaultRole")

        val sharedPref = activity?.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val role = sharedPref?.getString("loggedInRole", "defaultRole")


        // Inflate the layout for this fragment based on the role
        val layout = if (role == "dokter") {
            inflater.inflate(R.layout.fragment_akun_dokter, container, false)
        } else {
            inflater.inflate(R.layout.fragment_akun_pasien, container, false)
        }

        Log.d("AkunFragment", "Inflated layout: ${if (role == "dokter") "fragment_akun_dokter" else "fragment_akun_pasien"}")

        return layout
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
                        val editTextNamaLengkap =
                            view.findViewById<EditText>(R.id.editTextNamaLengkap)
                        editTextNamaLengkap.setText(akun["nama lengkap"] as String)

                        val editTextAlamatLengkap =
                            view.findViewById<EditText>(R.id.editTextAlamatLengkap)
                        editTextAlamatLengkap.setText(akun["alamat lengkap"] as String)

                        val editTextNomorTelepon =
                            view.findViewById<EditText>(R.id.editTextNomorTeleponPasien)
                        editTextNomorTelepon.setText(akun["nomor telepon"] as String)

                        val editTextEmail = view.findViewById<EditText>(R.id.editTextEmailPasien)
                        editTextEmail.setText(akun["email"] as String)

                        val editTextPassword = view.findViewById<EditText>(R.id.editTextPasswordPasien)
                        editTextPassword.setText(akun["password"] as String)

                        // Get the Spinner for "dokter pengawas"
                        val spinnerDokterPengawas =
                            view.findViewById<Spinner>(R.id.editSpinnerDokterPengawas)

                        if (spinnerDokterPengawas != null) {
                            // Query the Firestore database to get the list of doctors
                            val db = Firebase.firestore
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

                                        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, doctorList)
                                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                        spinnerDokterPengawas.adapter = adapter
                                    } else {
                                        Toast.makeText(requireContext(), "No doctors available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(requireContext(), "Request Failed", Toast.LENGTH_SHORT).show()
                                }
                        }

                        // Add a click listener to the "Ubah Profil" button
                        val btnUbahProfil = view.findViewById<Button>(R.id.btnUbahProfilPasien)
                        btnUbahProfil.setOnClickListener {
                            // Get the current text of the EditText fields
                            val namaLengkap = editTextNamaLengkap.text.toString()
                            val alamatLengkap = editTextAlamatLengkap.text.toString()
                            val nomorTelepon = editTextNomorTelepon.text.toString()
                            val email = editTextEmail.text.toString()
                            val password = editTextPassword.text.toString()
                            // Get the current selected item of the Spinner
                            val dokterPengawas = spinnerDokterPengawas.selectedItem.toString()

                            // Create a map with the updated user data
                            val updatedAkun = hashMapOf(
                                "nama lengkap" to namaLengkap,
                                "alamat lengkap" to alamatLengkap,
                                "nomor telepon" to nomorTelepon,
                                "email" to email,
                                "password" to password,
                                "dokter pengawas" to dokterPengawas
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
                    }
                }
            }
        val btnLogOut = view.findViewById<Button>(R.id.btnLogOut)

        btnLogOut.setOnClickListener {
            // Clear the user data from the shared preferences
            val sharedPref = activity?.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.clear()
            editor?.apply()

            // Navigate the user back to the login screen
            // Replace LoginActivity::class.java with your login activity class
            val intent = Intent(activity, SignInActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }
    }
    companion object {
        fun newInstance() = AkunFragment()
    }
}