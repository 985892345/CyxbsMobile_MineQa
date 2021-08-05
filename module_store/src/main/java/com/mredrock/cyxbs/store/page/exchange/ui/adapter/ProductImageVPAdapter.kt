package com.mredrock.cyxbs.store.page.exchange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.module_store.R
import com.mredrock.cyxbs.common.component.showPhotos
import com.mredrock.cyxbs.common.utils.extensions.onClick

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 12:40
 */
class ProductImageVPAdapter(private val productImageList:ArrayList<String>) : RecyclerView.Adapter<ProductImageVPAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.store_item_product_image, parent,false))
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {

        holder.productImage.onClick {
//            showPhotos(holder.itemView.context, arrayListOf("https://fanyi-cdn.cdn.bcebos.com/static/translation/img/header/logo_e835568.png"))
        }
    }

    override fun getItemCount(): Int {
        return productImageList.size
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.store_iv_product_image)
    }

}