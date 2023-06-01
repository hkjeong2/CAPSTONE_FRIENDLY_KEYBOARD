package com.example.friendlykeyboard.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.friendlykeyboard.fragments.ViewPagerFragment1
import com.example.friendlykeyboard.fragments.ViewPagerFragment2
import com.example.friendlykeyboard.fragments.ViewPagerFragment3
import com.example.friendlykeyboard.fragments.ViewPagerFragment4
import com.example.friendlykeyboard.fragments.ViewPagerFragment5
import com.example.friendlykeyboard.fragments.ViewPagerFragment6

class ViewPagerAdapter(fa: FragmentActivity, val count: Int) : FragmentStateAdapter(fa) {
    override fun getItemCount() = count

    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ViewPagerFragment1()
        1 -> ViewPagerFragment2()
        2 -> ViewPagerFragment3()
        3 -> ViewPagerFragment4()
        4 -> ViewPagerFragment5()
        5 -> ViewPagerFragment6()
        else -> ViewPagerFragment1()
    }
}