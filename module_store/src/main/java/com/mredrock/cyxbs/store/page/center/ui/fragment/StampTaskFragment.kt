package com.mredrock.cyxbs.store.page.center.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.common.ui.BaseFragment
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskListItem
import com.mredrock.cyxbs.store.page.center.ui.item.StampTaskTitleItem
import com.mredrock.cyxbs.store.page.center.viewmodel.StoreCenterViewModel
import com.mredrock.cyxbs.store.utils.Type

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/14
 */
class StampTaskFragment : BaseFragment() {

    // 因为我只需要 Activity 的 ViewModel, 所以没有继承于 BaseViewModelFragment
    private val viewModel by lazy {
        ViewModelProvider(requireActivity()).get(StoreCenterViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.store_item_stamp_task, container, false)
    }

    // 建立 ViewModel 的数据观察
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.store_rv_stamp_task)
        viewModel.stampCenterData.observe(viewLifecycleOwner, Observer {
            resetData(it.task) // 重新设置数据
            if (recyclerView.adapter == null) {
                setAdapter(recyclerView) // 第一次得到数据时设置 adapter
            }else {
                refreshAdapter(it.task) // 再次得到数据时刷新
            }
        })
    }

    private val mAdapter = SimpleRVAdapter() // 自己写的解耦 Adapter 的封装类, 可用于解耦不同的 item
    private lateinit var mStampTaskTitleItem: StampTaskTitleItem
    private lateinit var mStampTaskListItem: StampTaskListItem
    private fun setAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        mStampTaskTitleItem = StampTaskTitleItem(titleMap)
        mStampTaskListItem = StampTaskListItem(taskMap)
        recyclerView.adapter = mAdapter
            .addItem(mStampTaskTitleItem)
            .addItem(mStampTaskListItem)
            .show()
        recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.store_slide_from_left_to_right_in
                )
            )
    }

    // 用于再次得到数据后的刷新 adapter
    private fun refreshAdapter(tasks: List<StampCenter.Task>) {
        mStampTaskTitleItem.resetData(titleMap)
        mStampTaskListItem.resetData(taskMap)
        // 不可自己调用 notifyDataSetChanged(), 因为你的 itemCount 将无法改变
        mAdapter.refreshYYDS()
    }

    private val baseList = ArrayList<StampCenter.Task>()
    private val moreList = ArrayList<StampCenter.Task>()
    private val titleMap = HashMap<Int, String>() // adapter 的 position 与标题的映射
    private val taskMap = HashMap<Int, StampCenter.Task>() // adapter 的 position 与任务的映射
    private fun resetData(tasks: List<StampCenter.Task>) {
        titleMap.clear()
        taskMap.clear()
        baseList.clear()
        moreList.clear()
        for (task in tasks) { // 后端返回的 type = "base" 时为每日任务, type = "more" 时为更多任务
            when (task.type) {
                Type.Task.Base -> baseList.add(task)
                Type.Task.More -> moreList.add(task)
            }
        }
        titleMap[baseList.size] = "更多任务"
        for (i in baseList.indices) {
            taskMap[i] = baseList[i]
        }
        for (i in moreList.indices) {
            taskMap[baseList.size + 1 + i] = moreList[i]
        }
    }
}