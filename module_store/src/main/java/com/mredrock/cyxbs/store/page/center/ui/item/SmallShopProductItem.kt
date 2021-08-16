package com.mredrock.cyxbs.store.page.center.ui.item

import android.annotation.SuppressLint
import android.util.Log
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemSmallShopProductBinding
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRVAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class SmallShopProductItem(
    private var shopMap: HashMap<Int, StampCenter.Shop>,
    private var stampCount: Int
): SimpleRVAdapter.DBItem<StoreRecyclerItemSmallShopProductBinding>(
    R.layout.store_recycler_item_small_shop_product
) {

    fun resetData(shopMap: HashMap<Int, StampCenter.Shop>, stampCount: Int) {
        this.shopMap = shopMap
        this.stampCount = stampCount
    }

    override fun getItemCount(): Int {
        return shopMap.size
    }

    override fun isInHere(position: Int): Boolean {
        return shopMap.containsKey(position)
    }

    override fun create(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        //设置跳转到兑换界面
        binding.storeCvStampSmallShop.onClick {
            val shop = shopMap[holder.layoutPosition]
            if (shop != null) {
                ProductExchangeActivity.activityStart(
                    context,
                    shop.id.toString(), // 点击的商品 id
                    stampCount // 自己还剩下的邮票数量
                )
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun refactor(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
        val shop = shopMap[position]
        if (shop != null) {
            binding.storeIvSmallShopProduct.setImageFromUrl(shop.url)
            binding.storeTvSmallShopProductName.text = shop.title
            binding.storeTvSmallShopPrice.text = shop.price.toString()
            binding.storeTvSmallShopProductStock.text = "库存：${shop.amount}"
        }
    }
}