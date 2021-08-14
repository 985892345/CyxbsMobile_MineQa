package com.mredrock.cyxbs.store.page.center.ui.item

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class SmallShopTitleItem(
    private var titleMap: Map<Int, String>
) : SimpleRVAdapter.VHItem<SmallShopTitleItem.SmallShopTitleVH>(
    R.layout.store_recycler_item_small_shop_title
) {

    fun resetData(titleMap: Map<Int, String>) {
        this.titleMap = titleMap
    }

    class SmallShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.store_tv_small_shop_title)
    }

    override fun isInHere(position: Int): Boolean {
        return titleMap.containsKey(position)
    }

    override fun getNewViewHolder(itemView: View): SmallShopTitleVH {
        return SmallShopTitleVH(itemView)
    }

    override fun getItemCount(): Int {
        return titleMap.size
    }

    override fun create(holder: SmallShopTitleVH) {
    }

    override fun refactor(holder: SmallShopTitleVH, position: Int) {
        val title = titleMap[position]
        if (title != null) {
            holder.title.text = title
        }
    }
}