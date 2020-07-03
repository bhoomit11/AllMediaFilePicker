package com.bb.allmediafilepicker.utils.filepicker

import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.utils.getFileName
import com.master.basediproject.utils.filepicker.FilePickerActivity

data class FilesData(
    val file: String = "",
    val type: String = "",
    val time: String = "",
    val size: String = ""
) {
    fun getFileImage(): Int {
        return when (type) {
            FilePickerActivity.DOC -> {
                R.drawable.ic_doc
            }
            FilePickerActivity.DOCX -> {
                R.drawable.ic_doc
            }
            FilePickerActivity.PPT -> {
                R.drawable.ic_ppt
            }
            FilePickerActivity.PPTX -> {
                R.drawable.ic_ppt
            }
            FilePickerActivity.XLS -> {
                R.drawable.ic_xls
            }
            FilePickerActivity.XLSX -> {
                R.drawable.ic_xls
            }
            FilePickerActivity.TEXT -> {
                R.drawable.ic_txt
            }
            FilePickerActivity.PDF -> {
                R.drawable.ic_pdf
            }
            FilePickerActivity.ZIP -> {
                R.drawable.ic_zip
            }
            else -> {
                R.drawable.ic_pdf
            }
        }
    }

    fun getFileName(): String {
        return file.getFileName()
    }
}
