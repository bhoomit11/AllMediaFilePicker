package com.bb.allmediafilepicker.utils.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.databinding.GalleryViewpagerBinding

/**
 * This class is used to display multiple photos with swipe left and right manner.
 * This activity is used to show images in full screen with picnh zoom
 */
class GalleryPagerActivity : AppCompatActivity() {


    companion object {
        const val BUNDLE_SELECTED_IMAGE = "bundle_selected_image"

        /**
         * Start intent to open GalleryPagerActivity
         * @param mContext [ERROR : Context]
         * @param mediaList ArrayList<String> List of images
         * @param position Int Position of image, which need to be open first
         * @return Intent
         */
        fun getStartIntent(
            mContext: Context,
            mediaList: ArrayList<GalleryResponse>,
            position: Int
        ): Intent {
            val intent = Intent(mContext, GalleryPagerActivity::class.java)
            intent.putParcelableArrayListExtra(BUNDLE_SELECTED_IMAGE, mediaList)
            intent.putExtra("position", position)
            return intent
        }
    }

    lateinit var binding: GalleryViewpagerBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding =
            DataBindingUtil.setContentView(this@GalleryPagerActivity, R.layout.gallery_viewpager)

        val values = intent?.getParcelableArrayListExtra<GalleryResponse>(BUNDLE_SELECTED_IMAGE)
        val pos = intent.getIntExtra("position", 0)
        // binding.vpGallery.adapter = GalleryPagerAdapter(values ?: ArrayList())
        binding.vpGallery.adapter =
            GalleryPagerFragmentAdapter(supportFragmentManager, values ?: ArrayList())
        binding.vpGallery.currentItem = pos
        binding.vpGallery.offscreenPageLimit = 0
        binding.btnBack.setOnClickListener { finish() }
    }
}