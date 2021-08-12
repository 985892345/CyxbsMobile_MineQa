package com.mredrock.cyxbs.store.utils.ui.adapter

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/9 15:05
 */

class PhotoVPAdapter(private val productImageUrlList: ArrayList<String>?, val savePicClick: ((Bitmap, String) -> Unit)? = null,
                     val photoTapClick: (() -> Unit)? = null) : RecyclerView.Adapter<PhotoVPAdapter.ImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        return ImageHolder(LayoutInflater.from(parent.context).inflate(R.layout.store_item_photo, parent, false))
    }

    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        //对图片保存的处理照搬邮问 HackyViewPagerAdapter
        if (productImageUrlList != null) {
            holder.productImage.setImageFromUrl(productImageUrlList[position])
        }
        holder.productImage.setOnLongClickListener {
            val drawable = holder.productImage.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = (holder.productImage.drawable as BitmapDrawable).bitmap
                if (!productImageUrlList.isNullOrEmpty())
                    savePicClick?.invoke(bitmap, productImageUrlList[position])
            }
            true
        }
        holder.productImage.setOnPhotoTapListener { view, x, y ->
            photoTapClick?.invoke()
        }
    }

    override fun getItemCount(): Int {
        return if (productImageUrlList?.size != null) {
            productImageUrlList.size - 2
        } else {
            0
        }
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: PhotoView = itemView.findViewById(R.id.store_pv_view_image)
    }

}