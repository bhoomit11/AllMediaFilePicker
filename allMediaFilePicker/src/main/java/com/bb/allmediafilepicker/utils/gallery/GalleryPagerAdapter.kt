package com.bb.allmediafilepicker.utils.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.viewpager.widget.PagerAdapter
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.databinding.ItemGalleryPagerBinding
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.bumptech.glide.Glide
import java.util.*

/**
 * Gallery pager adapter is used to show images in pager view
 * @property mediaList [ERROR : null type]
 * @constructor
 */
class GalleryPagerAdapter(private val mediaList: ArrayList<GalleryResponse>) : PagerAdapter() {

    companion object{
        const val TYPE_IMAGE = "Image"
        const val TYPE_VIDEO = "Video"
    }

    override fun isViewFromObject(view: View, anyObject: Any): Boolean {
        return view == anyObject
    }

    override fun getCount(): Int {
        return mediaList.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val binding = ItemGalleryPagerBinding.inflate(LayoutInflater.from(container.context))
        container.addView(binding.root)
        Glide.with(container.context)
                .load(mediaList[position].url)
                .into(binding.ivGalleryImage)
        if(mediaList[position].type == TYPE_VIDEO){
//            binding.videoView.visibility = View.VISIBLE
            setVideoPlayer(binding, mediaList[position].url?:"")
            binding.ivGalleryImage.visibility = View.GONE
        }else{
            val imageMatrixTouchHandler = ImageMatrixTouchHandler(container.context)
            binding.ivGalleryImage.setOnTouchListener(imageMatrixTouchHandler)
//            binding.videoView.visibility = View.GONE
            binding.ivGalleryImage.visibility = View.VISIBLE
        }


        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as RelativeLayout)
    }

    fun setVideoPlayer(binding: ItemGalleryPagerBinding, url:String){
        /*binding.apply {
            val mediacontroller = MediaController(binding.root.context)
            mediacontroller.setAnchorView(binding.videoView)
            videoView.setMediaController(mediacontroller)
            videoView.setVideoURI(Uri.parse(url))
            videoView.requestFocus()
            videoView.setOnPreparedListener { mp ->
                mp.setOnVideoSizeChangedListener { mp, width, height ->
                    videoView.setMediaController(mediacontroller)
                    mediacontroller.setAnchorView(videoView)
                }
            }
            videoView.setOnCompletionListener { mp ->
                mp.release()
                binding.ivGalleryImage.visibility = View.VISIBLE
                binding.ivPlay.visibility = View.VISIBLE
                binding.videoView.visibility = View.GONE
            }

            videoView.setOnErrorListener { mp, what, extra ->
                Log.d("API123", "What $what extra $extra")
                false
            }

        }*/
    }
}