package com.funsoltech.pdfviewer

import android.R.attr.height
import android.R.attr.width
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


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



    @SuppressLint("MissingInflatedId", "UseSwitchCompatOrMaterialCode", "CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capture_image)

        // capture button
        // browse image button
        var browserButton = findViewById<Button>(R.id.btn_browser_image)
        browserButton.setOnClickListener {
            browseImageFiles()
        }

        val imageView = findViewById<ImageView>(R.id.imageView)
        var toggleButton = findViewById<Switch>(R.id.toggleButton)
        var imageLink =
            "https://moneymint.com/wp-content/uploads/2022/04/Best-URL-Shortener-to-Earn-Money-Online-1024x683.png"
        var bitmap: Bitmap? = null
        // load image in bitmap
        GlobalScope.launch(Dispatchers.IO) {
            // Load the bitmap on a background thread
            val futureTarget: FutureTarget<Bitmap> = Glide.with(this@CaptureImageActivity)
                .asBitmap()
                .load(imageLink)
                .timeout(10000)
                .submit()

            bitmap= futureTarget.get()

            // Display the bitmap on the UI thread
            withContext(Dispatchers.Main) {
                imageView.setImageBitmap(bitmap)
            }
        }



        toggleButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Glide.with(this)
                    .load(bitmap)
                    .transform(BlurTransformation(25, 3))
                    .into(imageView)
            } else {
                // Remove blur effect
                imageView.setImageBitmap(bitmap)
            }
        }
    }


    private fun browseImageFiles() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        browserImageLauncher.launch(intent)

    }
}

