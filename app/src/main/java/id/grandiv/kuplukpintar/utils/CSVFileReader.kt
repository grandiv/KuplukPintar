package id.grandiv.kuplukpintar.utils

import android.content.Context
import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader

data class EEGData(val timestamp: Float, val values: Map<String, Float>)

class CSVFileReader {
    fun readCSVFile(context: Context, filename: String): List<EEGData> {
        val eegDataList = mutableListOf<EEGData>()
        try {
            val inputStream = context.assets.open(filename)
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line: String?
            val header = bufferedReader.readLine().split(",") // read header
            val channels = header.drop(1) // skip timestamp

            while (bufferedReader.readLine().also { line = it } != null) {
                val tokens = line!!.split(",")
                val timestamp = tokens[0].replace("\"", "").toFloatOrNull()
                if (timestamp != null) {
                    val values = mutableMapOf<String, Float>()
                    for (i in 1 until tokens.size) {
                        val value = tokens[i].replace("\"", "").toFloatOrNull()
                        if (value != null) {
                            values[channels[i - 1]] = value
                        } else {
                            Log.e("CSVFileReader", "Invalid data point: $line")
                            continue
                        }
                    }
                    eegDataList.add(EEGData(timestamp, values))
                    Log.d("CSVFileReader", "Added data point: $timestamp, $values")
                } else {
                    Log.e("CSVFileReader", "Invalid timestamp: $line")
                }
            }
            bufferedReader.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return eegDataList
    }
}