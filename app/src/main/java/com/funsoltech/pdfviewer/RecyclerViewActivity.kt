package com.funsoltech.pdfviewer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Environment.getExternalStorageDirectory
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.ArrayAdapter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.funsoltech.pdfviewer.databinding.ActivityRecyclerViewBinding
import java.io.File

class RecyclerViewActivity : AppCompatActivity() {
    private val MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1
    private val READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2
    private lateinit var currentDirectory: File
    private lateinit var binding: ActivityRecyclerViewBinding
    private lateinit var filesList: MutableList<File>
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRecyclerViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, mutableListOf<String>())
        binding.filesTreeView.adapter = adapter
        binding.filesTreeView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = filesList[position]
            openSelectedFile(selectedItem)
        }

        if (checkStoragePermission()) {
            open(getExternalStorageDirectory())
        } else {
            requestStoragePermission()
        }
    }

    private fun openSelectedFile(selectedItem: File) {
        if (selectedItem.isFile) {
            return openFile(this, selectedItem)
        }
    }

    private fun open(selectedItem: File) {
        filesList = getPDFFiles().toMutableList()
        adapter.clear()
        adapter.addAll(filesList.map {
            renderItem(this, it) + it.name
        })
        adapter.notifyDataSetChanged()
    }

    private fun getPDFFiles(): List<File> {
        val pdfFiles = mutableListOf<File>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATA
        )

        val selection = "${MediaStore.Files.FileColumns.MIME_TYPE} = ? OR ${MediaStore.Files.FileColumns.MIME_TYPE} = ?"
        val selectionArgs = arrayOf("application/pdf", "application/pdf")

        val queryUri = MediaStore.Files.getContentUri("external")

        contentResolver.query(queryUri, projection, selection, selectionArgs, null)?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val data = cursor.getString(dataColumn)

                val file = File(data)
                if (file.exists() && file.isFile) {
                    pdfFiles.add(file)
                }
            }
        }
        Log.i("PDF_VIEWER", "getPDFFiles: ${pdfFiles.size}")
        return pdfFiles
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        // runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent()
                intent.addCategory("android.intent.category.DEFAULT")
                intent.data = android.net.Uri.parse(
                    String.format(
                        "package:%s",
                        applicationContext.packageName
                    )
                )
                startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
            } catch (e: Exception) {
                val intent = Intent()
                intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
                startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                open(getExternalStorageDirectory())
            }
        }
    }
}
