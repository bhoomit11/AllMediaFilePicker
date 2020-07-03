package com.bb.allmediafilepicker.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.text.Spanned
import android.util.StateSet
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.webkit.WebView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bb.allmediafilepicker.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList


fun runAsync(func: () -> Unit): Thread {
    val thread = Thread(Runnable { func() })
    thread.start()
    return thread
}

fun <T> Fragment.getFromArgument(key: String, defaultValue: T): T {
    if (arguments != null && arguments?.containsKey(key) == true) {
        return arguments!!.get(key) as T
    }
    return defaultValue
}

fun <T> Activity.getFromIntent(key: String, defaultValue: T): T {
    if (intent.extras != null && intent.extras?.containsKey(key) == true) {
        return intent.extras!!.get(key) as T
    }
    return defaultValue
}

fun WebView?.loadUrlWithPostParam(url: String, postParam: HashMap<String, String>) {
    var postData: String = ""
    for ((key, value) in postParam) {
        postData += "$key=${URLEncoder.encode(value, "UTF-8")}&"
    }
    this?.postUrl(url, postData.toByteArray())
}

/*fun runOnUI(func: () -> Unit): Handler {
    val mainHandler = Handler(myAppContext.mainLooper)
    val myRunnable = Runnable {
        func()
    }
    mainHandler.post(myRunnable)
    return mainHandler
}*/

fun View.showKeyBoard() {
    /*this.postDelayed({
        val inputManager = this.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(this, InputMethodManager.HIDE_NOT_ALWAYS)
    }, 100)*/

    val inputManager = this.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(this, 0)
}

fun AppCompatActivity.hideKeyBoard() {
    /*this.postDelayed({
        val inputManager = this.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.windowToken, 0)
    }, 100)*/

    val inputManager = this
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    val view = currentFocus
    if (view != null)
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun View.hideKeyBoard() {
    /*this.postDelayed({
        val inputManager = this.context
                .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(this.windowToken, 0)
    }, 100)*/

    val inputManager = this.context
        .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.windowToken, 0)
}

fun Intent.startActivityForResult(fragment: Fragment, requestCode: Int) {
    try {
        fragment.startActivityForResult(this, requestCode)
    } catch (e: Exception) {
    }
}

fun String?.ifNotBlank(func: (String) -> Unit) {
    if (this != null && this.trim().isNotEmpty()) {
        func(this)
    }
}

fun Activity.share(
    subject: String? = "",
    text: String,
    imageUrl: Uri? = null,
    appName: Array<String>? = null,
    defaultAllowed: Boolean = false
): Boolean {
    try {
        val shareIntent = Intent(android.content.Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, text)
        shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, subject)
        imageUrl?.apply {
            shareIntent.putExtra(Intent.EXTRA_STREAM, this)
            shareIntent.type = "image/*"
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        if (appName != null) {
            val pm = packageManager
            val activityList = pm?.queryIntentActivities(shareIntent, 0)
            val filteredPackage =
                activityList?.filter { appName.contains(it.activityInfo.packageName) == true }
                    ?.map { it.activityInfo.packageName }
            if (filteredPackage?.isNotEmpty() == true) {
                shareIntent.`package` = filteredPackage.get(0)
                startActivity(shareIntent)
                return true
            } else if (defaultAllowed) {
                startActivity(shareIntent)
                return true
            } else {
                return false
            }
        } else {
            startActivity(shareIntent)
            return true
        }
    } catch (e: Exception) {
        return false
    }
}

fun Fragment.share(text: String, appName: Array<String>? = null): Boolean {
    return activity?.share(text = text, appName = appName) == true
}

fun String.getMimeType(defaultMimeType: String = "image/*"): String {
    var type: String = defaultMimeType
    try {
        val extension = MimeTypeMap.getFileExtensionFromUrl(this.toLowerCase()).toLowerCase()
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: defaultMimeType
        }
    } catch (e: Exception) {

    }
    return type
}

fun String.showToast(context: Context?) {
    if (context != null)
        Toast.makeText(context, this, Toast.LENGTH_LONG).show()
}


@Suppress("DEPRECATION")
fun String.getHtmlFormattedText(): Spanned {
    val result: Spanned =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(this, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(this)
        }
    return result
}


fun setImageSelector(
    view: ImageView,
    selectedImageRes: Drawable?,
    pressedImageRes: Drawable?,
    normalImageRes: Drawable?
) {

    if (!(selectedImageRes == null && pressedImageRes == null && normalImageRes == null)) {
        val stateListDrawable = StateListDrawable()
        if (selectedImageRes != null) {
            stateListDrawable.addState(intArrayOf(android.R.attr.state_selected), selectedImageRes)
        }

        if (pressedImageRes != null) {
            stateListDrawable.addState(intArrayOf(android.R.attr.state_pressed), pressedImageRes)
        }
        stateListDrawable.addState(StateSet.WILD_CARD, normalImageRes)

        view.setImageDrawable(stateListDrawable)
    }
}


fun <T> List<T>?.toArrayList(): ArrayList<T> {
    return if (this == null) {
        ArrayList()
    } else {
        ArrayList(this)
    }
}

// to get file name from path
fun String.getFileName(): String {
    return this.substring(this.lastIndexOf("/") + 1, this.length)
}

/**
 * Extention function for load image with loader
 */
fun ImageView.loadImageProgress(url: String?, placeHolder: Int = R.drawable.ic_iconfinder_picture_1814111, pb: ProgressBar) {
    pb.visibility= View.VISIBLE
    Glide.with(this.context)
        .load(url)
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                pb.visibility= View.GONE
                return false
            }
            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                pb.visibility= View.GONE
                return false
            }
        })
        .apply(applyPlaceholder(placeHolder))
        .into(this)

}


fun applyPlaceholder(placeholderImage: Int): RequestOptions {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeholderImage)
    requestOptions.error(placeholderImage)
    requestOptions.dontAnimate()
    return requestOptions
}

fun String?.parseBoolean(): Boolean {
    return if ("1".equals(this)) {
        true
    } else if ("yes".equals(this, ignoreCase = true)) {
        true
    } else if ("true".equals(this, ignoreCase = true)) {
        true
    } else if ("0".equals(this)) {
        false
    } else if (this.isNullOrBlank()) {
        false
    } else {
        this.toBoolean()
    }
}
