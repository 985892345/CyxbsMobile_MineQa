package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemExchangeRecordBinding
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampGetRecordBinding
import com.mredrock.cyxbs.store.page.record.ui.activity.ExchangeDetailActivity
import com.mredrock.cyxbs.store.page.record.viewmodel.EventRecordViewModel
import com.mredrock.cyxbs.store.utils.Date

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */
class EventRecordFragment : BaseViewModelFragment<EventRecordViewModel>() {

    // 这个表示了该 Fragment 显示 "兑换记录" 还是 "获取记录" 界面, 原因在与使用了 VP2 的复用机制
    private var mEventType: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.store_fragment_event_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mEventType = arguments?.getString("event")
        initData()
        initAdapter(view)
        super.onViewCreated(view, savedInstanceState)
    }

    /**
     * 根据event的不同 通过VM获取对应的数据
     */
    private fun initData() {
        when (mEventType) {
            "兑换记录" -> {
                viewModel.getExchangeRecord()
            }
            "获取记录" -> {
                viewModel.getStampRecord()
            }
        }
    }

    /**
     * 根据event的不同 为RecyclerView赋予不同的item和数据
     */
    private fun initAdapter(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.store_fragment_rv_event_record)
        recyclerView.layoutManager = LinearLayoutManager(context)
        when (mEventType) {
            "兑换记录" -> {
                viewModel.mExchangeRecord.observeNotNull {
                    //若adapter未设置 则进行设置
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = SimpleRVAdapter()
                            .addItem<StoreRecyclerItemExchangeRecordBinding>(
                                layoutId = R.layout.store_recycler_item_exchange_record,
                                getItemCount = { it.size },
                                isInHere = { true },
                                create = { binding, holder ->
                                    //设置点击事件
                                    binding.storeLayoutExchangeRecord.onClick { _ ->
                                        ExchangeDetailActivity.activityStart(requireContext(), it[holder.layoutPosition])
                                    }
                                },
                                refactor = { binding, _, position: Int ->
                                    //绑定数据
                                    binding.data = it[position]
                                    //单独处理时间
                                    binding.storeItemExchangeRecordTvDate.text = Date.getTime(it[position].date)

                                    //如果已领取就隐藏 否则启动动画
                                    if (it[position].isReceived) {
                                        binding.storeBtnProductReceiveTips.alpha = 0f
                                    } else {
                                        binding.storeBtnProductReceiveTips.animate()
                                            .alpha(1f)
                                            .setDuration(600)
                                            .start()
                                    }
                                },
                                onViewRecycled = { binding, _ ->
                                    // 当 item 被回收时调用, 取消动画效果, 防止因复用而出现闪动
                                    binding.storeBtnProductReceiveTips.animate().cancel()
                                    binding.storeBtnProductReceiveTips.alpha = 0f
                                }
                            ).show()
                    }
                }
            }
            "获取记录" -> {
                viewModel.mStampGetRecord.observeNotNull {
                    //若adapter未设置 则进行设置
                    if (recyclerView.adapter == null) {
                        recyclerView.adapter = SimpleRVAdapter()
                            .addItem<StoreRecyclerItemStampGetRecordBinding>(
                                layoutId = R.layout.store_recycler_item_stamp_get_record,
                                getItemCount = { it.size },
                                isInHere = { true },
                                create = { binding, holder -> },
                                refactor = { binding, _, position ->
                                    binding.data = it[position]
                                    //单独处理时间
                                    binding.storeItemGetRecordTvDate.text = Date.getTime(it[position].date)
                                }
                            ).show()
                    }
                }
            }
        }
    }
}