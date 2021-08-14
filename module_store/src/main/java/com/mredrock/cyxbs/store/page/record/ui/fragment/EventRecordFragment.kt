package com.mredrock.cyxbs.store.page.record.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import com.mredrock.cyxbs.common.ui.BaseViewModelFragment
import com.mredrock.cyxbs.common.utils.extensions.gone
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampGetRecord
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemExchangeRecordBinding
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampGetRecordBinding
import com.mredrock.cyxbs.store.page.record.ui.activity.ExchangeDetailActivity
import com.mredrock.cyxbs.store.page.record.viewmodel.EventRecordViewModel
import com.mredrock.cyxbs.store.utils.Date
import kotlinx.android.synthetic.main.store_fragment_event_record.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/2 15:10
 */

class EventRecordFragment : BaseViewModelFragment<EventRecordViewModel>() {
    private lateinit var mEventRVAdapter: SimpleRVAdapter
    private val anim = AlphaAnimation(0f, 1f) //待领取的提示显示动画

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
                viewModel.getExchangeRecord()
            }
            "getStamp" -> {
                viewModel.getStampRecord()
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
                viewModel.mExchangeRecord.observeNotNull {
                    //若adapter未设置 则进行设置
                    if (store_fragment_rv_event_record.adapter == null) {
                        mEventRVAdapter = SimpleRVAdapter(it.size)
                                .addItem(
                                        layoutId = R.layout.store_recycler_item_exchange_record,
                                        isInHere = { true },
                                        create = { binding: StoreRecyclerItemExchangeRecordBinding, holder: SimpleRVAdapter.BindingVH ->
                                            //设置点击事件
                                            binding.storeLayoutExchangeRecord.onClick { _ ->
                                                val intent = Intent(activity, ExchangeDetailActivity::class.java)
                                                intent.putExtra("data", it[holder.layoutPosition])
                                                activity?.startActivity(intent)
                                            }
                                            //设置待领取提示的出场动画
                                            anim.duration = 600
                                        },
                                        refactor = { binding: StoreRecyclerItemExchangeRecordBinding, holder: SimpleRVAdapter.BindingVH, position: Int ->
                                            //绑定数据
                                            binding.data = it[position]
                                            //单独处理时间
                                            binding.storeItemExchangeRecordTvDate.text = Date.getTime(it[position].date)
                                            //如果已领取就gone 否则启动动画
                                            if (it[position].isReceived) {
                                                binding.storeBtnProductReceiveTips.gone()
                                            } else {
                                                binding.storeBtnProductReceiveTips.startAnimation(anim)
                                            }
                                        }
                                )
                        store_fragment_rv_event_record.adapter = mEventRVAdapter
                    }
                }
            }
            "getStamp" -> {
                viewModel.mStampGetRecord.observeNotNull {
                    //若adapter未设置 则进行设置
                    if (store_fragment_rv_event_record.adapter == null) {
                        mEventRVAdapter = SimpleRVAdapter(it.size)
                                .addItem(
                                        layoutId = R.layout.store_recycler_item_stamp_get_record,
                                        isInHere = { true },
                                        create = { binding: StoreRecyclerItemStampGetRecordBinding, holder: SimpleRVAdapter.BindingVH ->

                                        },
                                        refactor = { binding: StoreRecyclerItemStampGetRecordBinding, holder: SimpleRVAdapter.BindingVH, position: Int ->
                                            binding.data = it[position] as StampGetRecord.Data
                                            //单独处理时间
                                            binding.storeItemGetRecordTvDate.text = Date.getTime(it[position].date)
                                        }
                                )
                        store_fragment_rv_event_record.adapter = mEventRVAdapter
                    }
                }


            }
        }
    }
}