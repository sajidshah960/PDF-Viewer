package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }
    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)
}
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



        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.navigation_dashboard -> {
                    val intent = Intent(this, SetAlarmActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_home -> {
                    val intent = Intent(this, PdfViewerActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_notifications -> {
                    val intent = Intent(this, CaptureImageActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.navigation_fragments -> {
                    val intent = Intent(this, MainActivityTabView::class.java)
                    startActivity(intent)
                    true
                }

                else -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    false
                }

            }
        }
    }
}


