package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewStub
import android.widget.ProgressBar
import android.widget.TextView


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var viewStub: ViewStub
    private lateinit var progressBar: ProgressBar

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        Log.i("PDF_VIEWER", "onSaveInstanceState")
    }


    override fun onRestoreInstanceState(
        savedInstanceState: Bundle,
    ) {
        super.onRestoreInstanceState(savedInstanceState)

        onSaveInstanceState(savedInstanceState).run {
            Log.i("PDF_VIEWER", "onRestoreInstanceState")
        }
    }
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        viewStub = findViewById(R.id.stub_import)


        // view stub button
        val actionAlarmButton = findViewById<TextView>(R.id.view_stub_button)
        actionAlarmButton.setOnClickListener {
            progressBar = findViewById(R.id.progressBar)
            progressBar.visibility = View.VISIBLE
            inflateViewStub()
            actionAlarmButton.visibility = View.GONE
            // delay 3 seconds
            Handler().postDelayed({
                progressBar.visibility = View.GONE
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }, 3000)
        }

        // main activity button
        val actionViewPdfButton = findViewById<TextView>(R.id.main_screen_button)
        actionViewPdfButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


    }
    private fun inflateViewStub() {
        // Inflate the ViewStub
        val inflatedView: View = viewStub.inflate()
        val textView = inflatedView.findViewById<TextView>(R.id.textView)
        textView.text = "ViewStub inflated"

    }


}