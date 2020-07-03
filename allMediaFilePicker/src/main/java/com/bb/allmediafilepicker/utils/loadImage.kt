package com.bb.allmediafilepicker.utils

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun ImageView.loadImage(url: String?, placeHolder: Int = -1) {
    url.ifNotBlank {
        val requestManager = Glide.with(this.context.applicationContext).load(url)
        if (placeHolder != -1) {
            requestManager.apply(RequestOptions().placeholder(placeHolder).error(placeHolder)).into(this)
        } else {
            requestManager.into(this)
        }
    }
}

fun ImageView.loadImageWithCircle(url: String?, placeHolder: Int = -1) {
    url.ifNotBlank {
        val requestManager = Glide.with(this.context.applicationContext).load(url).apply(RequestOptions.bitmapTransform(CircleCrop()))
        if (placeHolder != -1) {
            requestManager.apply(RequestOptions().placeholder(placeHolder).error(placeHolder)).into(this)
        } else {
            requestManager.into(this)
        }
    }
}

fun ImageView.loadImageWithRoundedCorner(url: String?, placeHolder: Int = -1, radius: Int = 0) {

//    url.ifNotBlank {
    val requestManager = Glide.with(this).load(url).transform(MultiTransformation(CenterCrop(), RoundedCorners(radius)))
    if (placeHolder != -1) {
        requestManager.apply(RequestOptions().placeholder(placeHolder).error(placeHolder)).into(this)
    } else {
        requestManager.into(this)
    }
//    }
}

fun ImageView.loadImageWithThumb(url: String, placeHolder: Int = -1) {
    url.ifNotBlank {
        // setup Glide request without the into() method
        val thumbnailRequest = Glide
            .with(context.applicationContext)
            .load(url)

        val requestManager = Glide.with(this).load(url).thumbnail(thumbnailRequest)
        if (placeHolder != -1) {
            requestManager.apply(RequestOptions().placeholder(placeHolder).error(placeHolder)).into(this)
        } else {
            requestManager.into(this)
        }
    }
}


fun ImageView.loadImage(placeHolder: Int) {
    Glide.with(this.context.applicationContext).load(placeHolder).into(this)
}

fun ImageView.loadImageWithCircle(placeHolder: Int) {
    Glide.with(this.context.applicationContext).load(placeHolder).apply(RequestOptions.bitmapTransform(CircleCrop()))
        .into(this)
}

fun ImageView.loadImageNoAnimate(placeHolder: Int) {
    Glide.with(this.context.applicationContext).load(placeHolder).dontAnimate().into(this)
}

@BindingAdapter(value = ["bind:imageUrl", "bind:placeHolder"], requireAll = false)
fun setImageUrl(imageView: AppCompatImageView, url: String?, placeHolder: Drawable? = null) {

//    url?.ifNotBlank {
    if (url?.isNotBlank() == true) {
        Glide.with(imageView.context.applicationContext).load(url).apply(RequestOptions.noAnimation()).into(imageView)
    } else {
        imageView.setImageDrawable(placeHolder)
    }
//    }

}
