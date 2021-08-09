package com.mredrock.cyxbs.store.page.center.ui.item

import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
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
class SmallShopItem(
    private val fragmentActivity: FragmentActivity
) : SimpleRVAdapter.VHItem<SmallShopItem.SmallShopVH>(
    R.layout.store_item_small_shop
) {
    class SmallShopVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recycler: RecyclerView = itemView.findViewById(R.id.store_rv_small_shop)
    }

    override fun isInHere(position: Int): Boolean {
        return position == 0
    }

    override fun getNewViewHolder(itemView: View): SmallShopVH {
        return SmallShopVH(itemView)
    }

    override fun create(holder: SmallShopVH) {
        val layoutManager = GridLayoutManager(fragmentActivity, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (position == 0) 2 else 1
        }
        holder.recycler.layoutManager = layoutManager
        holder.recycler.adapter = SimpleRVAdapter(10)
            .addItem(SmallShopTitleItem(listOf("装扮")))
            .addItem(SmallShopProductItem(fragmentActivity))
    }

    override fun refactor(holder: SmallShopVH, position: Int) {
    }
}