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
    private val titleList: List<String>
) : SimpleRVAdapter.VHItem<SmallShopTitleItem.SmallShopTitleVH>(
    R.layout.store_recycler_item_small_shop_title
) {
    class SmallShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.store_tv_small_shop_title)
    }

    override fun isInHere(position: Int): Boolean {
        return position == 0
    }

    override fun getNewViewHolder(itemView: View): SmallShopTitleVH {
        return SmallShopTitleVH(itemView)
    }

    override fun create(holder: SmallShopTitleVH) {
    }

    override fun refactor(holder: SmallShopTitleVH, position: Int) {
        holder.title.text = titleList[getTitlePosition(position)]
    }

    private fun getTitlePosition(position: Int): Int {
        return 0
    }
}