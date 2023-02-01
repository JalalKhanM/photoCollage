package com.myapplication.testing

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.collage_special.MainActivity
import com.example.collage_special.SelectImageActivity

class Testting : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testting)

        val intent = Intent(this, SelectImageActivity::class.java)
        startActivity(intent)
    }
}