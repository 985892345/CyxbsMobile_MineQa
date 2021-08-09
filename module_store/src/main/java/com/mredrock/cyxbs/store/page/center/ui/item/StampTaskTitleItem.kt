package com.mredrock.cyxbs.store.page.center.ui.item

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskTitleItem : SimpleRVAdapter.VHItem<StampTaskTitleItem.StampTaskMoreVH>(
    R.layout.store_recycler_item_stamp_task_title
) {
    class StampTaskMoreVH(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun isInHere(position: Int): Boolean {
        return position == 4
    }

    override fun getNewViewHolder(itemView: View): StampTaskMoreVH {
        return StampTaskMoreVH(itemView)
    }

    override fun create(holder: StampTaskMoreVH) {
    }

    override fun refactor(holder: StampTaskMoreVH, position: Int) {
    }
}