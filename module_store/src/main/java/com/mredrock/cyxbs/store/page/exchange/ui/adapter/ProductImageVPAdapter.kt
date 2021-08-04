package com.mredrock.cyxbs.store.page.exchange.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.module_store.R

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/4 12:40
 */
class ProductImageVPAdapter(val productImageList:ArrayList<String>) : RecyclerView.Adapter<ProductImageVPAdapter.ImageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.store_item_product_image, parent,false))
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
//        holder.productImage
    }

    override fun getItemCount(): Int {
        return 3
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var productImage: ImageView = itemView.findViewById(R.id.store_iv_product_image)
    }

}