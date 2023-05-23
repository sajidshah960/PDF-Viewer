package com.funsoltech.pdfviewer

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.documentfile.provider.DocumentFile
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 1
    private val WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE = 2
    private val CAMERA_PERMISSION_REQUEST_CODE = 3
    private val READ_CONTACTS_PERMISSION_REQUEST_CODE = 4
    private val READ_CALENDAR_PERMISSION_REQUEST_CODE = 5
    private val READ_EXTERNAL_STORAGE_PERMISSION = "android.permission.READ_EXTERNAL_STORAGE"
    private val WRITE_EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // button to go to pdf viewer
        val actionListButton = findViewById<Button>(R.id.recycler_view_button)
        // action to go to pdf viewer
        actionListButton.setOnClickListener {
            requestStoragePermissionAndBrowse()
        }


    }
    // create an alarm
    private fun createAlarm(message: String, hour: Int, minutes: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
        else{
            Log.i("PDF_VIEWER", "Alarm not created")
        }
    }
    // capture photo
    private val REQUEST_IMAGE_CAPTURE = 1
    private val locationForPhotos: Uri = Uri.parse("content://com.example.myapp.files/photos")

    fun capturePhoto(targetFilename: String) {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra("uri", Uri.withAppendedPath(locationForPhotos, targetFilename))
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        }
    }

   /* override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            val thumbnail = data?.extras?.get("data") as Bitmap?
            // save image to storage
            val filename = "photo.jpg"
            val file = File(getExternalFilesDir(null), filename)
            val out: OutputStream
            try {
                out = FileOutputStream(file)
                thumbnail?.compress(Bitmap.CompressFormat.JPEG, 100, out)
                out.flush()
                out.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }*/
    private fun requestStoragePermissionAndBrowse() {
        if (ContextCompat.checkSelfPermission(
                this,
                READ_EXTERNAL_STORAGE_PERMISSION
            ) == PackageManager.PERMISSION_GRANTED) {
            browsePdfFiles()
            // show pdf files in recycler view
            /*val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivity(intent)*/

        }
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(READ_EXTERNAL_STORAGE_PERMISSION),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun browsePdfFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        startActivityForResult(intent, READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                browsePdfFiles()
            } else {
                Log.e("PDF_VIEWER", "Read storage permission denied")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                val documentFile = DocumentFile.fromSingleUri(this, uri)
                if (documentFile != null && documentFile.isFile) {
                    viewPdfFile(uri)
                } else {
                    Log.e("PDF_VIEWER", "Selected file is not a valid PDF file.")
                }
            }
        }
    }

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

class PDFDoc(name: String?, uri: Uri) {
    var name: String? = null
    var uri: Uri

    init {
        this.name = name
        this.uri = uri
    }
}
