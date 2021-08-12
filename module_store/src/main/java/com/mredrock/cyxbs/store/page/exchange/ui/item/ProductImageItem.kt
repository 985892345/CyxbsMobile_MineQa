package com.mredrock.cyxbs.store.page.exchange.ui.item

import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity
import kotlinx.android.synthetic.main.store_activity_product_exchenge.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/10 9:42
 */
class ProductImageItem(private val productImageUrlList: ArrayList<String>, private val launcher: ActivityResultLauncher<Boolean>, private val productExchangeActivity: ProductExchangeActivity) : SimpleRVAdapter.VHItem<ProductImageItem.ProductImageVH>(
        R.layout.store_item_product_image
) {
    class ProductImageVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.store_iv_product_image)
    }

    override fun isInHere(position: Int): Boolean {
        return position in 0 until productImageUrlList.size
    }

    override fun getNewViewHolder(itemView: View): ProductImageVH {
        return ProductImageVH(itemView)
    }

    override fun create(holder: ProductImageVH) {
        holder.productImage.scaleType = ImageView.ScaleType.FIT_CENTER
        holder.productImage.onClick {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(productExchangeActivity, Pair<View, String>(productExchangeActivity.store_vp_product_image, "productImage"))

            launcher.launch(true, options)
        }
    }

    override fun refactor(holder: ProductImageVH, position: Int) {
        holder.productImage.setImageFromUrl(productImageUrlList[position])

    }
}