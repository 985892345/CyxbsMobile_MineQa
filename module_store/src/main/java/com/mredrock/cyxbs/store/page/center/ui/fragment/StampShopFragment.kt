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
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity
import com.mredrock.cyxbs.store.utils.Type

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/14
 */
class StampShopFragment : BaseFragment() {

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProvider(requireActivity()).get(StoreCenterViewModel::class.java)
    }

    /**
     * 启动 [ProductExchangeActivity] 的启动器
     *
     * 1、因为要判断 [ProductExchangeActivity] 是否成功购买了商品, 所以需要使用到 [startActivityForResult],
     * 但该方法被废弃了
     *
     * 2、新方法有一个麻烦的地方, 就是必须在 [onStart] 回调前先使用 [registerForActivityResult]
     * 方法注册结果回调
     *
     * 3、注册后会返回一个启动器用于启动, 我再用了一个接口来封装, 所以下面这个就是一个启动器,
     * 该启动器在 [SmallShopProductItem] 中点击整个商品图片时进行启动
     */
    private lateinit var mProductExchangeLauncher: ProductExchangeActivity.IProductExchangeLauncher
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mProductExchangeLauncher = ProductExchangeActivity.getActivityLauncher(this) {
            if (it) viewModel.refresh() // 返回值是是否购买, 如果购买了就刷新数据
        }
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
            resetData(it.shop) // 重新设置数据
            if (recyclerView.adapter == null) { // 第一次得到数据时设置 adapter
                setAdapter(recyclerView, it.userAmount)
            }else {
                refreshAdapter(it.userAmount) // 再次得到数据时刷新
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
        mSmallShopProductItem = SmallShopProductItem(shopMap, stampCount, mProductExchangeLauncher)
        recyclerView.adapter = mAdapter
            .addItem(mSmallShopTitleItem)
            .addItem(mSmallShopProductItem)
            .show()
    }

    // 用于再次得到数据后的刷新 adapter
    private fun refreshAdapter(stampCount: Int) {
        mSmallShopTitleItem.resetData(titleMap)
        mSmallShopProductItem.resetData(shopMap, stampCount)
        mAdapter.refreshYYDS()
    }

    private val dressList = ArrayList<StampCenter.Shop>()
    private val goodsList = ArrayList<StampCenter.Shop>()
    private val titleMap = HashMap<Int, String>() // adapter 的 position 与标题的映射
    private val shopMap = HashMap<Int, StampCenter.Shop>() // adapter 的 position 与商品数据的映射
    private fun resetData(products: List<StampCenter.Shop>) {
        dressList.clear()
        goodsList.clear()
        titleMap.clear()
        shopMap.clear()
        // 为什么要遍历一边?
        // 因为后端不同 type 是混在一起的, 不遍历的话我就不知道 "邮货" 这个 title 是在哪个位置
        for (shop in products) {
            when (shop.type) { // 后端返回的 type = 1 时为装扮, type = 0 时为邮货
                Type.Product.DRESS -> dressList.add(shop)
                Type.Product.GOODS -> goodsList.add(shop)
            }
        }
        titleMap[0] = "装扮"
        titleMap[dressList.size + 1] = "邮货"
        for (i in dressList.indices) {
            shopMap[i + 1] = dressList[i]
        }
        for (i in goodsList.indices) {
            shopMap[dressList.size + 2 + i] = goodsList[i]
        }
    }
}