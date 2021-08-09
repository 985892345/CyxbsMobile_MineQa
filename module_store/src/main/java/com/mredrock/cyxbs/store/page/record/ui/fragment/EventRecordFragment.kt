package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.launcher.ARouter
import com.mredrock.cyxbs.common.config.STORE_EXCHANGE_DETAIL
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.doIfLogin
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.BaseSimplifyRecyclerAdapter2
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemExchangeRecordBinding
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampGetRecordBinding
import com.mredrock.cyxbs.store.page.record.viewmodel.EventRecordViewModel
import kotlinx.android.synthetic.main.store_fragment_event_record.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */

class EventRecordFragment : BaseViewModelFragment<EventRecordViewModel>() {
    private lateinit var mEventRVAdapter: BaseSimplifyRecyclerAdapter2<Any>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.store_fragment_event_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAdapter()
        initData()
        initView()
        super.onViewCreated(view, savedInstanceState)
    }


    /**
     * 根据event的不同 通过VM获取对应的数据
     */
    private fun initData() {
        when (arguments?.getString("event")) {
            "exchange" -> {
//             viewModel.getExchangeRecord()
            }
            "getStamp" -> {
//                viewModel.getStampRecord()
            }
        }
    }

    private fun initView() {
        store_fragment_rv_event_record.layoutManager = LinearLayoutManager(context)
        store_fragment_rv_event_record.layoutAnimation= LayoutAnimationController(AnimationUtils.loadAnimation(context,R.anim.slide_from_right_to_left_in))
    }

    /**
     * 根据event的不同 为RecyclerView赋予不同的item和数据
     */
    private fun initAdapter() {


        when (arguments?.getString("event")) {
            "exchange" -> {
                //观察 在观察中配置adapter
                //若adapter未设置 则进行设置
                if (store_fragment_rv_event_record.adapter == null) {
                    mEventRVAdapter = BaseSimplifyRecyclerAdapter2(arrayListOf("1", "2", "3"), arrayListOf(1, 2))
                            .onBindView(BaseSimplifyRecyclerAdapter2.BindingCallBack<StoreRecyclerItemExchangeRecordBinding>(
                                    R.layout.store_recycler_item_exchange_record,
                                    isItemPosition = { position -> position in 0 until 5 },
                                    onBindView = { binding, holder, position ->
                                        Log.d("zzzz", "(EventRecordFragment.kt:70)-->> onBindView")
                                        binding.eventHandle = EventHandle()
                                        //设置待领取提示的出场动画
                                        val anim = AlphaAnimation(0f, 1f)
                                        anim.duration = 1000
                                        binding.storeBtnProductReceiveTips.startAnimation(anim)
                                    }
                            ))
                }

            }
            "getStamp" -> {
                //若adapter未设置 则进行设置
                if (store_fragment_rv_event_record.adapter == null) {
                    mEventRVAdapter = BaseSimplifyRecyclerAdapter2(arrayListOf("1", "2", "3"), arrayListOf(1, 2))
                            .onBindView(BaseSimplifyRecyclerAdapter2.BindingCallBack<StoreRecyclerItemStampGetRecordBinding>(
                                    R.layout.store_recycler_item_stamp_get_record,
                                    isItemPosition = { position -> position in 0 until 5 },
                                    onBindView = { binding, holder, position -> Log.d("zzzz", "(EventRecordFragment.kt:70)-->> onBindView") }
                            ))
                }

            }
        }
        store_fragment_rv_event_record.adapter = mEventRVAdapter

    }

    /**
     * 事件处理内部类
     * 通过binding绑定到xml中
     */
    inner class EventHandle() {
        //处理单击事件
        fun onItemSingleClick(view: View) {

            when (view.id) {
                R.id.store_layout_exchange_record -> {
                    context?.doIfLogin { ARouter.getInstance().build(STORE_EXCHANGE_DETAIL).navigation() }
                }
            }
        }
    }
}