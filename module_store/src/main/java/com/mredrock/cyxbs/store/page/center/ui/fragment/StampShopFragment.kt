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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.store_rv_small_shop)
        viewModel.stampCenterData.observe(viewLifecycleOwner, Observer {
            resetData(it.data.shop)
            if (recyclerView.adapter == null) {
                setAdapter(recyclerView, it.data.userAmount)
            }else {
                refreshAdapter(it.data.shop, it.data.userAmount)
            }
        })
    }

    private val mAdapter = SimpleRVAdapter()
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

    private fun refreshAdapter(shops: List<StampCenter.Shop>, stampCount: Int) {
        mSmallShopTitleItem.resetData(titleMap)
        mSmallShopProductItem.resetData(shopMap, stampCount)
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
}