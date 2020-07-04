package com.allmediafilepicker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.allmediafilepicker.databinding.ActivityFilePickerBinding
import com.bb.allmediafilepicker.fileupload.FileUploadModel
import com.bb.allmediafilepicker.utils.MediaPicker

class FilePickerActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilePickerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_file_picker)

        iniViews()
    }

    fun iniViews() {
        binding.fileUploadView.setupWithActivityOrFragment(
            activity = this@FilePickerActivity,
            fileUploadModel = FileUploadModel(
                title = "Attachments",
                customRequestCode = 123,
                minFilesCount = 1,
                maxFilesCount = 2,
                allowMultipleImages = false,
                requiresCrop = true,
                requiresVideoCompress = true,
                mediaType = MediaPicker.MEDIA_TYPE_IMAGE or MediaPicker.MEDIA_TYPE_VIDEO,
                action = MediaPicker.ACTION_TYPE_CAMERA or MediaPicker.ACTION_TYPE_GALLERY
            )
        )
    }

    /**
     * To give media response back to file upload view
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        binding.fileUploadView.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * To give media permission response back to file upload view
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        binding.fileUploadView.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}