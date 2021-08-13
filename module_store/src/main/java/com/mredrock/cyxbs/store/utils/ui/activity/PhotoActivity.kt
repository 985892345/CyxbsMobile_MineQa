package com.mredrock.cyxbs.store.utils.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.utils.widget.slideshow.SlideShow
import com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.store_activity_photo.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/9 15:10
 */

class PhotoActivity : AppCompatActivity() {

    private lateinit var mImgUrls: ArrayList<String>
    private var mPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //降低进入activity后的白闪情况
        window.setBackgroundDrawableResource(R.color.store_transparent)
        setContentView(R.layout.store_activity_photo)
        setTheme(R.style.Theme_MaterialComponents) // 因为学长用的奇怪的 dialog, 需要这个主题支持
        setFullScreen()

        initData()
        initView()
    }

    private fun initData() {
        mImgUrls = intent.getStringArrayListExtra("imageUrlList")
        mPosition = intent.getIntExtra("position", 0)
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        val tvPosition: TextView = findViewById(R.id.store_tv_photo_position)

        val slideShow: SlideShow = findViewById(R.id.store_slideShow_photo)
        slideShow
            .setStartItem(mPosition)
            .setPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    //设置图片进度(1/X)
                    tvPosition.post { // TextView 有奇怪的 bug, 改变文字不用 post 就无法改变
                        tvPosition.text = "${position + 1}/${mImgUrls.size}"
                    }
                    //传入当前选中位置
                    val intent = Intent().apply {
                        putExtra("position", position)
                    }
                    setResult(Activity.RESULT_OK, intent)
                }
            })
            .setViewAdapter(
                getNewView = { context -> PhotoView(context) },
                getItemCount = { mImgUrls.size },
                create = { holder ->
                    holder.view.setOnPhotoTapListener { _, _, _ ->
                        finish()
                    }
                    holder.view.setOnLongClickListener {
                        val drawable = holder.view.drawable
                        if (drawable is BitmapDrawable) {
                            val bitmap = drawable.bitmap
                            savePhoto(bitmap, mImgUrls[holder.layoutPosition])
                        }
                        true
                    }
                },
                onBindViewHolder = { view, _, position, _ ->
                    view.setImageFromUrl(mImgUrls[position])
                }
            )
    }

    //对图片保存的处理是照搬 邮问 ViewImageActivity
    private fun savePhoto(bitmap: Bitmap, url: String) {
        doPermissionAction(Manifest.permission.WRITE_EXTERNAL_STORAGE) {
            doAfterGranted {
                com.google.android.material.dialog.MaterialAlertDialogBuilder(this@PhotoActivity)
                    .setTitle(getString(R.string.store_pic_save_alert_dialog_title))
                    .setMessage(R.string.store_pic_save_alert_dialog_message)
                    .setPositiveButton("确定") { dialog, _ ->
                        val name = System.currentTimeMillis().toString() + url.split('/').lastIndex.toString()
                        io.reactivex.schedulers.Schedulers.io().scheduleDirect {
                            this@PhotoActivity.saveImage(bitmap, name)
                            android.media.MediaScannerConnection.scanFile(this@PhotoActivity,
                                arrayOf(android.os.Environment.getExternalStorageDirectory().toString() + com.mredrock.cyxbs.common.config.DIR_PHOTO),
                                arrayOf("image/jpeg"),
                                null)

                            runOnUiThread {
                                toast("图片保存于系统\"${com.mredrock.cyxbs.common.config.DIR_PHOTO}\"文件夹下哦")
                                dialog.dismiss()
                                setFullScreen()
                            }

                        }
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                        setFullScreen()
                    }
                    .show()
            }
        }
    }
}