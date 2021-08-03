package com.mredrock.cyxbs.store.page.stamp.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.module_store.R
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.store.page.stamp.viewmodel.EventRecordViewModel
import com.mredrock.cyxbs.store.until.ui.ReusableRecyclerAdapter

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */

class EventRecordFragment : BaseViewModelFragment<EventRecordViewModel>() {
    private lateinit var eventRVAdapter: ReusableRecyclerAdapter<Any>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.store_fragment_event_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initData()
        initAdapter()
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 根据event的不同 通过VM获取对应的数据
     */
    private fun initData() {
        when (arguments?.getString("event")) {
            "exchange" -> {

            }
            "getStamp" -> {

            }
        }
    }

    private fun initView() {

    }

    /**
     * 根据event的不同 为RecyclerView赋予不同的item和数据
     */
    private fun initAdapter() {
        when (arguments?.getString("event")) {
            "exchange" -> {

            }
            "getStamp" -> {

            }
        }
    }
}