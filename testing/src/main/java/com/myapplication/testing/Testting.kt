package com.myapplication.testing



import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.collage_special.SelectImageActivity


class Testting : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testting)

        val intent = Intent(this, SelectImageActivity::class.java)
        startActivity(intent)

    }



}