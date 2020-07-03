package com.bb.allmediafilepicker.fileupload

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.databinding.LayoutAttachmentBinding
import com.bb.allmediafilepicker.databinding.LayoutFileuploadBinding
import com.bb.allmediafilepicker.utils.MediaPicker
import com.bb.allmediafilepicker.utils.gallery.GalleryPagerActivity
import com.bb.allmediafilepicker.utils.gallery.GalleryPagerAdapter.Companion.TYPE_IMAGE
import com.bb.allmediafilepicker.utils.gallery.GalleryPagerAdapter.Companion.TYPE_VIDEO
import com.bb.allmediafilepicker.utils.gallery.GalleryResponse
import com.bb.allmediafilepicker.utils.loadImage
import com.bb.allmediafilepicker.utils.showToast
import com.bb.allmediafilepicker.utils.toArrayList
import kotlin.collections.ArrayList

class FileUploadView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    lateinit var binding: LayoutFileuploadBinding
    private var model: FileUploadModel? = null
        set(value) {
            field = value
            setView()
        }
    var activity: AppCompatActivity? = null
    var mediapicker: FileMediaPicker? = null
    var onFilesChanged: (list: ArrayList<FileModel>) -> Unit = {}
    fun setupWithActivityOrFragment(
        activity: AppCompatActivity? = null,
        fragment: Fragment? = null,
        fileUploadModel: FileUploadModel,
        onFilesChanged: (list: ArrayList<FileModel>) -> Unit = {}
    ) {
        mediapicker = FileMediaPicker(
                activity = activity,
                fragment = fragment,
                requiresCrop = fileUploadModel.requiresCrop,
                requiresVideoCompress = fileUploadModel.requiresVideoCompress,
                mediaType = fileUploadModel.mediaType,
                action = fileUploadModel.action,
                allowMultipleImages = fileUploadModel.allowMultipleImages,
                customRequestCode = fileUploadModel.customRequestCode
        )
        model = fileUploadModel
        this@FileUploadView.activity = activity
        this.onFilesChanged = onFilesChanged
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        mediapicker?.onActivityResult(requestCode, resultCode, data)
    }

    fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        mediapicker?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    //--------

    init {
        init()
    }

    val fileList = ArrayList<FileModel>()
    val inflater by lazy { context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater }
    private fun init() {
        if (isInEditMode) {
            inflate(context, R.layout.layout_fileupload, this)
        } else {
            val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = LayoutFileuploadBinding.inflate(inflater, this, true)

            val itemView = LayoutAttachmentBinding.inflate(inflater)
            itemView.ivDelete.visibility = View.GONE
            binding.llImageContainer.removeAllViews()
            binding.llImageContainer.addView(itemView.root)

            itemView.root.setOnClickListener {
                if (mediapicker == null) {
                    throw Exception("You forgot to call setupWithActivityOrFragment() also onActivityResult, requestPermission")
                }

                if (model?.allowMultipleImages == true) {
                    mediapicker?.startMultiSelect { path: ArrayList<FileModel>, mediaType: Int ->
                        for (i in path) {
                            if (fileList.size < (model?.maxFilesCount ?: 0)) {
                                addNewItem(fileModel = i, type = mediaType)
                                onFilesChanged(fileList)
                            } else {
                                if (activity != null) {
                                    "Maximum ${model?.maxFilesCount} files are allowed!".showToast(context)
                                    break
                                }
                            }
                        }
                    }
                } else {
                    mediapicker?.start { path: FileModel, mediaType: Int ->
                        addNewItem(fileModel = path, type = mediaType)
                        onFilesChanged(fileList)
                    }
                }
            }
        }
    }

    fun addNewItem(fileModel: FileModel, type: Int) {
        val itemView = LayoutAttachmentBinding.inflate(inflater)
        itemView.ivDelete.visibility = View.VISIBLE
        itemView.ivVideo.visibility = View.GONE
        when (type) {
            MediaPicker.MEDIA_TYPE_IMAGE -> {
                itemView.ivImage.loadImage(fileModel.file)
            }
            MediaPicker.MEDIA_TYPE_VIDEO -> {
                itemView.ivVideo.visibility = View.VISIBLE
                itemView.ivImage.loadImage(fileModel.file)
            }
            else -> {
                itemView.ivImage.scaleType = ImageView.ScaleType.CENTER_INSIDE
                itemView.ivImage.loadImage(R.drawable.ic_file_white)
            }
        }

        itemView.ivImage.setTag(R.id.file, fileModel)
        itemView.ivDelete.setTag(R.id.file, fileModel)
        itemView.root.setTag(R.id.file, fileModel)

        itemView.ivDelete.setOnClickListener {
            fileList.remove(it.getTag(R.id.file) as FileModel)

            binding.llImageContainer.removeView(itemView.root)

            binding.llImageContainer.getChildAt(0).isEnabled =
                    fileList.size < model?.maxFilesCount!!

            if (binding.llImageContainer.getChildAt(0).isEnabled) {
                binding.llImageContainer.getChildAt(0).visibility = View.VISIBLE
            } else {
                binding.llImageContainer.getChildAt(0).visibility = View.GONE
            }

            onFilesChanged(fileList)
        }

        itemView.root.setOnClickListener {
            val path = it.getTag(R.id.file) as FileModel?
            val index = fileList.indexOf(path)
            activity?.startActivity(
                GalleryPagerActivity.getStartIntent(activity!!,
                    fileList.map {
                        GalleryResponse(type = if (it.fileType == FileMediaPicker.MEDIA_TYPE_IMAGE) TYPE_IMAGE else TYPE_VIDEO, url = it.file)
                    }.toArrayList(),
                    position = index)
            )
        }

        fileList.add(fileModel)
        binding.llImageContainer.addView(itemView.root)

        binding.llImageContainer.getChildAt(0).isEnabled = fileList.size < model?.maxFilesCount!!
        if (binding.llImageContainer.getChildAt(0).isEnabled) {
            binding.llImageContainer.getChildAt(0).visibility = View.VISIBLE
        } else {
            binding.llImageContainer.getChildAt(0).visibility = View.GONE
        }
    }

    private fun setView() {
        if (model != null) {
            binding.tvTitle.text = model?.title
        }
    }

    fun checkValidity(): Boolean {
        return true
    }
}

data class FileModel(
        var file: String = "",
        var fileType: Int = 0,
        var fileMimeType: String = ""
)