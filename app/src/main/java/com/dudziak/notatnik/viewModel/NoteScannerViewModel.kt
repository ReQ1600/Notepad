package com.dudziak.notatnik.viewModel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.common.InputImage
import java.io.File
import androidx.camera.core.ImageCaptureException
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class NoteScannerViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private val _content = mutableStateOf<String?>(null)
        val content: State<String?> = _content
    }

    private val _context = application.applicationContext
    private val _outputDir = _context.cacheDir
    private val _executor = ContextCompat.getMainExecutor(_context)

    val imageCapture = ImageCapture.Builder().build()

    fun takeAndProcessPhoto(){
        val photoFile = File(_outputDir, "note_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            _executor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    try {
                        val image = InputImage.fromFilePath(_context, Uri.fromFile(photoFile))
                        processImg(image)
                    } catch (e: Exception) {
                        Log.e("OCR", "Photo processing error", e)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("Camera", "Saving photo error", exception)
                }
            }
        )
    }

    private fun processImg(image: InputImage) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val txt = visionText.text
                _content.value = txt
            }
            .addOnFailureListener {
                _content.value = null
                Log.e("OCR", "OCR error", it)
            }
    }
}