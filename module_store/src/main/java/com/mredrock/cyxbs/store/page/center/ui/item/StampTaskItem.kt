package com.mredrock.cyxbs.store.page.center.ui.item

import android.animation.AnimatorInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskItem : SimpleRVAdapter.VHItem<StampTaskItem.StampShopTitleVH>(
    R.layout.store_item_stamp_task
) {
    class StampShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.store_rv_stamp_task)
    }

    override fun isInHere(position: Int): Boolean {
        return position == 1
    }

    override fun getNewViewHolder(itemView: View): StampShopTitleVH {
        return StampShopTitleVH(itemView)
    }

    override fun create(holder: StampShopTitleVH) {
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    holder.recyclerView.context,
                    R.anim.store_slide_from_left_to_right_in
                )
            )
        holder.recyclerView.adapter = SimpleRVAdapter(10)
            .addItem(StampTaskTitleItem())
            .addItem(StampTaskListItem())
    }

    override fun refactor(holder: StampShopTitleVH, position: Int) {
    }
}