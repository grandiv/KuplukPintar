package id.grandiv.kuplukpintar.utils

import android.content.Context
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class TFLiteModel(context: Context, modelPath: String) {
    val interpreter: Interpreter

    init {
        interpreter = Interpreter(loadModelFile(context, modelPath))
        logModelInputOutputShape()
    }

    @Throws(IOException::class)
    private fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
        val assetFileDescriptor = context.assets.openFd(modelPath)
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val declaredLength = assetFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun logModelInputOutputShape() {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        Log.d("TFLiteModel", "Model Input Shape: ${inputShape.contentToString()}")
        Log.d("TFLiteModel", "Model Output Shape: ${outputShape.contentToString()}")
    }

    fun predict(inputData: FloatArray): FloatArray {
        val inputShape = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        val outputData = Array(outputShape[0]) { FloatArray(outputShape[1]) }

        val inputBuffer = ByteBuffer.allocateDirect(inputData.size * 4).order(ByteOrder.nativeOrder())
        inputBuffer.asFloatBuffer().put(inputData)

        interpreter.run(inputBuffer, outputData)

        return outputData[0]
    }
}
