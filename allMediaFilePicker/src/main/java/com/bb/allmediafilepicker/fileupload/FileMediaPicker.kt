package com.bb.allmediafilepicker.fileupload

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.utils.*
import com.hb.videocompressor.VideoCompressUtility
import com.hb.videocompressor.VideoCompressorConfig
import com.hb.videocompressor.org.m4m.IProgressListener
import com.bb.allmediafilepicker.utils.dialog.BottomSheetDialogFragmentHelperView
import com.bb.allmediafilepicker.utils.dialog.ProgressDialogFragment
import com.master.basediproject.utils.filepicker.FilePickerActivity
import com.master.permissionhelper.PermissionHelper
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropActivity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import net.alhazmy13.mediapicker.Image.ImagePicker
import net.alhazmy13.mediapicker.Video.VideoPicker
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class FileMediaPicker(
    val activity: AppCompatActivity? = null,
    val fragment: Fragment? = null,
    val requiresCrop: Boolean = true,
    val requiresVideoCompress: Boolean = true,
    val mediaType: Int,
    val allowMultipleImages: Boolean = false,
    val action: Int,
    val customRequestCode: Int = 123
) {
    var filePaths: FilePaths =
        FilePaths(if (fragment != null) fragment.requireContext() else activity as Context)

    var progressDialogFragment: ProgressDialogFragment? = null

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

        const val REQUEST_CODE_COMPRESS = 501

    }

    var onMediaChoose: (path: FileModel, mediaType: Int) -> Unit = { path, mediaType -> }
    var onMultiMediaChoose: (path: ArrayList<FileModel>, mediaType: Int) -> Unit =
        { path, mediaType -> }

    var permissionHelper: PermissionHelper? = null
    fun start(onMediaChoose: (path: FileModel, mediaType: Int) -> Unit) {
        progressDialogFragment = ProgressDialogFragment.newInstance()
        this.onMediaChoose = onMediaChoose
        if (activity != null || fragment != null) {
            BottomSheetDialogFragmentHelperView.with(
                R.layout.dialog_picker,
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

                val ivImageCamera = it.findViewById<AppCompatImageView>(R.id.ivImageCamera)
                val ivImageGallery = it.findViewById<AppCompatImageView>(R.id.ivImageGallery)
                val ivVideoCamera = it.findViewById<AppCompatImageView>(R.id.ivVideoCamera)
                val ivVideoGallery = it.findViewById<AppCompatImageView>(R.id.ivVideoGallery)
                val ivFile = it.findViewById<AppCompatImageView>(R.id.ivFile)

                ivImageCamera.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )
                ivImageGallery.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerSecondary
                        )
                    )
                )
                ivVideoCamera.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )
                ivVideoGallery.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerSecondary
                        )
                    )
                )
                ivFile.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )

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

    fun startMultiSelect(onMultiMediaChoose: (path: ArrayList<FileModel>, mediaType: Int) -> Unit) {
        this.onMultiMediaChoose = onMultiMediaChoose
        if (activity != null || fragment != null) {
            BottomSheetDialogFragmentHelperView.with(
                R.layout.dialog_picker,
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

                val ivImageCamera = it.findViewById<AppCompatImageView>(R.id.ivImageCamera)
                val ivImageGallery = it.findViewById<AppCompatImageView>(R.id.ivImageGallery)
                val ivVideoCamera = it.findViewById<AppCompatImageView>(R.id.ivVideoCamera)
                val ivVideoGallery = it.findViewById<AppCompatImageView>(R.id.ivVideoGallery)
                val ivFile = it.findViewById<AppCompatImageView>(R.id.ivFile)

                ivImageCamera.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity!!,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )
                ivImageGallery.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerSecondary
                        )
                    )
                )
                ivVideoCamera.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )
                ivVideoGallery.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerSecondary
                        )
                    )
                )
                ivFile.backgroundTintList = ColorStateList.valueOf(
                    getLightColor(
                        ContextCompat.getColor(
                            activity,
                            R.color.file_upload_colorPickerPrimary
                        )
                    )
                )


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
                        .allowMultipleImages(allowMultipleImages)
                        .allowOnlineImages(true)
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

                    if (activity != null)
                        activity.startActivityForResult(
                            FilePickerActivity.getIntent(activity),
                            customRequestCode * FILE_PICKER_REQUEST_CODE
                        )
                    else fragment?.startActivityForResult(
                        FilePickerActivity.getIntent(fragment.requireActivity()),
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
                if (allowMultipleImages) {
                    onMultiMediaChoose(mPaths.map {
                        FileModel(
                            it,
                            MEDIA_TYPE_IMAGE,
                            it.getMimeType()
                        )
                    }.toArrayList(), MEDIA_TYPE_IMAGE)
                } else {
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

                            activity?.startActivityForResult(
                                UCrop.of(
                                    Uri.fromFile(File(it)),
                                    Uri.fromFile(destinationFile)
                                ).withOptions(options).withAspectRatio(1f, 1f)
                                    .getIntent(activity)
                                , customRequestCode * IMAGE_CROP_REQUEST_CODE
                            )
                                ?: fragment?.startActivityForResult(
                                    UCrop.of(
                                        Uri.fromFile(File(it)),
                                        Uri.fromFile(destinationFile)
                                    ).withOptions(options).withAspectRatio(
                                        1f,
                                        1f
                                    ).getIntent(fragment.requireContext())
                                    , customRequestCode * IMAGE_CROP_REQUEST_CODE
                                )
                        } else {
                            //                        addNewItem(it, TYPE_GALLERY)
                            onMediaChoose(
                                FileModel(it, MEDIA_TYPE_IMAGE, it.getMimeType()),
                                MEDIA_TYPE_IMAGE
                            )
                        }
                    }
                }
            }
        } else if (requestCode == customRequestCode * VIDEO_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val mPaths =
                data?.getSerializableExtra(VideoPicker.EXTRA_VIDEO_PATH) as ArrayList<String>
            if (mPaths != null) {
                if (allowMultipleImages) {
                    if (requiresVideoCompress) {
                        doVideoCompression(mPaths[0])
                    } else {
                        onMultiMediaChoose(mPaths.map {
                            FileModel(
                                it,
                                MEDIA_TYPE_VIDEO,
                                it.getMimeType()
                            )
                        }.toArrayList(), MEDIA_TYPE_VIDEO)
                    }
                } else {
                    mPaths[0].let {
                        if (requiresVideoCompress) {
                            doVideoCompression(it)
                        } else {
                            onMediaChoose(
                                FileModel(it, MEDIA_TYPE_VIDEO, it.getMimeType()),
                                MEDIA_TYPE_VIDEO
                            )
                        }
                    }
                }
            }
        } else if (requestCode == customRequestCode * IMAGE_CROP_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val resultUri = UCrop.getOutput(data)
            val imagePath = resultUri?.path ?: ""
//            addNewItem(imagePath, TYPE_GALLERY)
            onMediaChoose(
                FileModel(imagePath, MEDIA_TYPE_IMAGE, imagePath.getMimeType()),
                MEDIA_TYPE_IMAGE
            )
        } else if (requestCode == customRequestCode * FILE_PICKER_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK && data != null) {
            val filePath = data.getStringExtra("filePath")
            onMediaChoose(
                FileModel(
                    filePath ?: "",
                    MEDIA_TYPE_OTHER,
                    filePath?.getMimeType() ?: ""
                ), MEDIA_TYPE_OTHER
            )
        }
    }

    private fun doVideoCompression(s: String) {
        val file = File(s)
        val timeStamp = SimpleDateFormat("dd_MMM_yyyy", Locale.getDefault()).format(Date())
        val dest = file.parentFile?.toString() + "/" + file.name.substring(
            0,
            file.name.lastIndexOf(".")
        ) + "_" + timeStamp + "_trimmer.mp4"

        try {
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(file.absolutePath)
            val width =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH))
            val height =
                Integer.valueOf(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT))
            if (!retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_HAS_AUDIO)
                    .parseBoolean()
            ) {
                activity?.getString(R.string.msg_invalid_video)?.showToast(activity)
                return
            }
            retriever.release()

            val videoEditorConfig = VideoCompressorConfig.Builder().apply {
                setVideoWidth(width)
                setVideoHeight(height)
                setVideoBitRateInKBytes(1800)
                setVideoFrameRate(25)
                setVideoFrameInterval(1)
                setAudioBitRate(48)
            }.build()


            showHideProgressDialog(true)
            VideoCompressUtility().compress(
                context = activity as AppCompatActivity,
                sourcepath = file.absolutePath,
                destiURI = dest,
                videoEditorConfig = videoEditorConfig,
                requestCode = REQUEST_CODE_COMPRESS,
                progressListener = object : IProgressListener {
                    override fun onMediaPause(requestCode: Int) {
                    }

                    override fun onMediaProgress(requestCode: Int, progress: Float) {
                    }

                    @SuppressLint("CheckResult")
                    override fun onMediaDone(requestCode: Int) {
                        showHideProgressDialog(false)
                        Observable.timer(150, TimeUnit.MILLISECONDS)
                            .subscribeOn(AndroidSchedulers.mainThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({
                                if (allowMultipleImages) {
                                    onMultiMediaChoose(arrayListOf(dest).map {
                                        FileModel(
                                            it,
                                            MEDIA_TYPE_VIDEO,
                                            it.getMimeType()
                                        )
                                    }.toArrayList(), MEDIA_TYPE_VIDEO)
                                } else {
                                    onMediaChoose(
                                        FileModel(
                                            dest,
                                            MEDIA_TYPE_VIDEO,
                                            dest.getMimeType()
                                        ), MEDIA_TYPE_VIDEO
                                    )
                                }
                            }, {})
                    }

                    override fun onMediaStart(requestCode: Int) {
                    }

                    override fun onMediaStop(requestCode: Int) {
                    }

                    override fun onError(requestCode: Int, exception: Exception?) {
                        showHideProgressDialog(false)
                        activity?.getString(R.string.error_something_went_wrong)
                            ?.showToast(activity)
                    }

                }
            )

        } catch (e: Exception) {
            showHideProgressDialog(false)
            activity?.getString(R.string.msg_invalid_video)?.showToast(activity)
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    /**
     * Show or hide progress dialog
     * @param isShow Boolean
     */
    fun showHideProgressDialog(isShow: Boolean) {
        try {
            if (isShow) {
                if (progressDialogFragment?.dialog == null || progressDialogFragment?.dialog?.isShowing == false || progressDialogFragment?.isAdded == false) {
                    progressDialogFragment?.show(
                        fragment?.childFragmentManager ?: activity?.supportFragmentManager!!,
                        javaClass.simpleName
                    )
                }
            } else {
                progressDialogFragment?.dismissAllowingStateLoss()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLightColor(color: Int): Int {
        var a = Color.alpha(color)
        val r = Color.red(color)
        val g = Color.green(color)
        val b = Color.blue(color)

        a = (a * 0.25).toInt()

        return Color.argb(a, r, g, b)
    }
}