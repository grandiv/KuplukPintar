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
import id.grandiv.kuplukpintar.utils.TFLiteModel
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.io.IOException

class HomeFragment : Fragment() {
    private lateinit var eegChart: LineChart
    private lateinit var currentStatusTextView: TextView
    private lateinit var lastMicroseizureTextView: TextView
    private lateinit var lastSeizureTextView: TextView
    private val eegDataMap = mutableMapOf<String, MutableList<Entry>>()
    private var currentIndex = 0
    private lateinit var tfliteModel: TFLiteModel
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

        try {
            tfliteModel = TFLiteModel(requireContext(), "v2_seizure_prediction_model.tflite")
        } catch (e: IOException) {
            e.printStackTrace()
        }

        loadData()
        setupChart()
        startRealTimeUpdates()

        return view
    }

    private fun setupChart() {
        val lineData = LineData()

        for (channel in eegDataList[0].values.keys) {
            val dataSet = LineDataSet(eegDataMap[channel], channel)
            dataSet.setDrawValues(false)
            dataSet.setDrawCircles(false)
            eegDataMap[channel] = mutableListOf()
            lineData.addDataSet(dataSet)
        }

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
        eegDataList = csvFileReader.readCSVFile(requireContext(), "new4_eeg_data.csv")
        Log.d("HomeFragment", "Raw data size: ${eegDataList.size}")
    }

    private fun extractFeatures(dataPoint: EEGData): FloatArray {
        val featureVector = mutableListOf<Float>()
        for (channel in dataPoint.values.keys) {
            val value = dataPoint.values[channel] ?: 0f
            featureVector.add(value)
        }

        val inputShape = tfliteModel.interpreter.getInputTensor(0).shape()
        val inputLength = inputShape[1]

        // If featureVector size is less than inputLength, pad with zeros
        if (featureVector.size < inputLength) {
            while (featureVector.size < inputLength) {
                featureVector.add(0f)
            }
        }

        // If featureVector size is greater than inputLength, truncate the list
        if (featureVector.size > inputLength) {
            featureVector.subList(inputLength, featureVector.size).clear()
        }

        if (featureVector.size != inputLength) {
            throw IllegalArgumentException("Expected input length $inputLength but got ${featureVector.size}")
        }

        return featureVector.toFloatArray()
    }


    private fun startRealTimeUpdates() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex < eegDataList.size) {
                    val rawDataPoint = eegDataList[currentIndex]
                    Log.d(
                        "HomeFragment",
                        "Processing entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}"
                    )

                    try {
                        for ((channel, value) in rawDataPoint.values) {
                            val entry = Entry(rawDataPoint.timestamp, value)
                            val dataSet =
                                eegChart.data.getDataSetByLabel(channel, true) as LineDataSet

                            dataSet.addEntry(entry)
                            eegChart.data.notifyDataChanged()
                        }
                        eegChart.notifyDataSetChanged()
                        eegChart.invalidate()

                        // Extract features from raw data
                        val inputData = extractFeatures(rawDataPoint)
                        val prediction = tfliteModel.predict(inputData)

                        // Determine the status based on prediction
                        val maxIndex = prediction.withIndex().maxByOrNull { it.value }?.index
                        val status = when (maxIndex) {
                            0 -> "Normal"
                            1 -> "Microseizure"
                            2 -> "Seizure"
                            else -> "Unknown"
                        }

                        // Update UI accordingly
                        currentStatusTextView.text = status
                        when (status) {
                            "Normal" -> currentStatusTextView.setBackgroundResource(R.drawable.sh_status_normal)
                            "Microseizure" -> currentStatusTextView.setBackgroundResource(R.drawable.sh_status_microseizure)
                            "Seizure" -> currentStatusTextView.setBackgroundResource(R.drawable.sh_status_seizure)
                        }

                        Log.d(
                            "HomeFragment",
                            "Added entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}"
                        )
                        currentIndex++
                    } catch (e: Exception) {
                        Log.e(
                            "HomeFragment",
                            "Error adding entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}",
                            e
                        )
                    }
                }
                handler.postDelayed(this, 100) // Update every 0.1 seconds
            }
        }, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null) // Stop the handler when the view is destroyed
    }
}
