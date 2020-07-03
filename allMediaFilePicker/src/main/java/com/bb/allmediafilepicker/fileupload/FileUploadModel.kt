package com.bb.allmediafilepicker.fileupload


data class FileUploadModel(
    var id: String = "",
    var title: String = "",
    var suggestion: String = "",
    var allowMultipleImages: Boolean = false,
    var isCompulsory: Boolean = false,
    var requiresCrop: Boolean = false,
    var requiresVideoCompress: Boolean = false,
    var minFilesCount: Int,
    var maxFilesCount: Int,
    var mediaType:Int,
    var action:Int,
    var customRequestCode:Int
)