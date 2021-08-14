package com.mredrock.cyxbs.store.page.center.ui.item

import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.store.bean.StampCenter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class SmallShopItem : SimpleRVAdapter.VHItem<SmallShopItem.SmallShopVH>(
    R.layout.store_item_small_shop
) {

    private lateinit var mSmallShopTitleItem: SmallShopTitleItem
    private lateinit var mSmallShopProductItem: SmallShopProductItem
    private var mAdapter =  SimpleRVAdapter()
    fun refreshData(products: List<StampCenter.Shop>) {
        resetData(products)
        if (mAdapter.itemCount == 0) {
            mSmallShopTitleItem = SmallShopTitleItem(titleMap)
            mSmallShopProductItem = SmallShopProductItem(shopMap)
            mAdapter = SimpleRVAdapter()
                .addItem(mSmallShopTitleItem)
                .addItem(mSmallShopProductItem)
                .show()
            refreshMySelf(false)
            return
        }
        mSmallShopTitleItem.resetData(titleMap)
        mSmallShopProductItem.resetData(shopMap)
        mAdapter.refreshAuto(
            true,
            products.size + 2,
            true,
            isItemTheSame = { oldItemPosition, newItemPosition ->
                val old = oldAllMap[oldItemPosition]
                if (old is StampCenter.Shop) {
                    shopMap.containsKey(newItemPosition)
                }else {
                    titleMap.containsKey(newItemPosition)
                }
            },
            isContentsTheSame = { oldItemPosition, newItemPosition ->
                val old = oldAllMap[oldItemPosition]
                if (old is StampCenter.Shop) {
                    val shop = shopMap[newItemPosition]
                    if (shop != null) {
                        shop == old
                    }else false
                }else {
                    true
                }
            }
        )
    }

    private val kinds = ArrayList<ArrayList<StampCenter.Shop>>(2)
    private val titleMap = HashMap<Int, String>()
    private val shopMap = HashMap<Int, StampCenter.Shop>()
    private val oldAllMap = HashMap<Int, Any>()
    private fun resetData(products: List<StampCenter.Shop>) {
        oldAllMap.clear()
        oldAllMap.putAll(titleMap)
        oldAllMap.putAll(shopMap)
        kinds.clear()
        titleMap.clear()
        shopMap.clear()
        kinds.add(ArrayList())
        kinds.add(ArrayList())
        for (shop in products) {
            kinds[shop.type].add(shop)
        }
        titleMap[0] = "装扮"
        titleMap[kinds[0].size + 1] = "邮货"
        for (i in 0 until kinds[0].size) {
            shopMap[i + 1] = kinds[0][i]
        }
        for (i in 0 until kinds[1].size) {
            shopMap[kinds[0].size + 2 + i] = kinds[1][i]
        }
    }

    class SmallShopVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.store_rv_small_shop)
    }

    override fun isInHere(position: Int): Boolean {
        return position == 0
    }

    override fun getNewViewHolder(itemView: View): SmallShopVH {
        return SmallShopVH(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun create(holder: SmallShopVH) {
        holder.recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.store_product_fade_in
                )
            )
    }

    override fun refactor(holder: SmallShopVH, position: Int) {
    }

    override fun refresh(holder: SmallShopVH, position: Int) {
        val layoutManager = GridLayoutManager(context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (titleMap.containsKey(position)) 2 else 1
        }
        holder.recyclerView.layoutManager = layoutManager
        holder.recyclerView.adapter = mAdapter
    }
}