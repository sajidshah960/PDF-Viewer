package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile

class CaptureImageActivity : AppCompatActivity() {
    private val browserImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    val documentFile = DocumentFile.fromSingleUri(this, uri)
                    if (documentFile != null && documentFile.type?.startsWith("image/") == true) {
                        openImage(uri)
                    } else {
                        Toast.makeText(this, "Selected file is not a valid image file.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    private fun openImage(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "image/*")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, "No image viewer app found.", Toast.LENGTH_SHORT).show()
        }
    }



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        // capture button
        // browse image button
        var browserButton = findViewById<Button>(R.id.btn_browser_image)
        browserButton.setOnClickListener {
            browseImageFiles()
        }

        var captureButton = findViewById<Button>(R.id.btn_capture_image)
        captureButton.setOnClickListener {
            captureImage()
        }

    }

    private fun captureImage() {
        // open camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(intent)
    }

    private fun browseImageFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        browserImageLauncher.launch(intent)

    }

}