package com.mredrock.cyxbs.store.page.center.ui.item

import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemSmallShopProductBinding
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class SmallShopProductItem(
    private val fragmentActivity: FragmentActivity
) : SimpleRVAdapter.DBItem<StoreRecyclerItemSmallShopProductBinding>(
    R.layout.store_recycler_item_small_shop_product
) {
    override fun isInHere(position: Int): Boolean {
        return position != 0
    }

    override fun create(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        //设置跳转到兑换界面
        binding.storeCvStampSmallShop.onClick {
//            val options = ActivityOptions.makeSceneTransitionAnimation(fragmentActivity,
//                    UtilPair.create(binding.storeBtnSmallShopProductExchange,"exchange"),
//                    UtilPair.create(binding.storeIvSmallShopProduct,"productImage"),
//                    UtilPair.create(binding.storeTvSmallShopPrice,"productPrice"),
//                    UtilPair.create(binding.storeTvSmallShopProductName,"productName"),
//                    UtilPair.create(binding.storeTvSmallShopProductStock,"productStock")
//                    )
//            val options = ActivityOptions.makeSceneTransitionAnimation(fragmentActivity,
//                    UtilPair.create(binding.storeCvStampSmallShop,"productDetail")
//            )
            val intent = Intent(context, ProductExchangeActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun refactor(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
//        binding.storeIvSmallShopProduct.setBackgroundColor(ContextCompat.getColor(context, android.R.color.black))
        val correctPosition = getCorrectPosition(position)
        binding.storeIvSmallShopProduct.setImageFromUrl("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
//        Glide
//            .with(fragmentActivity)
//            .load("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
//            .into(binding.storeIvSmallShopProduct)
    }

    private fun getCorrectPosition(position: Int): Int {
        return position - 1
    }
}