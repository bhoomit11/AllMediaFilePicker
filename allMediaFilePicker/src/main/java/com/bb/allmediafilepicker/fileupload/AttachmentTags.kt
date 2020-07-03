package com.bb.allmediafilepicker.fileupload
import com.google.gson.annotations.SerializedName


data class AttachmentTags(
    @SerializedName("is_required")
    var isRequired: String? = "",
    @SerializedName("tag_master_id")
    var tagMasterId: String? = "",
    @SerializedName("tag_doc_id")
    var tagDocId: String? = ""
)