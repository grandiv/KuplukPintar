package id.grandiv.kuplukpintar.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import id.grandiv.kuplukpintar.R
import id.grandiv.kuplukpintar.utils.CSVFileReader
import id.grandiv.kuplukpintar.utils.EEGData
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HomeFragment : Fragment() {
    private lateinit var eegChart: LineChart
    private lateinit var currentStatusTextView: TextView
    private lateinit var lastMicroseizureTextView: TextView
    private lateinit var lastSeizureTextView: TextView
    private val eegData = mutableListOf<Entry>()
    private var currentIndex = 0
    private lateinit var handler: Handler
    private lateinit var eegDataList: List<EEGData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        eegChart = view.findViewById(R.id.eegChart)
        currentStatusTextView = view.findViewById(R.id.tv_current_status)
        lastMicroseizureTextView = view.findViewById(R.id.tv_last_microseizure)
        lastSeizureTextView = view.findViewById(R.id.tv_last_seizure)

        loadData()
        setupChart()
        startRealTimeUpdates()

        return view
    }

    private fun setupChart() {
        val dataSet = LineDataSet(eegData, "EEG Data")
        dataSet.setDrawValues(false)
        dataSet.setDrawCircles(false)

        val lineData = LineData(dataSet)
        eegChart.data = lineData
        eegChart.setTouchEnabled(true)
        eegChart.isDragEnabled = true
        eegChart.setScaleEnabled(true)
        eegChart.setPinchZoom(true)
        eegChart.setDrawGridBackground(false)
        eegChart.description.isEnabled = false
        eegChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        eegChart.axisRight.isEnabled = false

        eegChart.invalidate()
    }



    private fun loadData() {
        val csvFileReader = CSVFileReader()
        eegDataList = csvFileReader.readCSVFile(requireContext(), "eeg_data.csv")
        Log.d("HomeFragment", "Data size: ${eegDataList.size}")
        eegDataList.forEach {
            Log.d("HomeFragment", "Data point: ${it.timestamp}, ${it.value}")
        }
    }

    private fun startRealTimeUpdates() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex < eegDataList.size) {
                    val dataPoint = eegDataList[currentIndex]
                    Log.d("HomeFragment", "Processing entry: ${dataPoint.timestamp}, ${dataPoint.value}")

                    try {
                        val entry = Entry(dataPoint.timestamp, dataPoint.value)
                        val dataSet = eegChart.data.getDataSetByIndex(0)

                        dataSet.addEntry(entry)
                        eegChart.data.notifyDataChanged()
                        eegChart.notifyDataSetChanged()
                        eegChart.invalidate()

                        Log.d("HomeFragment", "Added entry: ${dataPoint.timestamp}, ${dataPoint.value}")
                        currentIndex++
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error adding entry: ${dataPoint.timestamp}, ${dataPoint.value}", e)
                    }
                }
                handler.postDelayed(this, 1000) // Update every second
            }
        }, 1000)
    }
    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // Stop the handler when the view is destroyed
    }
}
