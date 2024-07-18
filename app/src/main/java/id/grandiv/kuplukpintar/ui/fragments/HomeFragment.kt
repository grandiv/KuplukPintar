package id.grandiv.kuplukpintar.ui.fragments

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    // Variables to store timestamps
    private var lastMicroseizureTime: Long? = null
    private var lastSeizureTime: Long? = null

    // Variables for smoothing predictions
    private val predictionWindowSize = 5
    private val predictionWindow = mutableListOf<Int>()

    // Variables for ringtone and vibration management
    private var ringtone: Ringtone? = null
    private var currentStatus: String? = null
    private var vibrationHandler: Handler? = null
    private var ringtoneHandler: Handler? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        eegChart = view.findViewById(R.id.eegChart)
        currentStatusTextView = view.findViewById(R.id.tv_current_status)
        lastMicroseizureTextView = view.findViewById(R.id.tv_last_microseizure)
        lastSeizureTextView = view.findViewById(R.id.tv_last_seizure)

        tfliteModel = TFLiteModel(requireContext(), "v8_seizure_prediction_model.tflite")

        loadData()
        setupChart()
        startRealTimeUpdates()

        return view
    }

    private fun setupChart() {
        val lineData = LineData()
        val colors = listOf(
            R.color.red, R.color.green, R.color.blue, R.color.yellow, R.color.purple,
            R.color.darkpurple, R.color.darkgreen, R.color.darkred, R.color.darkblue, R.color.darkyellow
        )
        var colorIndex = 0

        for (channel in eegDataList[0].values.keys) {
            val dataSet = LineDataSet(eegDataMap[channel], channel)
            dataSet.setDrawValues(false)
            dataSet.setDrawCircles(false)
            dataSet.color = resources.getColor(colors[colorIndex], null)
            colorIndex = (colorIndex + 1) % colors.size
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

    private fun normalize(data: FloatArray): FloatArray {
        val mean = floatArrayOf(0.09955387f, -0.09428317f, 0.05470202f, 0.01383419f, 0.09847564f, -0.01032247f,
            -0.06579117f, -0.10411164f, -0.03822606f, -0.00253246f)
        val std = floatArrayOf(
            7.7848763f, 8.057112f, 7.74029f, 7.953238f, 7.6843634f, 7.855832f,
            7.609452f, 7.830898f, 7.668156f, 7.869738f
        )

        return data.mapIndexed { index, value -> (value - mean[index]) / std[index] }.toFloatArray()
    }

    private fun extractFeatures(dataPoint: EEGData): FloatArray {
        val featureVector = mutableListOf<Float>()
        for (channel in dataPoint.values.keys) {
            val value = dataPoint.values[channel] ?: 0f
            featureVector.add(value)
        }

        val inputShape = tfliteModel.interpreter.getInputTensor(0).shape()
        val inputLength = inputShape[1]

        if (featureVector.size < inputLength) {
            while (featureVector.size < inputLength) {
                featureVector.add(0f)
            }
        }

        if (featureVector.size > inputLength) {
            featureVector.subList(inputLength, featureVector.size).clear()
        }

        if (featureVector.size != inputLength) {
            throw IllegalArgumentException("Expected input length $inputLength but got ${featureVector.size}")
        }

        return normalize(featureVector.toFloatArray())
    }

    private fun startRealTimeUpdates() {
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (currentIndex < eegDataList.size) {
                    val rawDataPoint = eegDataList[currentIndex]
                    Log.d("HomeFragment", "Processing entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}")

                    try {
                        for ((channel, value) in rawDataPoint.values) {
                            val entry = Entry(rawDataPoint.timestamp, value) // Use timestamp instead of currentIndex
                            val dataSet = eegChart.data.getDataSetByLabel(channel, true) as LineDataSet

                            dataSet.addEntry(entry)
                            eegChart.data.notifyDataChanged()
                        }
                        eegChart.notifyDataSetChanged()
                        eegChart.invalidate()

                        val inputData = extractFeatures(rawDataPoint)
                        Log.d("HomeFragment", "Input data for prediction: ${inputData.joinToString()}")

                        val prediction = tfliteModel.predict(inputData)
                        Log.d("HomeFragment", "Model prediction: ${prediction.joinToString()}")

                        val maxIndex = prediction.indices.maxByOrNull { prediction[it] } ?: -1

                        if (predictionWindow.size == predictionWindowSize) {
                            predictionWindow.removeAt(0)
                        }
                        predictionWindow.add(maxIndex)

                        val smoothedPrediction = predictionWindow.groupBy { it }.maxByOrNull { it.value.size }?.key ?: -1

                        val status = when (smoothedPrediction) {
                            0 -> "Normal"
                            1 -> "Microseizure"
                            2 -> "Seizure"
                            else -> "Unknown"
                        }

                        if (currentStatus != status) {
                            currentStatus = status
                            triggerAlert(status)
                        }

                        currentStatusTextView.text = status
                        when (status) {
                            "Normal" -> {
                                currentStatusTextView.setBackgroundResource(R.drawable.sh_status_normal)
                                stopAlert()
                            }
                            "Microseizure" -> {
                                currentStatusTextView.setBackgroundResource(R.drawable.sh_status_microseizure)
                                lastMicroseizureTime = System.currentTimeMillis()
                                updateLastMicroseizureTextView()
                            }
                            "Seizure" -> {
                                currentStatusTextView.setBackgroundResource(R.drawable.sh_status_seizure)
                                lastSeizureTime = System.currentTimeMillis()
                                updateLastSeizureTextView()
                            }
                        }

                        Log.d("HomeFragment", "Added entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}")
                        currentIndex++
                    } catch (e: Exception) {
                        Log.e("HomeFragment", "Error adding entry: ${rawDataPoint.timestamp}, ${rawDataPoint.values}", e)
                    }
                }
                handler.postDelayed(this, 100)
            }
        }, 100)
    }

    private fun updateLastMicroseizureTextView() {
        lastMicroseizureTime?.let {
            val dateFormat = SimpleDateFormat("HH:mm:ss | dd-MM-yyyy", Locale.getDefault())
            lastMicroseizureTextView.text = dateFormat.format(Date(it))
        }
    }

    private fun updateLastSeizureTextView() {
        lastSeizureTime?.let {
            val dateFormat = SimpleDateFormat("HH:mm:ss | dd-MM-yyyy", Locale.getDefault())
            lastSeizureTextView.text = dateFormat.format(Date(it))
        }
    }

    private fun triggerAlert(type: String) {
        val vibrator = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val pattern = when (type) {
            "Microseizure" -> longArrayOf(0, 500, 200, 500) // Vibration pattern for microseizure
            "Seizure" -> longArrayOf(0, 1000, 500, 1000, 500, 1000) // Vibration pattern for seizure
            else -> return
        }

        if (vibrator.hasVibrator()) {
            vibrationHandler?.removeCallbacksAndMessages(null)
            vibrationHandler = Handler(Looper.getMainLooper())
            vibrationHandler?.post(object : Runnable {
                override fun run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
                    } else {
                        vibrator.vibrate(pattern, -1)
                    }
                    vibrationHandler?.postDelayed(this, pattern.sum())
                }
            })
        }

        // Stop the previous ringtone if it's playing
        ringtone?.stop()

        val notificationUri: Uri = when (type) {
            "Microseizure" -> Uri.parse("android.resource://${requireContext().packageName}/raw/microseizure_ringtone")
            "Seizure" -> Uri.parse("android.resource://${requireContext().packageName}/raw/seizure_ringtone")
            else -> return
        }

        ringtoneHandler?.removeCallbacksAndMessages(null)
        ringtoneHandler = Handler(Looper.getMainLooper())
        ringtoneHandler?.post(object : Runnable {
            override fun run() {
                ringtone = RingtoneManager.getRingtone(requireContext(), notificationUri)
                ringtone?.play()
                ringtoneHandler?.postDelayed(this, 5000) // Repeat after 5 seconds
            }
        })
    }

    private fun stopAlert() {
        vibrationHandler?.removeCallbacksAndMessages(null)
        ringtoneHandler?.removeCallbacksAndMessages(null)
        ringtone?.stop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        stopAlert()
    }
}
