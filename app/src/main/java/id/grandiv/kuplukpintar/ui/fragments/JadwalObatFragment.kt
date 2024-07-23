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
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.Day
import id.grandiv.kuplukpintar.models.JadwalKontrol
import id.grandiv.kuplukpintar.models.JadwalObat
import id.grandiv.kuplukpintar.ui.adapters.JadwalKontrolAdapter
import id.grandiv.kuplukpintar.ui.adapters.JadwalObatAdapter
import id.grandiv.kuplukpintar.ui.adapters.RiwayatKontrolAdapter
import id.grandiv.kuplukpintar.ui.adapters.WeekAdapter
import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

class JadwalObatFragment : Fragment() {
    private lateinit var weekAdapter: WeekAdapter
    private lateinit var jadwalObatAdapter: JadwalObatAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var jadwalObatRecyclerView: RecyclerView
    private lateinit var jadwalObatList: MutableList<JadwalObat>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal_obat, container, false)

        val recyclerViewWeek = view.findViewById<RecyclerView>(R.id.recyclerViewWeek)
        val tvFullDate = view.findViewById<TextView>(R.id.tv_full_date)

        // Fetch the user's role from shared preferences
        val sharedPref = activity?.getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        val role = sharedPref?.getString("loggedInRole", "")

        // Hide the buttons if the user's role is "pasien"
        if (role == "pasien") {
            view.findViewById<Button>(R.id.addButton).visibility = View.GONE
        }

        // membuat recyclerview jadi horizontal
        recyclerViewWeek.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        weekAdapter = WeekAdapter(getWeekDays())
        recyclerViewWeek.adapter = weekAdapter

        tvFullDate.text = getFullDate()

        db = Firebase.firestore

        jadwalObatRecyclerView = view.findViewById(R.id.recyclerViewJadwalObat)
        jadwalObatRecyclerView.layoutManager = LinearLayoutManager(context)

        jadwalObatList = mutableListOf()

        jadwalObatAdapter = JadwalObatAdapter(requireContext(), jadwalObatList, ::editJadwalObat)
        jadwalObatRecyclerView.adapter = jadwalObatAdapter

        val addButton = view.findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddJadwalDialog()
        }

        db.collection("jadwal minum obat")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val jadwalObat = document.toObject(JadwalObat::class.java).apply {
                        id = document.id
                        namaobat = document.getString("namaobat") ?: ""
                        kadar = document.getString("kadar") ?: ""
                        kuantitas = document.getString("kuantitas") ?: ""
                        jam = document.getString("jam") ?: ""
                        kapan = document.getString("kapan") ?: ""
                    }
                    jadwalObatList.add(jadwalObat)
                }
                jadwalObatAdapter.notifyDataSetChanged()
            }

        return view
    }

    private fun getWeekDays(): List<Day> {
        val weekDays = mutableListOf<Day>()
        val calendar = Calendar.getInstance()
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        // Set the calendar to the start of the week (Sunday)
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)

        for (i in 0 until 7) {
            val dayOfWeek = daysOfWeek[i]
            val date = calendar.get(Calendar.DAY_OF_MONTH).toString()
            val isCurrentDate = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == calendar.get(Calendar.DAY_OF_WEEK)
            weekDays.add(Day(dayOfWeek, date, isCurrentDate))

            // Move the calendar to the next day
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        return weekDays
    }

    private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
    }

    private fun getFullDate(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val date = SimpleDateFormat("d", Locale.getDefault()).format(calendar.time)
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        return "Today is $dayOfWeek, $date $month $year"
    }

    private fun showAddJadwalDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        builder.setTitle("Add Jadwal Minum Obat")
        val dialogLayout = inflater.inflate(R.layout.dialog_add_obat, null)
        val editTextObat  = dialogLayout.findViewById<EditText>(R.id.editTextObat)
        val editTextKadar  = dialogLayout.findViewById<EditText>(R.id.editTextKadar)
        val editTextKuantitas  = dialogLayout.findViewById<EditText>(R.id.editTextKuantitas)
        val editTextJam  = dialogLayout.findViewById<EditText>(R.id.editTextJam)
        val editTextKapan = dialogLayout.findViewById<EditText>(R.id.editTextKapan)
        builder.setView(dialogLayout)

        builder.setPositiveButton("Save") { dialogInterface, i ->
            val obat = editTextObat.text.toString()
            val kadar = editTextKadar.text.toString()
            val kuantitas = editTextKuantitas.text.toString()
            val jam = editTextJam.text.toString()
            val kapan = editTextKapan.text.toString()
            val jadwalObat = JadwalObat("", obat, kadar, kuantitas, jam, kapan)
            jadwalObatList.add(jadwalObat)
            jadwalObatAdapter.notifyDataSetChanged()

            // Add the new JadwalKontrol to the Firestore database
            db.collection("jadwal minum obat")
                .add(jadwalObat)
                .addOnSuccessListener { documentReference ->
                    // Document was added successfully
                    // Set the id property to the document ID and update the document
                    jadwalObat.id = documentReference.id
                    documentReference.set(jadwalObat)
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
        builder.show()
    }
    private fun editJadwalObat(jadwalObat: JadwalObat) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        builder.setTitle("Edit Jadwal Minum Obat")
        val dialogLayout = inflater.inflate(R.layout.dialog_add_obat, null)
        val editTextObat  = dialogLayout.findViewById<EditText>(R.id.editTextObat)
        val editTextKadar  = dialogLayout.findViewById<EditText>(R.id.editTextKadar)
        val editTextKuantitas  = dialogLayout.findViewById<EditText>(R.id.editTextKuantitas)
        val editTextJam  = dialogLayout.findViewById<EditText>(R.id.editTextJam)
        val editTextKapan = dialogLayout.findViewById<EditText>(R.id.editTextKapan)
        builder.setView(dialogLayout)

        // Populate the dialog with the current JadwalKontrol data
        editTextObat.setText(jadwalObat.namaobat)
        editTextKadar.setText(jadwalObat.kadar)
        editTextKuantitas.setText(jadwalObat.kuantitas)
        editTextJam.setText(jadwalObat.jam)
        editTextKapan.setText(jadwalObat.kapan)

        builder.setPositiveButton("Save") { dialogInterface, i ->
            val obat = editTextObat.text.toString()
            val kadar = editTextKadar.text.toString()
            val kuantitas = editTextKuantitas.text.toString()
            val jam = editTextJam.text.toString()
            val kapan = editTextKapan.text.toString()
            jadwalObat.namaobat = obat
            jadwalObat.kadar = kadar
            jadwalObat.kuantitas = kuantitas
            jadwalObat.jam = jam
            jadwalObat.kapan = kapan

            // Update the JadwalKontrol in the Firestore database
            db.collection("jadwal minum obat")
                .document(jadwalObat.id) // Assuming JadwalKontrol has an 'id' property
                .set(jadwalObat)
                .addOnSuccessListener {
                    // Document was updated successfully
                }
                .addOnFailureListener { e ->
                    // Handle the error
                }
        }
        builder.show()
    }
}