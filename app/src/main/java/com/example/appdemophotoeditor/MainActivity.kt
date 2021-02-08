package com.example.appdemophotoeditor

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.appdemophotoeditor.`interface`.*
import com.example.appdemophotoeditor.fragment.FiltersListFragment
import com.example.appdemophotoeditor.utils.BitmapUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import com.zomato.photofilters.imageprocessors.Filter
import ja.burhanrashid52.photoeditor.PhotoEditor
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity(), FiltersListFragmentListener, EditImageFragmentListener, AddTextFragmentListener,BrushFragmentListener,EmojiFragmentListener {
    companion object {
        val pictureName: String? = "IU.jpg"
        const val PERMISSION_PICK_IMAGE = 1000
        const val PERMISSION_INSERT_IMAGE = 1001
        const val CAMERA_REQUEST = 1002
    }

    var image_selected_uri: Uri? = null
    private var onEdit = false
    private var originalBitmap: Bitmap? = null
    private var filteredBitmap: Bitmap? = null
    private var finalBitmap: Bitmap? = null
    private var filtersListFragment: FiltersListFragment? = null
    private var photoEditor: PhotoEditor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        photoEditor = PhotoEditor.Builder(this, image_preview)
            .setPinchTextScalable(true)
            .setDefaultEmojiTypeface(Typeface.createFromAsset(assets, "emojione-android.ttf"))
            .build()

        loadImage()
        image_selected_uri = intent.data
        if (image_selected_uri != null) {
            setData(image_selected_uri)
        }
    }

    private fun openImage(path: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path), "image/*")
        startActivity(intent)
    }

    private fun loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName!!, 100, 100)
        filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        image_preview?.source?.setImageBitmap(originalBitmap)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            openImageFromGallery()
            return true
        } else if (id == R.id.action_save && onEdit) {
            saveImageToGallery()
            return true
        } else if (id == R.id.action_camera) {
            openCamera()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveImageToGallery() {
        //save image
    }

    private fun setData(uri: Uri?) {
        edit_side?.visibility = View.VISIBLE
        onEdit = true
        val bitmap = BitmapUtils.getBitmapFromGallery(this, uri!!, 800, 800)
        originalBitmap?.recycle()
        finalBitmap?.recycle()
        filteredBitmap?.recycle()
        originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        image_preview.source.setImageBitmap(originalBitmap)
        bitmap?.recycle()
        filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
        filtersListFragment?.let { FiltersListFragment.setListener(it, this) }
    }

    private fun openImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    if (report!!.areAllPermissionsGranted()) {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        startActivityForResult(intent, PERMISSION_PICK_IMAGE)
                    } else {
                        Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest?>?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            })
            .check()
    }

    private fun openCamera() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    if (p0!!.areAllPermissionsGranted()) {
                        val values = ContentValues()
                        values.put(MediaStore.Images.Media.TITLE, "New picture")
                        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                        image_selected_uri = contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_selected_uri)
                        startActivityForResult(cameraIntent, CAMERA_REQUEST)
                    } else {
                        Toast.makeText(applicationContext, "Permission Denied!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    p1?.continuePermissionRequest()
                }
            })
            .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            edit_side.visibility = View.VISIBLE
            onEdit = true
            if (requestCode == PERMISSION_PICK_IMAGE) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, data?.data!!, 800, 800)
                image_selected_uri = data.data
                originalBitmap?.recycle()
                finalBitmap?.recycle()
                filteredBitmap?.recycle()
                originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                finalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                filteredBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                bitmap?.recycle()
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
                filtersListFragment?.let { FiltersListFragment.setListener(it, this) }

            } else if (requestCode == PERMISSION_INSERT_IMAGE) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, data?.data!!, 250, 250)
                photoEditor?.addImage(bitmap)
            }

            if (requestCode == CAMERA_REQUEST) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, image_selected_uri!!, 800, 800)
                originalBitmap?.recycle()
                finalBitmap?.recycle()
                filteredBitmap?.recycle()
                originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                finalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                filteredBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                image_preview?.source?.setImageBitmap(originalBitmap)
                bitmap?.recycle()
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
                filtersListFragment?.let { FiltersListFragment.setListener(it, this) }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data)
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            handleCropError(data)
        }

    }

    private fun handleCropError(data: Intent?) {
        val cropError = UCrop.getError(data!!)
        if (cropError != null) {
            Toast.makeText(this, "" + cropError.message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "" + "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCropResult(data: Intent?) {
        val resultUri = UCrop.getOutput(data!!)
        if (resultUri != null) {
            image_preview.source.setImageURI(resultUri)
            val bitmap = (image_preview?.source?.drawable as BitmapDrawable).bitmap
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredBitmap = originalBitmap
            finalBitmap = originalBitmap
        } else {
            Toast.makeText(applicationContext, "Cannot retrieve crop image", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onFilterSelected(filter: Filter) {

    }

    override fun onBrightnessChanged(brightness: Int) {

    }

    override fun onSaturationChanged(saturation: Float) {

    }

    override fun onConstraintChanged(constraint: Float) {

    }

    override fun onEditStarted() {

    }

    override fun onEditCompleted() {

    }

    override fun onAddTextButtonClick(typeface: Typeface, text: String, color: Int) {

    }

    override fun onBrushSizeChangeListener(size: Float) {

    }

    override fun onBrushOpacityChangeListener(opacity: Int) {

    }

    override fun onBrushColorChangeListener(color: Int) {

    }

    override fun onBrushStateChangeListener(isEraser: Boolean) {

    }

    override fun onEmojiSelected(emoji: String) {

    }
}