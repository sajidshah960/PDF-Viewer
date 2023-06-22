package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Process
import android.provider.AlarmClock
import android.provider.MediaStore
import android.provider.Settings
import android.text.format.DateUtils
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.documentfile.provider.DocumentFile
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar

// import appUsageStats.AppsUsageStats

class MainActivity : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView

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

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // create alarm button

        // app stats
        var appStats  = AppsUsageStats(this)

        appStats.fetchUsageStats()

        val actionAlarmButton = findViewById<Button>(R.id.btn_create_alarm)
        actionAlarmButton.setOnClickListener {
            createAlarm("Alarm", 12, 0)
        }

        // view pdf button
        val actionViewPdfButton = findViewById<Button>(R.id.btn_view_pdfs)
        actionViewPdfButton.setOnClickListener {
            browsePdfFiles()
        }

        // take photo button
        val actionTakePhotoButton = findViewById<Button>(R.id.btn_capture_image)
        actionTakePhotoButton.setOnClickListener {
            captureImage()
        }

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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

    class AppsUsageStats(private val context: Context) {
        fun fetchUsageStats() {
            if (!hasPermission()) {
                requestPermission()
                return
            }

            val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
            if (usageStatsManager != null) {
                val calendar = Calendar.getInstance()
                val endTime = calendar.timeInMillis
                calendar.add(Calendar.DAY_OF_MONTH, -1) // Fetch usage for the last day
                val startTime = calendar.timeInMillis

                val usageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, startTime, endTime).sortedBy { it.totalTimeInForeground }.reversed()
                if (usageStats != null && usageStats.isNotEmpty()) {
                    for (stat in usageStats) {
                        val packageName = stat.packageName
                        val totalTimeInForeground = stat.totalTimeInForeground
                        Log.d("AppUsageFetcher", "Package: $packageName, Time: ${DateUtils.formatElapsedTime(totalTimeInForeground / 1000)}")
                    }
                }
            } else {
                Log.e("AppUsageFetcher", "UsageStatsManager is null.")
            }
        }

        private fun hasPermission(): Boolean {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            val mode = appOps?.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), context.packageName)
            return mode == AppOpsManager.MODE_ALLOWED
        }

        private fun requestPermission() {
            Toast.makeText(context, "Please grant usage access permission", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            context.startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            if (grantResults.isNotEmpty() && grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED){
                createAlarm("Alarm", 12, 0)
            }
            else{
                Toast.makeText(this, "Permission denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun browsePdfFiles() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
        }
        viewPdfLauncher.launch(intent)
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

    private fun captureImage() {
        // open camera app
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivity(intent)
    }

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
            Toast.makeText(this, "No alarm app found.", Toast.LENGTH_SHORT).show()
        }
    }
}


