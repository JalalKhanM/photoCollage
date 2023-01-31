package com.example.collage_special

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mobi.collage.R

class MainActivity : AppCompatActivity() {

    lateinit var collage_img: ImageView

    companion object {
        var isFromSaved: Boolean = true
    }

    fun ImagesPath(): ArrayList<String> {
        var uri: Uri
        var cursor: Cursor
        var column_index_data: Int

        var listOfAllImages = ArrayList<String>()
        var absolutePathOfImage: String? = null
        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        var projection = arrayOf(MediaStore.MediaColumns.DATA)

        cursor =
            contentResolver.query(
                uri,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
            )!!

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)

        while (cursor.moveToNext()) {
            absolutePathOfImage = cursor.getString(column_index_data)
            listOfAllImages.add(absolutePathOfImage)
        }
        return listOfAllImages
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        collage_img = findViewById(R.id.btn_collage)

        requestPermission()

        collage_img.setOnClickListener(View.OnClickListener {
            if (chechkPermission()) {
                checkClick()
                var intent = Intent(this, SelectImageActivity::class.java)
                startActivity(intent)
            }
        })
    }

    private fun chechkPermission(): Boolean {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) ==
                    PackageManager.PERMISSION_GRANTED
        }
        else{
            return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        }

    }

    private fun requestPermission() {


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                100
            )
        }
        else{
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA
                ),
                100
            )
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("Permission", "Granted")
//                setAdapter()
            } else {
                Log.e("Permission", "Denied")

                requestPermission()
            }
        }
    }
    private var mLastClickTime: Long = 0
    fun checkClick() {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return
        }
        mLastClickTime = SystemClock.elapsedRealtime()
    }

    private var mCapturedImageUri: Uri? = null


}