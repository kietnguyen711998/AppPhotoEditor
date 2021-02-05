package com.example.appdemophotoeditor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    companion object{
        const val PERMISSION_PICK_IMAGE = 1000
        const val PERMISSION_INSERT_IMAGE = 1001
        const val CAMERA_REQUEST = 1002
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}