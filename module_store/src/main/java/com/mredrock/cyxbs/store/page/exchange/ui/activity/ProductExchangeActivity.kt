package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.viewpager2.widget.ViewPager2
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreActivityProductExchangeBinding
import com.mredrock.cyxbs.store.page.exchange.ui.item.ProductImageItem
import com.mredrock.cyxbs.store.page.exchange.viewmodel.ProductExchangeViewModel
import com.mredrock.cyxbs.store.utils.ui.activity.PhotoActivity
import com.mredrock.cyxbs.store.utils.ui.fragment.ProductExchangeDialogFragment
import com.mredrock.cyxbs.store.utils.widget.ZoomOutPageTransformer
import kotlinx.android.synthetic.main.store_activity_product_exchange.*
import kotlinx.android.synthetic.main.store_common_toolbar.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 11:55
 */
class ProductExchangeActivity : BaseViewModelActivity<ProductExchangeViewModel>() {

    private var mImageViewPagerAdapter: SimpleRVAdapter? = null
    private lateinit var dataBinding: StoreActivityProductExchangeBinding
    private var mImageList = ArrayList<String>()
    private var mPosition = 0 //当前VP显示的item的位置
    private val mLauncher = registerForActivityResult(ResultContract()) { result ->
        //从PhotoActivity回到该Activity时 将VP中图片位置移动到退出时PhotoActivity时图片的位置
        store_vp_product_image.setCurrentItem(result, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //创建binding
        dataBinding = StoreActivityProductExchangeBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        initAdapter()
        initView()
        initData()
    }

    private fun initView() {

        //设置左上角返回点击事件
        store_iv_toolbar_arrow_left.setOnClickListener {
            finish()
        }

        //初始化可循环滑动的VP
        initViewPager2()

        dataBinding.eventHandle = EventHandle()
    }

    private fun initViewPager2() {
        //设置切换动画
        store_vp_product_image.setPageTransformer(ZoomOutPageTransformer())
        //设置起始页 通过 page：2 0 1 2 0 来实现 0 1 2 界面的循环滑动
        store_vp_product_image.setCurrentItem(1, false)
        //添加VP的页面选中监听 来控制圆点重绘
        store_vp_product_image.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                //更新圆点
                store_progress_dot.updatePosition(position - 1, positionOffset)
                //进行界面跳转 实现循环
                if (position == mImageList.size - 1) {
                    store_vp_product_image.setCurrentItem(1, false)
                }
                if (position == 0) {
                    store_vp_product_image.setCurrentItem(mImageList.size - 1, false)
                }
            }

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mPosition = position - 1
            }
        })
    }

    private fun initAdapter() {
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")
        mImageList.add("http://hakaimg.com/i/2021/08/09/nr64i7.jpg")

        mImageViewPagerAdapter = SimpleRVAdapter(5)
                .addItem(ProductImageItem(mImageList, mLauncher, this))

        store_vp_product_image.adapter = mImageViewPagerAdapter
    }

    private fun initData() {

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

        override fun createIntent(context: Context, input: Boolean?): Intent {
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