package com.mredrock.cyxbs.store.page.exchange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.page.exchange.ui.activity.ProductExchangeActivity


/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 12:40
 */
class ProductImageVPAdapter(private val productImageUrlList: ArrayList<String>, private val launcher: ActivityResultLauncher<Boolean>, private val productExchangeActivity: ProductExchangeActivity) : RecyclerView.Adapter<ProductImageVPAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.store_item_product_image, parent, false))
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
//        holder.productImage.scaleType=ImageView.ScaleType.FIT_XY
        holder.productImage.setImageFromUrl(productImageUrlList[position])
        //设置元素共享所需要的 transitionName
        holder.productImage.transitionName = "productImage"
        holder.productImage.onClick {
//            ImageActivity.activityStart(holder.itemView.context, arrayOf("http://hakaimg.com/i/2021/08/09/nr64i7.jpg","https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png","https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png"), position-1)
//            showPhotos(holder.itemView.context, arrayListOf("https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png"))
//            productExchangeActivity.startActivityForResult<>(productExchangeActivity, arrayOf("http://hakaimg.com/i/2021/08/09/nr64i7.jpg", "http://hakaimg.com/i/2021/08/09/nr64i7.jpg", "http://hakaimg.com/i/2021/08/09/nr64i7.jpg"), position - 1)
//
//            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(productExchangeActivity, Pair<View, String>(holder.productImage, "productImage"))

            launcher.launch(true)
        }
    }

    override fun getItemCount(): Int {
        return productImageUrlList.size
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.store_iv_product_image)
    }

}