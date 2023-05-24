package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create alarm button
        val actionAlarmButton = findViewById<Button>(R.id.btn_create_alarm)
        actionAlarmButton.setOnClickListener {
            val intent = Intent(this, SetAlarmActivity::class.java)
            startActivity(intent)
        }

        // view pdf button
        val actionViewPdfButton = findViewById<Button>(R.id.btn_view_pdfs)
        actionViewPdfButton.setOnClickListener {
            val intent = Intent(this, PdfViewerActivity::class.java)
            startActivity(intent)
        }

        // take photo button
        val actionTakePhotoButton = findViewById<Button>(R.id.btn_capture_image)
        actionTakePhotoButton.setOnClickListener {
            val intent = Intent(this, CaptureImageActivity::class.java)
            startActivity(intent)
        }
    }

}


