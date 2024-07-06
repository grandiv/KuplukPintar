package id.grandiv.kuplukpintar.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

data class EEGData(val timestamp: Float, val value: Float)

class CSVFileReader {
    fun readCSVFile(context: Context, filename: String): List<EEGData> {
        val eegDataList = mutableListOf<EEGData>()
        try {
            val inputStream = context.assets.open(filename)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            bufferedReader.readLine() // skip header
            while (bufferedReader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                if (tokens.size == 2) {
                    val timestamp = tokens[0].replace("\"", "").toFloatOrNull()
                    val value = tokens[1].replace("\"", "").toFloatOrNull()
                    if (timestamp != null && value != null) {
                        eegDataList.add(EEGData(timestamp, value))
                        Log.d("CSVFileReader", "Added data point: $timestamp, $value")
                    } else {
                        Log.e("CSVFileReader", "Invalid data point: $line")
                    }
                } else {
                    Log.e("CSVFileReader", "Incorrect format: $line")
                }
            }
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return eegDataList
    }
}
