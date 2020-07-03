package com.bb.allmediafilepicker.utils

import java.text.SimpleDateFormat
import java.util.*


const val DATE_FORMAT_INPUT = "dd/MM/yyyy"
const val DATE_FORMAT_OUTPUT = "dd-MM-yyyy"
const val DATE_FORMAT_FILE = "dd/MM/yyyy HH:mm"

const val TIME_FORMAT_INPUT = "HH:mm"
const val TIME_FORMAT_OUTPUT = "hh:mm a"


fun String?.getTimestamp(
    inputFormat: String = DATE_FORMAT_INPUT
): Long {
    return try {
        val date = SimpleDateFormat(inputFormat, Locale.getDefault()).parse(this)
        date.time
    } catch (e: Exception) {
        System.currentTimeMillis()
    }
}

fun Long.getFormatedDate(
    outputFormat: String = DATE_FORMAT_OUTPUT
): String {
    return SimpleDateFormat(outputFormat, Locale.getDefault()).format(Date(this))
}


fun String.getDate(inputFormat: String = DATE_FORMAT_INPUT): Date {
    return SimpleDateFormat(inputFormat, Locale.getDefault()).parse(this)
}