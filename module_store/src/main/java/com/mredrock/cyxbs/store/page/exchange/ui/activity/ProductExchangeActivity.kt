package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.databinding.StoreActivityProductExchangeBinding
import com.mredrock.cyxbs.store.page.exchange.viewmodel.ProductExchangeViewModel
import com.mredrock.cyxbs.store.ui.activity.PhotoActivity
import com.mredrock.cyxbs.store.ui.fragment.ProductExchangeDialogFragment
import com.mredrock.cyxbs.store.utils.widget.slideshow.viewpager2.transformer.ScaleInTransformer
import kotlinx.android.synthetic.main.store_common_toolbar.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */
class ProductExchangeActivity : BaseViewModelActivity<ProductExchangeViewModel>() {

    private lateinit var dataBinding: StoreActivityProductExchangeBinding
    private var mImageList = ArrayList<String>()
    private var mPosition = 0 //当前VP显示的item的位置
    private val mLauncher = registerForActivityResult(ResultContract()) { result ->
        //从PhotoActivity回到该Activity时 将VP中图片位置移动到退出时PhotoActivity时图片的位置
        dataBinding.storeSlideShowExchangeProductImage.setCurrentItem(result, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //降低进入activity后的白闪情况
        window.setBackgroundDrawableResource(R.color.store_transparent)
        //创建binding
        dataBinding = StoreActivityProductExchangeBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initView()
    }

    private fun initView() {

        //设置左上角返回点击事件
        store_iv_toolbar_arrow_left.setOnClickListener {
            finish()
        }

        //初始化轮播图
        initSlideShow()

        dataBinding.eventHandle = EventHandle()
    }

    private fun initSlideShow() {
        mImageList.add("https://s3-alpha-sig.figma.com/img/2716/1ae8/a90e8b280c050f738d05547af9153217?Expires=1629676800&Signature=YVNkTPufMAVsMB9DEJpITFO9BPAUCXE~VIO0PVrSgQhsaCSetbXOr3Ioph-obX3y59VVpW7eWs-xmJINPuDsGTV-wGMfL-lIUnfPChLkKG96~~9fGvAtT~sQk43eEbmwO50GIeqN8Gf1S31b~OREa0KucuLEE7ZpQso8XLvxU9WBjVSW4n3qjwTnPwqPeMxJrGY4cv3a9342Gagi29bYTy-9i7i4O5XDT9gkDmzZIedVjr9r46ukds-ldUXtlntqZKGfMX1FUY9-xmlUB6qmVWrBEI95AEK~o2XQFBdii3dfi2KNLgxNqmVX5ejFy3PA~WSwujD87bm34pPv0nfpXw__&Key-Pair-Id=APKAINTVSUGEWH5XD5UA")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        dataBinding.storeSlideShowExchangeProductImage
            .addTransformer(ScaleInTransformer())
            .openCirculateEnabled()
            .setImgAdapter(mImageList,
                create = { holder ->
                    holder.view.setOnClickListener {
                        val options =
                            ActivityOptionsCompat.makeSceneTransitionAnimation(
                                this, Pair<View, String>(
                                    dataBinding.storeSlideShowExchangeProductImage,
                                    "productImage"
                                )
                            )
                        mPosition =
                            dataBinding.storeSlideShowExchangeProductImage
                                .getRealPosition(holder.layoutPosition)
                        mLauncher.launch(true, options)
                    }
                },
                refactor = { data, imageView, _, _ ->
                    imageView.setImageFromUrl(data)
                })
    }

    /**
     * 事件处理内部类
     * 通过binding绑定到xml中
     */
    inner class EventHandle {
        //处理单击事件
        fun onItemSingleClick(view: View) {

            when (view.id) {
                R.id.store_btn_exchange -> {
                    //进行判断
                    if (true) {
                        ProductExchangeDialogFragment().apply {
                            initView(
                                dialogRes = R.layout.store_dialog_exchange_product,
                                onPositiveClick = { dismiss() },
                                onNegativeClick = { dismiss() }
                            )
                        }.show(supportFragmentManager, "zz")
                    }
                }
            }
        }
    }

    inner class ResultContract : ActivityResultContract<Boolean, Int>() {

        override fun createIntent(context: Context, input: Boolean): Intent {
            return Intent(context, PhotoActivity::class.java).apply {
                putExtra("position", mPosition)
                putStringArrayListExtra("imageUrlList", mImageList)
            }
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Int {
            if (intent != null) {
                return intent.getIntExtra("position", 0)
            }
            return 0
        }
    }
}