package com.example.friendlykeyboard.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.friendlykeyboard.fragments.ViewPagerFragment1
import com.example.friendlykeyboard.fragments.ViewPagerFragment2

class ViewPagerAdapter(fa: FragmentActivity, val count: Int) : FragmentStateAdapter(fa) {
    override fun getItemCount() = count

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ViewPagerFragment1()
        1 -> ViewPagerFragment2()
        else -> ViewPagerFragment1()
    }
}