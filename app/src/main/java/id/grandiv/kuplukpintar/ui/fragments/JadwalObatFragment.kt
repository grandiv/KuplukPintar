package id.grandiv.kuplukpintar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.models.Day
import id.grandiv.kuplukpintar.ui.adapters.WeekAdapter
import java.text.SimpleDateFormat
import java.util.*

class JadwalObatFragment : Fragment() {

    private lateinit var weekAdapter: WeekAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jadwal_obat, container, false)

        val recyclerViewWeek = view.findViewById<RecyclerView>(R.id.recyclerViewWeek)
        val tvFullDate = view.findViewById<TextView>(R.id.tv_full_date)

        recyclerViewWeek.layoutManager = LinearLayoutManager(context)

        weekAdapter = WeekAdapter(getWeekDays())
        recyclerViewWeek.adapter = weekAdapter

        tvFullDate.text = getFullDate()

        return view
    }

    private fun getWeekDays(): List<Day> {
        val weekDays = mutableListOf<Day>()
        val calendar = Calendar.getInstance()
        for (i in 0 until 7) {
            val dayOfWeek = SimpleDateFormat("EEE", Locale.getDefault()).format(calendar.time)
            val date = SimpleDateFormat("d", Locale.getDefault()).format(calendar.time)
            val isCurrentDate = Calendar.getInstance().get(Calendar.DAY_OF_YEAR) == calendar.get(Calendar.DAY_OF_YEAR)
            weekDays.add(Day(dayOfWeek, date, isCurrentDate))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        return weekDays
    }

    private fun getFullDate(): String {
        val calendar = Calendar.getInstance()
        val dayOfWeek = SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time)
        val date = SimpleDateFormat("d", Locale.getDefault()).format(calendar.time)
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
        val year = SimpleDateFormat("yyyy", Locale.getDefault()).format(calendar.time)
        return "Hari $dayOfWeek, $date $month $year"
    }
}