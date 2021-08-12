package com.mredrock.cyxbs.store.utils.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.common.utils.extensions.*
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.utils.ui.adapter.PhotoVPAdapter
import kotlinx.android.synthetic.main.store_activity_photo.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/9 15:10
 */

class PhotoActivity : AppCompatActivity() {

    private lateinit var mPhotoVPAdapter: PhotoVPAdapter
    private var mImgUrls: ArrayList<String>? = null
    private var mPosition = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //降低进入activity后的白闪情况
        window.setBackgroundDrawableResource(R.color.store_transparent)
        setContentView(R.layout.store_activity_photo)
        setTheme(R.style.Theme_MaterialComponents)
        setFullScreen()

        initData()
        initAdapter()
        initView()
    }

    private fun initData() {
        mImgUrls = intent.getStringArrayListExtra("imageUrlList")
        mPosition = intent.getIntExtra("position", 0)
    }

    private fun initAdapter() {
        //对图片保存的处理是照搬 邮问 ViewImageActivity
        mPhotoVPAdapter = PhotoVPAdapter(mImgUrls, this,
                photoTapClick = { finish() },
                savePicClick = { bitmap, url ->
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
                })
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        store_vp_product_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                //设置图片进度(1/X)
                store_tv_position.text = "${position + 1}/${mImgUrls?.size?.minus(2)}"
                //传入当前选中位置
                val intent = Intent().apply {
                    putExtra("position", position + 1)
                }
                setResult(Activity.RESULT_OK, intent)
            }
        })
        store_vp_product_image.adapter = mPhotoVPAdapter
        store_tv_position.text = "1/${mImgUrls?.size}"
        store_vp_product_image.setCurrentItem(mPosition, false)
    }
}