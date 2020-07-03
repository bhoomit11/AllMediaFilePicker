package com.bb.allmediafilepicker.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.utils.dialog.BottomSheetDialogFragmentHelperView
import com.master.basediproject.utils.filepicker.FilePickerActivity
import com.master.permissionhelper.PermissionHelper
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import net.alhazmy13.mediapicker.Image.ImagePicker
import net.alhazmy13.mediapicker.Video.VideoPicker
import java.io.File

class MediaPicker(
    val activity: AppCompatActivity? = null,
    val fragment: Fragment? = null,
    val requiresCrop: Boolean = true,
    val mediaType: Int,
    val action: Int,
    val customRequestCode: Int = 123
) {
    lateinit var filePaths: FilePaths

    init {
        filePaths = FilePaths(if (fragment != null) fragment.context!! else activity as Context)
    }

    private val IMAGE_PICKER_REQUEST_CODE = 1
    private val IMAGE_CROP_REQUEST_CODE = 2
    private val VIDEO_PICKER_REQUEST_CODE = 3
    private val FILE_PICKER_REQUEST_CODE = 4

    companion object {

        const val ACTION_TYPE_CAMERA = 1
        const val ACTION_TYPE_GALLERY = 2
        const val ACTION_TYPE_FILE = 4

        const val MEDIA_TYPE_IMAGE = 1
        const val MEDIA_TYPE_VIDEO = 2
        const val MEDIA_TYPE_OTHER = 4

    }

    var onMediaChoose: (path: String, mediaType: Int) -> Unit = { path, mediaType -> }

    var permissionHelper: PermissionHelper? = null
    fun start(onMediaChoose: (path: String, mediaType: Int) -> Unit) {
        this.onMediaChoose = onMediaChoose
        if (activity != null || fragment != null) {
            BottomSheetDialogFragmentHelperView.with(
                R.layout.dialog_file_picker,
                isCancellable = true,
                isCancellableOnTouchOutSide = true
            ) { it, dialog ->

                val llImageCamera = it.findViewById<View>(R.id.llImageCamera)
                val llImageGallery = it.findViewById<View>(R.id.llImageGallery)
                val llVideoCamera = it.findViewById<View>(R.id.llVideoCamera)
                val llVideoGallery = it.findViewById<View>(R.id.llVideoGallery)
                val llFile = it.findViewById<View>(R.id.llFile)

                llImageCamera.visibility = View.GONE
                llImageGallery.visibility = View.GONE
                llVideoCamera.visibility = View.GONE
                llVideoGallery.visibility = View.GONE
                llFile.visibility = View.GONE

                if (action and ACTION_TYPE_CAMERA == ACTION_TYPE_CAMERA) {
                    if (mediaType and MEDIA_TYPE_IMAGE == MEDIA_TYPE_IMAGE) {
                        llImageCamera.visibility = View.VISIBLE
                    }
                    if (mediaType and MEDIA_TYPE_VIDEO == MEDIA_TYPE_VIDEO) {
                        llVideoCamera.visibility = View.VISIBLE
                    }
                }
                if (action and ACTION_TYPE_GALLERY == ACTION_TYPE_GALLERY) {
                    if (mediaType and MEDIA_TYPE_IMAGE == MEDIA_TYPE_IMAGE) {
                        llImageGallery.visibility = View.VISIBLE
                    }
                    if (mediaType and MEDIA_TYPE_VIDEO == MEDIA_TYPE_VIDEO) {
                        llVideoGallery.visibility = View.VISIBLE
                    }
                }
                if (action and ACTION_TYPE_FILE == ACTION_TYPE_FILE) {
                    llFile.visibility = View.VISIBLE
                }

                llImageCamera.setOnClickListener {
                    requestPermission(
                        it.id,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    dialog.dismiss()
                }
                llImageGallery.setOnClickListener {
                    requestPermission(
                        it.id,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                    dialog.dismiss()
                }
                llVideoCamera.setOnClickListener {
                    requestPermission(
                        it.id,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    )
                    dialog.dismiss()
                }
                llVideoGallery.setOnClickListener {
                    requestPermission(
                        it.id,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                    dialog.dismiss()
                }
                llFile.setOnClickListener {
                    requestPermission(
                        it.id,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    )
                    dialog.dismiss()
                }

            }.show(
                if (activity != null) activity.supportFragmentManager else fragment!!.childFragmentManager,
                "filepicker"
            )
        } else {
            throw RuntimeException("It Seems activity is not set")
        }
    }

    fun requestPermission(resourceId: Int, permission: Array<String>) {
        if (fragment != null)
            permissionHelper = PermissionHelper(fragment, permissions = permission)
        else if (activity != null)
            permissionHelper = PermissionHelper(activity, permissions = permission)

        permissionHelper?.requestAll {

            val tempActivity = activity ?: fragment?.activity
            when (resourceId) {
                R.id.llImageCamera -> {

                    ImagePicker.Builder(tempActivity)
                        .mode(ImagePicker.Mode.CAMERA)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .requestCode(customRequestCode * IMAGE_PICKER_REQUEST_CODE)
                        .build()
                }
                R.id.llImageGallery -> {
                    ImagePicker.Builder(tempActivity)
                        .mode(ImagePicker.Mode.GALLERY)
                        .compressLevel(ImagePicker.ComperesLevel.MEDIUM)
                        .directory(ImagePicker.Directory.DEFAULT)
                        .extension(ImagePicker.Extension.JPG)
                        .scale(600, 600)
                        .allowMultipleImages(false)
                        .enableDebuggingMode(true)
                        .requestCode(customRequestCode * IMAGE_PICKER_REQUEST_CODE)
                        .build()
                }
                R.id.llVideoCamera -> {
                    VideoPicker.Builder(tempActivity)
                        .mode(VideoPicker.Mode.CAMERA)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .requestCode(customRequestCode * VIDEO_PICKER_REQUEST_CODE)
                        .build()
                }
                R.id.llVideoGallery -> {
                    VideoPicker.Builder(tempActivity)
                        .mode(VideoPicker.Mode.GALLERY)
                        .directory(VideoPicker.Directory.DEFAULT)
                        .extension(VideoPicker.Extension.MP4)
                        .enableDebuggingMode(true)
                        .requestCode(customRequestCode * VIDEO_PICKER_REQUEST_CODE)
                        .build()
                }
                R.id.llFile -> {
//                    val intent = Intent(Intent.ACTION_GET_CONTENT)
//                    if (mediaType == MEDIA_TYPE_IMAGE) {
//                        intent.type = "image/*"
//                    } else if (mediaType == MEDIA_TYPE_VIDEO) {
//                        intent.type = "video/*"
//                    } else {
//                        intent.type = "*/*"
//                    }
//                    if (activity != null)
//                        activity.startActivityForResult(
//                            intent,
//                            customRequestCode * FILE_PICKER_REQUEST_CODE
//                        )
//                    else fragment?.startActivityForResult(
//                        intent,
//                        customRequestCode * FILE_PICKER_REQUEST_CODE
//                    )
//

                    if (activity != null)
                        activity.startActivityForResult(
                            FilePickerActivity.getIntent(activity),
                            customRequestCode * FILE_PICKER_REQUEST_CODE
                        )
                    else fragment?.startActivityForResult(
                        FilePickerActivity.getIntent(fragment.activity!!),
                        customRequestCode * FILE_PICKER_REQUEST_CODE
                    )


                }
            }
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == customRequestCode * IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val mPaths =
                data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_PATH) as ArrayList<String>
            if (mPaths != null) {
                mPaths[0].let {
                    if (requiresCrop == true) {
                        val destinationFile = File(
                            filePaths.getLocalDirectory(
                                type = TYPES.PUBLIC_IMAGE_DIRECTORY
                            )?.path + "/" + File(it).name
                        )
                        destinationFile.createNewFile()
                        //Cropping

                        val options = UCrop.Options()
                        options.setAllowedGestures(
                            UCropActivity.SCALE,
                            UCropActivity.NONE,
                            UCropActivity.SCALE
                        )
                        options.setToolbarColor(
                            ContextCompat.getColor(
                                activity ?: fragment?.requireContext()!!,
                                R.color.crop_toolbar_color
                            )
                        )
                        options.setStatusBarColor(
                            ContextCompat.getColor(
                                activity ?: fragment?.requireContext()!!,
                                R.color.crop_statusbar_color
                            )
                        )
                        options.setHideBottomControls(true)

                        if (activity != null) {
                            activity.startActivityForResult(
                                UCrop.of(
                                    Uri.fromFile(File(it)),
                                    Uri.fromFile(destinationFile)
                                ).withOptions(options).withAspectRatio(1f, 1f).getIntent(activity)
                                , customRequestCode * IMAGE_CROP_REQUEST_CODE
                            )
                        } else {
                            fragment?.startActivityForResult(
                                UCrop.of(
                                    Uri.fromFile(File(it)),
                                    Uri.fromFile(destinationFile)
                                ).withOptions(options).withAspectRatio(
                                    1f,
                                    1f
                                ).getIntent(fragment.context!!)
                                , customRequestCode * IMAGE_CROP_REQUEST_CODE
                            )
                        }
                    } else {
            //                        addNewItem(it, TYPE_GALLERY)
                        onMediaChoose(it, MEDIA_TYPE_IMAGE)
                    }
                }
            }
        } else if (requestCode == customRequestCode * VIDEO_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val mPaths =
                data?.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH) as ArrayList<String>
            if (mPaths != null) {
                mPaths[0].let {
                    //                        addNewItem(it, TYPE_GALLERY)
                    onMediaChoose(it, MEDIA_TYPE_VIDEO)
                }
            }
        } else if (requestCode == customRequestCode * IMAGE_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val resultUri = UCrop.getOutput(data)
            val imagePath = resultUri?.path ?: ""
//            addNewItem(imagePath, TYPE_GALLERY)
            onMediaChoose(imagePath, MEDIA_TYPE_IMAGE)
        } else if (requestCode == customRequestCode * FILE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
//            val pathHolder = data.data.path

            val filePath = data.getStringExtra("filePath")
            /*val _uri = data.data
            if (_uri != null && "content" == _uri.scheme) {
                val cursor = activity!!.contentResolver.query(_uri, null, null, null, null)
                cursor.moveToFirst()
                filePath = cursor.getString(0) ?: ""
                cursor.close()
            } else {
                filePath = _uri.path ?: ""
            }*/

            onMediaChoose(filePath, MEDIA_TYPE_OTHER)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}