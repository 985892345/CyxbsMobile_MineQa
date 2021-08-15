package com.mredrock.cyxbs.store.page.center.ui.item

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRVAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskTitleItem(
    private var titleMap: Map<Int, String>
) : SimpleRVAdapter.VHItem<StampTaskTitleItem.StampTaskMoreVH>(
    R.layout.store_recycler_item_stamp_task_title
) {

    fun resetData(titleMap: Map<Int, String>) {
        this.titleMap = titleMap
    }

    class StampTaskMoreVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.store_tv_stamp_task_title)
    }

    override fun isInHere(position: Int): Boolean {
        return titleMap.containsKey(position)
    }

    override fun getNewViewHolder(itemView: View): StampTaskMoreVH {
        return StampTaskMoreVH(itemView)
    }

    override fun getItemCount(): Int {
        return titleMap.size
    }

    override fun create(holder: StampTaskMoreVH) {
    }

    override fun refactor(holder: StampTaskMoreVH, position: Int) {
        holder.tvTitle.text = titleMap[position]
    }
}