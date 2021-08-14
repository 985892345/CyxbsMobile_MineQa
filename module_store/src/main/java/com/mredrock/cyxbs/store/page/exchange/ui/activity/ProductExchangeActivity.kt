package com.mredrock.cyxbs.store.page.exchange.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import com.mredrock.cyxbs.common.ui.BaseViewModelActivity
import com.mredrock.cyxbs.common.utils.extensions.setImageFromUrl
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.bean.ProductDetail
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
    private var mStampCount = 0 //我的余额
    private var mId = "" //商品ID
    private lateinit var mData: ProductDetail.Data
    private val mLauncher = registerForActivityResult(ResultContract()) { result ->
        //从PhotoActivity回到该Activity时 将VP中图片位置移动到退出时PhotoActivity时图片的位置
        dataBinding.storeSlideShowExchangeProductImage.setCurrentItem(result, false)
    }

    companion object {
        fun activityStart(context: Context, id: String, stampCount: Int) {
            val intent = Intent(context, ProductExchangeActivity::class.java)
            intent.putExtra("id", id)
            intent.putExtra("stampCount", stampCount)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //降低进入activity后的白闪情况
        window.setBackgroundDrawableResource(R.color.store_transparent)
        //创建binding
        dataBinding = StoreActivityProductExchangeBinding.inflate(layoutInflater)
        setContentView(dataBinding.root)
        dataBinding.lifecycleOwner = this
        initView()
        initObserve()
        initData()
        //绑定相关点击事件并设置数据
        dataBinding.eventHandle = EventHandle()
        dataBinding.storeTvUserStampCount.text = mStampCount.toString()
    }


    private fun initData() {
        mId = intent.getStringExtra("id")
        mStampCount = intent.getIntExtra("stampCount", 0)
        //得到商品详细
        viewModel.getProductDetail("1")
//        viewModel.getProductDetail(mId)
    }

    @SuppressLint("SetTextI18n")
    private fun initObserve() {
        viewModel.productDetail.observe {
            if (it != null) {
                dataBinding.data = it
                //处理权益说明
                when (it.type) {
                    1 -> {
                        dataBinding.storeTvEquityDescription.text = "1、每个实物商品每人限兑换一次，已经兑换的商品不能退货换货也不予折现。\n2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
                    }
                    0 -> {
                        dataBinding.storeTvEquityDescription.text = "1、虚拟商品版权归红岩网校工作站所有。\n2、在法律允许的范围内，本活动的最终解释权归红岩网校工作站所有。"
                    }
                }
                //设置轮播图UrlList
                mImageList = it.urls as ArrayList<String>
                //初始化轮播图
                initSlideShow()
                //保存
                mData = it
            }
        }
        viewModel.exchangeResult.observe {
            if (it != null) {
                when (it) {
                    "reduce goods error" -> {
                        ProductExchangeDialogFragment().apply {
                            initView(
                                    dialogRes = R.layout.store_dialog_exchange_result,
                                    onPositiveClick = { dismiss() },
                                    exchangeTips = "啊欧，手慢了！下次再来吧=.="
                            )
                        }.show(supportFragmentManager, "zz")
                    }
                    "Integral not enough" -> {
                        ProductExchangeDialogFragment().apply {
                            initView(
                                    dialogRes = R.layout.store_dialog_exchange_result,
                                    onPositiveClick = { dismiss() },
                                    exchangeTips = "诶......邮票不够啊......穷日子真不好过呀QAQ"
                            )
                        }.show(supportFragmentManager, "zz")
                    }
                    "success" -> {
                        //根据不同商品类型弹出不同dialog
                        if (::mData.isInitialized) {
                            when (mData.type) {
                                0 -> {
                                    ProductExchangeDialogFragment().apply {
                                        initView(
                                                dialogRes = R.layout.store_dialog_exchange_product,
                                                onPositiveClick = { dismiss() },
                                                onNegativeClick = { dismiss() },
                                                exchangeTips = "兑换成功！现在就换掉原来的名片吧！",
                                                positiveString = "好的",
                                                negativeString = "再想想"
                                        )
                                    }.show(supportFragmentManager, "zz")
                                }
                                1 -> {
                                    ProductExchangeDialogFragment().apply {
                                        initView(
                                                dialogRes = R.layout.store_dialog_exchange_result,
                                                onPositiveClick = { dismiss() },
                                                exchangeTips = "兑换成功！请在30天内到红岩网校领取哦"
                                        )
                                    }.show(supportFragmentManager, "zz")
                                }
                            }
                        }

                    }

                }
            }
        }
    }

    private fun initView() {

        //设置左上角返回点击事件
        store_iv_toolbar_arrow_left.setOnClickListener {
            finish()
        }
    }

    private fun initSlideShow() {

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

                    ProductExchangeDialogFragment().apply {
                        initView(
                                dialogRes = R.layout.store_dialog_exchange_product,
                                onPositiveClick = {
                                    //请求获取用户是否能购买
                                    viewModel.exchangeProduct(mId)
                                    dismiss()
                                },

                                onNegativeClick = { dismiss() },
                                exchangeTips = "确认要用${dataBinding.storeTvExchangeDetailPrice.text}邮票兑换${dataBinding.storeTvProductName.text}吗？"
                        )
                    }.show(supportFragmentManager, "zz")
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