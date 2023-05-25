package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile


class PdfViewerActivity : AppCompatActivity() {
    private val viewPdfLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
                    val documentFile = DocumentFile.fromSingleUri(this, uri)
                    if (documentFile != null && documentFile.type?.startsWith("application/pdf") == true) {
                        viewPdfFile(uri)
                    } else {
                        Toast.makeText(this, "Selected file is not a valid PDF file.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    lateinit var viewPdfButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        // view pdf button
        viewPdfButton = findViewById(R.id.btn_view_pdf)
        viewPdfButton.setOnClickListener {
            browsePdfFiles()
        }

        // recycler view button
        val actionRecyclerViewButton = findViewById<Button>(R.id.btn_recycler_view)
        actionRecyclerViewButton.setOnClickListener {
            val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        // slide show button
        val actionSlideShowButton = findViewById<Button>(R.id.btn_slide_show)
        actionSlideShowButton.setOnClickListener {
            val intent = Intent(this, SlideShowActivity::class.java)
            startActivity(intent)
        }
}




    private fun browsePdfFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        viewPdfLauncher.launch(intent)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun viewPdfFile(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Log.e("PDF_VIEWER", "No PDF viewer app found.")
        }
    }
}
