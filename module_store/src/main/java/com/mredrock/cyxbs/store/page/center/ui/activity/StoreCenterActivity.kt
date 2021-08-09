package com.mredrock.cyxbs.store.page.center.ui.activity

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.mredrock.cyxbs.common.config.STORE_CENTER
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel

/**
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/7
 */
@Route(path = STORE_CENTER)
class StoreCenterActivity : BaseViewModelActivity<StoreCenterViewModel>() {

    private lateinit var mViewPager2: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_store_center)
        initView()
        initViewPager2()
    }

    private fun initView() {
        mViewPager2 = findViewById(R.id.vp_stamp_center)
    }

    private fun initViewPager2() {
        mViewPager2.adapter = SimpleRVAdapter(2)
            .addItem(SmallShopItem())
            .addItem(StampTaskItem())
    }

    private class SmallShopItem : SimpleRVAdapter.VHItem<SmallShopItem.SmallShopVH>(
        R.layout.store_item_small_shop
    ) {
        class SmallShopVH(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun isInHere(position: Int): Boolean {
            return position == 0
        }

        override fun getNewViewHolder(itemView: View): SmallShopVH {
            return SmallShopVH(itemView)
        }

        override fun create(holder: SmallShopVH) {
        }

        override fun refactor(holder: SmallShopVH, position: Int) {

        }

        override fun refresh(holder: SmallShopVH, position: Int) {

            super.refresh(holder, position)
        }
    }

    private class StampTaskItem : SimpleRVAdapter.VHItem<StampTaskItem.StampShopTitleVH>(
        R.layout.store_item_stamp_task
    ) {
        class StampShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        }

        override fun isInHere(position: Int): Boolean {
            return position == 1
        }

        override fun getNewViewHolder(itemView: View): StampShopTitleVH {
            return StampShopTitleVH(itemView)
        }

        override fun create(holder: StampShopTitleVH) {
        }

        override fun refactor(holder: StampShopTitleVH, position: Int) {
        }
    }
}