package com.example.appdemophotoeditor

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdemophotoeditor.utils.BitmapUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    private var image_taken_by_camera_uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_start)
        val appLogoBitmap = BitmapUtils.getBitmapFromAssets(this, "app_logo.png", 240, 240)
        app_logo.setImageBitmap(appLogoBitmap)
        btn_open_image?.setOnClickListener { openImageFromGallery() }
        btn_open_camera?.setOnClickListener { openCamera() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (resultCode == MainActivity.PERMISSION_PICK_IMAGE) {
                val intent = Intent(this, MainActivity::class.java)
                intent.data = data?.data
                startActivity(intent)
            } else if (requestCode == MainActivity.CAMERA_REQUEST) {
                val intent = Intent(this, MainActivity::class.java)
                intent.data = image_taken_by_camera_uri
                startActivity(intent)
            }
        }
    }

    private fun openCamera() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New Picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                        image_taken_by_camera_uri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                        val cameraIntent =
                            Intent(MediaStore.EXTRA_OUTPUT, image_taken_by_camera_uri)
                        startActivityForResult(cameraIntent, MainActivity.CAMERA_REQUEST)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Permission Denied! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            })
            .withErrorListener {
                Toast.makeText(
                    applicationContext,
                    "Error occurred! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .check()
    }

    private fun openImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, MainActivity.PERMISSION_PICK_IMAGE)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Permission Denied! ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }

            })
            .withErrorListener {
                Toast.makeText(
                    applicationContext,
                    "Error occurred! ",
                    Toast.LENGTH_SHORT
                ).show()
            }
            .check()
    }
}