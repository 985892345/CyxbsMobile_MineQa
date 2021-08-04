package com.mredrock.cyxbs.store.page.record.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 13:36
 */
class StampDetailVPAdapter<T>(
        fragmentActivity: FragmentActivity,
        private var fragmentList: ArrayList<T>
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position] as Fragment
    }
}