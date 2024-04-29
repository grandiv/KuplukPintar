import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.JadwalKontrol
import id.grandiv.kuplukpintar.JadwalKontrolAdapter
import id.grandiv.kuplukpintar.RiwayatKontrol
import id.grandiv.kuplukpintar.RiwayatKontrolAdapter
import id.grandiv.kuplukpintar.R
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class JadwalKontrolFragment : Fragment() {
    private lateinit var db: FirebaseFirestore
    private lateinit var jadwalKontrolAdapter: JadwalKontrolAdapter
    private lateinit var riwayatKontrolAdapter: RiwayatKontrolAdapter
    val jadwalKontrolList = mutableListOf<JadwalKontrol>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal_kontrol, container, false)

        db = Firebase.firestore

        val jadwalKontrolList = mutableListOf<JadwalKontrol>()
        jadwalKontrolAdapter = JadwalKontrolAdapter(jadwalKontrolList)

        val recyclerViewJadwal = view.findViewById<RecyclerView>(R.id.recyclerViewJadwal)
        recyclerViewJadwal.adapter = jadwalKontrolAdapter

        db.collection("jadwal kontrol rutin")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val jadwalKontrol = document.toObject(JadwalKontrol::class.java)
                    jadwalKontrolList.add(jadwalKontrol)
                }
                jadwalKontrolAdapter.notifyDataSetChanged()
            }

        val riwayatKontrolList = mutableListOf<RiwayatKontrol>()
        riwayatKontrolAdapter = RiwayatKontrolAdapter(riwayatKontrolList)

        val recyclerViewRiwayat = view.findViewById<RecyclerView>(R.id.recyclerViewRiwayat)
        recyclerViewRiwayat.adapter = riwayatKontrolAdapter

        db.collection("riwayat kontrol")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val riwayatKontrol = document.toObject(RiwayatKontrol::class.java)
                    riwayatKontrolList.add(riwayatKontrol)
                }
                riwayatKontrolAdapter.notifyDataSetChanged()
            }

        val addButton = view.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddJadwalDialog()
        }

        val editButton = view.findViewById<Button>(R.id.editButton)
        editButton.setOnClickListener {
            // Edit the currently selected checkup schedule
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
                val date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                editTextTanggal.setText(date)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        editTextTanggal.setOnClickListener {
            datePickerDialog.show()
        }

        builder.setPositiveButton("Save") { dialogInterface, i ->
            val tanggal = com.google.firebase.Timestamp(calendar.time)
            val tempat = editTextTempat.text.toString()
            val dokter = editTextDokter.text.toString()
            val pesan = editTextPesan.text.toString()
            val jadwalKontrol = JadwalKontrol(tanggal, tempat, dokter, pesan)
            jadwalKontrolList.add(jadwalKontrol)
            jadwalKontrolAdapter.notifyDataSetChanged()
        }
        builder.show()
    }
}