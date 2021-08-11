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
        //设置元素共享所需要的 transitionName
        holder.productImage.transitionName = "productImage"
        holder.productImage.onClick {
//            ImageActivity.activityStart(holder.itemView.context, arrayOf("http://hakaimg.com/i/2021/08/09/nr64i7.jpg","https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png","https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png"), position-1)
//            showPhotos(holder.itemView.context, arrayListOf("https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png"))
//            productExchangeActivity.startActivityForResult<>(productExchangeActivity, arrayOf("http://hakaimg.com/i/2021/08/09/nr64i7.jpg", "http://hakaimg.com/i/2021/08/09/nr64i7.jpg", "http://hakaimg.com/i/2021/08/09/nr64i7.jpg"), position - 1)
//
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(productExchangeActivity, Pair<View, String>(holder.productImage, "productImage"))

            launcher.launch(true, options)
        }
    }

    override fun refactor(holder: ProductImageVH, position: Int) {
        holder.productImage.setImageFromUrl(productImageUrlList[position])

    }

    override fun refresh(holder: ProductImageVH, position: Int) {
        super.refresh(holder, position)
    }
}