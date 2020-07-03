package com.bb.allmediafilepicker.utils.gallery

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bb.allmediafilepicker.R
import com.bb.allmediafilepicker.databinding.ItemGalleryPagerBinding
import com.bb.allmediafilepicker.utils.gallery.GalleryPagerAdapter.Companion.TYPE_VIDEO
import com.bb.allmediafilepicker.utils.getFromArgument
import com.bb.allmediafilepicker.utils.loadImageProgress
import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler
import com.halilibo.bettervideoplayer.BetterVideoCallback
import com.halilibo.bettervideoplayer.BetterVideoPlayer
import java.io.File

class GalleryPagerFrament : Fragment() {

    lateinit var binding: ItemGalleryPagerBinding
    var mediacontroller: MediaController? = null

    companion object {
        const val GALLERY = "gallery"

        fun getInstance(gallery: GalleryResponse): GalleryPagerFrament {
            return GalleryPagerFrament().apply {
                arguments = Bundle().apply {
                    putParcelable(GALLERY, gallery)
                }
            }
        }
    }

    private val gallery by lazy {
        getFromArgument(GALLERY, GalleryResponse())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,  R.layout.item_gallery_pager, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        iniViews()
    }

    fun iniViews() {
        if (gallery.type == TYPE_VIDEO) {
            binding.player.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            setBetterVideoPlayer()

            binding.ivGalleryImage.visibility = View.GONE
        } else {
            binding.ivGalleryImage.loadImageProgress(gallery.url
                    ?: "", R.drawable.ic_iconfinder_picture_1814111, binding.progressBar)
            val imageMatrixTouchHandler = ImageMatrixTouchHandler(binding.root.context)
            binding.ivGalleryImage.setOnTouchListener(imageMatrixTouchHandler)
            binding.player.visibility = View.GONE
            binding.ivGalleryImage.visibility = View.VISIBLE
        }

    }

    private fun setBetterVideoPlayer() {
        if (File(gallery.url ?: "").exists()) {
            binding.player.setSource(Uri.fromFile(File(gallery.url ?: "")))
        } else {
            binding.player.setSource(Uri.parse(gallery.url ?: ""))
        }
        binding.player.setAutoPlay(true)
        binding.player.setLoop(true)
        binding.player.setCallback(object : BetterVideoCallback {
            override fun onPrepared(player: BetterVideoPlayer?) {
            }

            override fun onStarted(player: BetterVideoPlayer?) {
            }

            override fun onCompletion(player: BetterVideoPlayer?) {
            }

            override fun onBuffering(percent: Int) {
            }

            override fun onPreparing(player: BetterVideoPlayer?) {
            }

            override fun onError(player: BetterVideoPlayer?, e: Exception?) {
            }

            override fun onToggleControls(player: BetterVideoPlayer?, isShowing: Boolean) {
            }

            override fun onPaused(player: BetterVideoPlayer?) {
            }

        })
    }

    override fun onResume() {
        binding.player.start()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mediacontroller?.hide()
        binding.player.pause()
    }
}