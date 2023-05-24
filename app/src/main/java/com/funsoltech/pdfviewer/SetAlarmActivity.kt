package com.funsoltech.pdfviewer

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.AlarmClock
import android.util.Log
import android.widget.Button
import android.widget.Toast

class SetAlarmActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_alarm)

        // add create alarm button here
        var btn = findViewById<Button>(R.id.btn_create_alarm)
        /*btn.setOnClickListener {
            if (checkSelfPermission(AlarmClock.ACTION_SET_ALARM) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                createAlarm("Alarm", 12, 0)
            }
            else{
                requestPermissions(arrayOf(AlarmClock.ACTION_SET_ALARM), 1)
            }
        }*/
        btn.setOnClickListener {
            createAlarm("Alarm", 12, 0)
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

    private fun createAlarm(message: String, hour: Int, minutes: Int) {
        val intent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
            putExtra(AlarmClock.EXTRA_MESSAGE, message)
            putExtra(AlarmClock.EXTRA_HOUR, hour)
            putExtra(AlarmClock.EXTRA_MINUTES, minutes)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
            Toast.makeText(this, "Alarm created.", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(this, "No alarm app found.", Toast.LENGTH_SHORT).show()
        }
    }
}