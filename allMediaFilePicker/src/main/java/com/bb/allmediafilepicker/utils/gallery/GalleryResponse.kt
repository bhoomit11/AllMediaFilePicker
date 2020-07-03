package com.bb.allmediafilepicker.utils.gallery

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GalleryResponse (

        @field:SerializedName("user_id")
        val userId: String? = null,

        @field:SerializedName("added_date")
        val addedDate: String? = null,

        @field:SerializedName("file_type")
        val type: String? = null,

        @field:SerializedName("user_media_id")
        val userMediaId: String? = null,

        @field:SerializedName("temperature_covid_result_id")
        val temperatureCovidResultId: String? = null,

        @field:SerializedName("media_file")
        val url: String? = null,

        @field:SerializedName("record_type")
        val recordType: String? = null,

        @field:SerializedName("status")
        val status: String? = null,

        @field:SerializedName("video_image")
        val videoImage: String? = null
) : Parcelable