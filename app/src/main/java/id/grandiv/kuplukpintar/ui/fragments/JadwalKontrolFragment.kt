package id.grandiv.kuplukpintar.ui.fragments
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.google.firebase.Timestamp
import id.grandiv.kuplukpintar.models.JadwalKontrol
import id.grandiv.kuplukpintar.ui.adapters.JadwalKontrolAdapter
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.RiwayatKontrol
import id.grandiv.kuplukpintar.ui.adapters.RiwayatKontrolAdapter

class JadwalKontrolFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var jadwalKontrolRecyclerView: RecyclerView
    private lateinit var riwayatKontrolRecyclerView: RecyclerView
    private lateinit var jadwalKontrolList: MutableList<JadwalKontrol>
    private lateinit var riwayatKontrolList: MutableList<RiwayatKontrol>
    private lateinit var jadwalKontrolAdapter: JadwalKontrolAdapter
    private lateinit var riwayatKontrolAdapter: RiwayatKontrolAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_jadwal_kontrol, container, false)

        db = Firebase.firestore

        jadwalKontrolRecyclerView = view.findViewById(R.id.recyclerViewJadwal)
        jadwalKontrolRecyclerView.layoutManager = LinearLayoutManager(context)

        riwayatKontrolRecyclerView = view.findViewById(R.id.recyclerViewRiwayat)
        riwayatKontrolRecyclerView.layoutManager = LinearLayoutManager(context)

        jadwalKontrolList = mutableListOf()
        riwayatKontrolList = mutableListOf()

        jadwalKontrolAdapter = JadwalKontrolAdapter(jadwalKontrolList, ::checklistJadwalKontrol)
        jadwalKontrolRecyclerView.adapter = jadwalKontrolAdapter

        riwayatKontrolAdapter = RiwayatKontrolAdapter(riwayatKontrolList, this)
        riwayatKontrolRecyclerView.adapter = riwayatKontrolAdapter

        val addButton = view.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddJadwalDialog()
        }

        db.collection("riwayat kontrol")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val riwayatKontrol = document.toObject(RiwayatKontrol::class.java)
                    riwayatKontrolList.add(riwayatKontrol)
                }
                riwayatKontrolAdapter.notifyDataSetChanged()

                // After fetching the accepted patients, fetch the patient requests
                db.collection("jadwal kontrol rutin")
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            val jadwalKontrol = document.toObject(JadwalKontrol::class.java)

                            // Check if the patient request is already in the accepted patients list
                            val isAlreadyKontrol = riwayatKontrolList.any { it.tanggal == jadwalKontrol.tanggal }

                            // If the patient request is not already accepted, add it to the list
                            if (!isAlreadyKontrol) {
                                jadwalKontrolList.add(jadwalKontrol)
                            }
                        }
                        jadwalKontrolAdapter.notifyDataSetChanged()
                    }
            }

        return view
    }

    private fun showAddJadwalDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        builder.setTitle("Add Jadwal Kontrol")
        val dialogLayout = inflater.inflate(R.layout.dialog_add_jadwal, null)
        val editTextTanggal  = dialogLayout.findViewById<EditText>(R.id.editTextTanggal)
        val editTextTempat  = dialogLayout.findViewById<EditText>(R.id.editTextTempat)
        val editTextDokter  = dialogLayout.findViewById<EditText>(R.id.editTextDokter)
        val editTextPesan  = dialogLayout.findViewById<EditText>(R.id.editTextPesan)
        builder.setView(dialogLayout)

        // Set up the DatePicker
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        val timePickerDialog = TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val date = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault()).format(calendar.time)
                editTextTanggal.setText(date)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // 24 hour time
        )

        editTextTanggal.setOnClickListener {
            datePickerDialog.show()
            datePickerDialog.setOnDismissListener {
                timePickerDialog.show()
            }
        }

        builder.setPositiveButton("Save") { dialogInterface, i ->
            val tanggalStr = editTextTanggal.text.toString()
            val tempat = editTextTempat.text.toString()
            val dokter = editTextDokter.text.toString()
            val pesan = editTextPesan.text.toString()
            if (pesan.isEmpty()) {
                Toast.makeText(context, "Pesan cannot be empty", Toast.LENGTH_SHORT).show()
            } else {
                // Convert the String date to a Timestamp
                val dateFormat = SimpleDateFormat("HH:mm | dd-MM-yyyy", Locale.getDefault())
                val date = dateFormat.parse(tanggalStr)
                val tanggal = Timestamp(date.time / 1000, 0)

                val jadwalKontrol = JadwalKontrol(tanggal, tempat, dokter, pesan)
                jadwalKontrolList.add(jadwalKontrol)
                jadwalKontrolAdapter.notifyDataSetChanged()
                riwayatKontrolAdapter.notifyDataSetChanged()

                // Add the new JadwalKontrol to the Firestore database
                db.collection("jadwal kontrol rutin")
                    .add(jadwalKontrol)
                    .addOnSuccessListener { documentReference ->
                        // Document was added successfully
                    }
                    .addOnFailureListener { e ->
                        // Handle the error
                    }
            }
        }
        builder.show()
    }

    private fun checklistJadwalKontrol(jadwalKontrol: JadwalKontrol){
        val riwayatKontrol = RiwayatKontrol(jadwalKontrol.tanggal, jadwalKontrol.tempat, jadwalKontrol.dokter, jadwalKontrol.pesan)
        riwayatKontrolList.add(riwayatKontrol)
        riwayatKontrolAdapter.notifyDataSetChanged()
        jadwalKontrolList.remove(jadwalKontrol)
        jadwalKontrolAdapter.notifyDataSetChanged()

        // Add the new RiwayatKontrol to the Firestore database
        db.collection("riwayat kontrol")
            .add(riwayatKontrol)
            .addOnSuccessListener { documentReference ->
                // Document was added successfully
            }
            .addOnFailureListener { e ->
                // Handle the error
            }

        // Remove the JadwalKontrol from the Firestore database
        db.collection("jadwal kontrol rutin")
            .whereEqualTo("tanggal", jadwalKontrol.tanggal)
            .whereEqualTo("tempat", jadwalKontrol.tempat)
            .whereEqualTo("dokter", jadwalKontrol.dokter)
            .whereEqualTo("pesan", jadwalKontrol.pesan)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    document.reference.delete()
                }
            }
    }

}