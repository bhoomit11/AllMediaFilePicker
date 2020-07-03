package com.bb.allmediafilepicker.utils.gallery

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class GalleryPagerFragmentAdapter(manager:FragmentManager,private val mediaList: ArrayList<GalleryResponse>) : FragmentStatePagerAdapter(manager,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
    override fun getItem(position: Int): Fragment {
        return GalleryPagerFrament.getInstance(mediaList[position])
    }

    override fun getCount(): Int {
        return mediaList.size
    }
}