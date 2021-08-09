package com.mredrock.cyxbs.store.page.center.ui.item

import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemSmallShopProductBinding

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
    }

    override fun refactor(
        binding: StoreRecyclerItemSmallShopProductBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
        val correctPosition = getCorrectPosition(position)
        binding.storeIvSmallShopProduct.setImageDrawable(
            ContextCompat.getDrawable(
                fragmentActivity,
                R.drawable.img_product_example
            )
        )
//        Glide
//            .with(fragmentActivity)
//            .load(data)
//            .into(binding.storeIvSmallShopProduct)
    }

    fun getCorrectPosition(position: Int): Int {
        return position - 1
    }
}