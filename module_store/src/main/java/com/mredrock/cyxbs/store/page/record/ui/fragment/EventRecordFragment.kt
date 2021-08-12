package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.page.record.ui.item.ExchangeRecordItem
import com.mredrock.cyxbs.store.page.record.ui.item.StampGetRecordItem
import com.mredrock.cyxbs.store.page.record.viewmodel.EventRecordViewModel
import kotlinx.android.synthetic.main.store_fragment_event_record.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */

class EventRecordFragment : BaseViewModelFragment<EventRecordViewModel>() {
    private lateinit var mEventRVAdapter: SimpleRVAdapter

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
//             viewModel.getStampRecord()
            }
        }
    }

    private fun initView() {
        store_fragment_rv_event_record.layoutManager = LinearLayoutManager(context)
        store_fragment_rv_event_record.layoutAnimation = LayoutAnimationController(AnimationUtils.loadAnimation(context, R.anim.store_slide_from_right_to_left_in))
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
                    mEventRVAdapter = SimpleRVAdapter(5)
                            .addItem(ExchangeRecordItem())
                }

            }
            "getStamp" -> {
                //若adapter未设置 则进行设置
                if (store_fragment_rv_event_record.adapter == null) {
                    mEventRVAdapter = SimpleRVAdapter(5)
                            .addItem(StampGetRecordItem())
                }

            }
        }

        store_fragment_rv_event_record.adapter = mEventRVAdapter

    }
}