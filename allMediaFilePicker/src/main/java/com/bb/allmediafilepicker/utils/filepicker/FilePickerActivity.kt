package com.master.basediproject.utils.filepicker

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.Formatter
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.databinding.ItemFilePickerBinding
import com.bb.allmediafilepicker.utils.*
import com.bb.allmediafilepicker.utils.filepicker.FilesData
import com.master.permissionhelper.PermissionHelper
import com.simpleadapter.SimpleAdapter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_file_picker.*
import java.io.File
import java.lang.Exception


class FilePickerActivity : AppCompatActivity() {

    private val filePaths: FilePaths by lazy {
        FilePaths(this)
    }

    lateinit var permissionHelper: PermissionHelper
    private var adapter: SimpleAdapter<FilesData>? = null
    val list = ArrayList<FilesData>()

    companion object {

        // Mime Types
        const val DOC = "application/msword" // .doc
        const val DOCX =
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
        const val PPT = "application/vnd.ms-powerpoint"// .ppt
        const val PPTX =
            "application/vnd.openxmlformats-officedocument.presentationml.presentation" // .pptx
        const val XLS = "application/vnd.ms-excel" // .xls
        const val XLSX =
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" // .xlsx
        const val TEXT = "text/plain" // text
        const val PDF = "application/pdf" // PDF
        const val ZIP = "application/zip" // PDF

        fun getIntent(context: Context): Intent {
            return Intent(context, FilePickerActivity::class.java)
        }
    }

    private val mimeTypes = arrayOf(
        /*DOC,
        DOCX,
        PPT,
        PPTX,
        XLS,
        XLSX,
        TEXT,*/
        PDF
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file_picker)

        tvHeaderTitle.text = getString(R.string.file_picker)
        initUi()
    }

    private fun initUi() {
        rvFileList.layoutManager = LinearLayoutManager(this)
        adapter =
            SimpleAdapter.with<FilesData, ItemFilePickerBinding>(R.layout.item_file_picker) { adapaterPosition, model, binding ->
                binding.model = model

                binding.ivImage.loadImage(model.getFileImage())
            }

        adapter?.setClickableViews({ view, model, adapterPosition ->
            when (view.id) {
                R.id.llMain -> {

                    if (model.file.isNotBlank()) {
                        setResult(Activity.RESULT_OK, Intent().apply {
                            putExtra("filePath", model.file)
                        })

                        finish()
                    } else {
                        Toast.makeText(this, "Invalid file!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }, R.id.llMain)
        rvFileList.adapter = adapter


        permissionHelper =
            PermissionHelper(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))

        permissionHelper.requestAll {
            pbLoading.visibility = View.VISIBLE
            Observable.fromCallable {
                walk(
                    File(
                        filePaths.getLocalDirectory(
                            type = TYPES.GENERAL_PUBLIC_DIRECTORY
                        )?.path ?: ""
                    )
                )
            }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    pbLoading.visibility = View.GONE
                    setRecyclerView(it)
                }, {
                    Log.e("FILEPICKER", it.message)
                })
        }

        permissionHelper.denied {
            if (it) {
                /*DialogUtils.showSimpleDialog(supportFragmentManager = supportFragmentManager,
                    title = getString(R.string.media_picker_some_permission_is_denied),
                    message = getString(R.string.delete_confirmation),
                    yesText = getString(R.string.yes),
                    yesCallback = {
                        showProgressDialog(true)
                        positionToDelete = adapterPosition
                        vehicleViewModel.deleteVehicle(model.companyVehicleId ?: "")
                        it.dismiss()
                    },
                    noText = getString(R.string.no),
                    noCallback = {
                        it.dismiss()
                    }
                )*/
            } else {
                finish()
            }
        }

        ivBack.setOnClickListener {
            onBackPressed()
        }
    }

    fun walk(root: File): ArrayList<FilesData> {
        try {
            val listFile = root.listFiles { dir, name ->
                val file = File(dir, name)
                if (!file.isDirectory) {
                    val mimeType = file.absoluteFile?.absolutePath?.getMimeType("*/*")
                    mimeTypes.contains(mimeType)
                } else {
                    file.absoluteFile?.absolutePath?.isNotEmpty() == true
                }
            }.toCollection(ArrayList())

            val datadir =
                filePaths.getLocalDirectory(type = TYPES.PUBLIC_CACHE_DIRECTORY)?.path ?: ""

            datadir.substring(datadir.lastIndexOf("data/") + 1, datadir.length)

            val file: File? = listFile.find {
                it.path == datadir.substring(0, datadir.lastIndexOf("/data/"))
            }
            listFile.remove(file)

            if (listFile.isNotEmpty()) {
                for (f in listFile) {
                    if (f.isDirectory) {
//                    Timber.d("Dir: %s", f.absolutePath)
                        walk(f)
                    } else {
//                    Log.d("File: %s", f.absolutePath)
                        val mimeType = f.absoluteFile.absolutePath.getMimeType("*/*")
                        if (mimeTypes.contains(mimeType)) {
                            list.add(
                                FilesData(
                                    file = f.absoluteFile.absolutePath,
                                    type = mimeType,
                                    time = f.lastModified().getFormatedDate(
                                        DATE_FORMAT_FILE
                                    ),
                                    size = Formatter.formatShortFileSize(this, f.length())
                                )
                            )
                        }
                    }
                }

                return if (list.isNotEmpty()) list else ArrayList()
            } else {
                return ArrayList()
            }
        } catch (e: Exception) {
            return ArrayList()
        }
    }

    private fun setRecyclerView(list: ArrayList<FilesData>) {
        adapter?.clear()
        adapter?.addAll(list)
        adapter?.notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
