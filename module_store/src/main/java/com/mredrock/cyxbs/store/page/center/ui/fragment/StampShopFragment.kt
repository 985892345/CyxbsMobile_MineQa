package com.mredrock.cyxbs.store.page.center.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.BaseApp
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.center.ui.item.SmallShopProductItem
import com.mredrock.cyxbs.store.page.center.ui.item.SmallShopTitleItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/14
 */
class StampShopFragment : BaseFragment() {

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(StoreCenterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_item_small_shop, container, false)
    }

    // 建立 ViewModel 的数据观察
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.store_rv_small_shop)
        viewModel.stampCenterData.observe(viewLifecycleOwner, Observer {
            resetData(it.data.shop) // 重新设置数据
            if (recyclerView.adapter == null) { // 第一次得到数据时设置 adapter
                setAdapter(recyclerView, it.data.userAmount)
            }else {
                refreshAdapter(it.data.shop, it.data.userAmount) // 再次得到数据时刷新
            }
        })
    }

    private val mAdapter = SimpleRVAdapter() // 自己写的解耦 Adapter 的封装类, 可用于解耦不同的 item
    private lateinit var mSmallShopTitleItem: SmallShopTitleItem
    private lateinit var mSmallShopProductItem: SmallShopProductItem
    private fun setAdapter(recyclerView: RecyclerView, stampCount: Int) {
        val layoutManager = GridLayoutManager(BaseApp.context, 2)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = if (titleMap.containsKey(position)) 2 else 1
        }
        recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    BaseApp.context,
                    R.anim.store_product_fade_in
                )
            )
        recyclerView.layoutManager = layoutManager
        mSmallShopTitleItem = SmallShopTitleItem(titleMap)
        mSmallShopProductItem = SmallShopProductItem(shopMap, stampCount)
        recyclerView.adapter = mAdapter
            .addItem(mSmallShopTitleItem)
            .addItem(mSmallShopProductItem)
            .show()
    }

    // 用于再次得到数据后的刷新 adapter
    private fun refreshAdapter(shops: List<StampCenter.Shop>, stampCount: Int) {
        mSmallShopTitleItem.resetData(titleMap)
        mSmallShopProductItem.resetData(shopMap, stampCount)
        // 使用了自己封装的 DiffUtil 来刷新, 避免使用 notifyDataSetChanged
        // 你要是觉得过于麻烦请使用 refreshYYDS()
        // 不可自己调用 notifyDataSetChanged(), 因为你的 itemCount 将无法改变
        mAdapter.refreshAuto(
            true,
            shops.size + 2,
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

    // 艹, 接口不同的 type 要自己去区分, 这个 kings[0] 装的装扮, kinds[1] 装的邮货
    private val kinds = ArrayList<ArrayList<StampCenter.Shop>>(2)
    private val titleMap = HashMap<Int, String>() // adapter 的 position 与标题的映射
    private val shopMap = HashMap<Int, StampCenter.Shop>() // adapter 的 position 与商品数据的映射
    private val oldAllMap = HashMap<Int, Any>() // 用于 refreshAdapter() 方法中使用 DiffUtil 来比对刷新
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
            kinds[shop.type].add(shop) // 因为后端返回的 type = 0 时为装扮, type = 1 时为邮货
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
}