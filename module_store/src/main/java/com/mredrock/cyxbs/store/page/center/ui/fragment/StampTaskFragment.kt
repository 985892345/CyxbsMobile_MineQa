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

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/14
 */
class StampTaskFragment : BaseFragment() {

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView: RecyclerView = view.findViewById(R.id.store_rv_stamp_task)
        viewModel.stampCenterData.observe(viewLifecycleOwner, Observer {
            resetData(it.data.task)
            if (recyclerView.adapter == null) {
                setAdapter(recyclerView)
            }else {
                refreshAdapter(it.data.task)
            }
        })
    }

    private val mAdapter = SimpleRVAdapter()
    private lateinit var mStampTaskTitleItem: StampTaskTitleItem
    private lateinit var mStampTaskListItem: StampTaskListItem
    private fun setAdapter(recyclerView: RecyclerView) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    context,
                    R.anim.store_slide_from_left_to_right_in
                )
            )
        mStampTaskTitleItem = StampTaskTitleItem(titleMap)
        mStampTaskListItem = StampTaskListItem(taskMap)
        recyclerView.adapter = mAdapter
            .addItem(mStampTaskTitleItem)
            .addItem(mStampTaskListItem)
            .show()
    }

    private fun refreshAdapter(tasks: List<StampCenter.Task>) {
        mStampTaskTitleItem.resetData(titleMap)
        mStampTaskListItem.resetData(taskMap)
        mAdapter.refreshAuto(
            true,
            tasks.size + 2,
            true,
            isItemTheSame = { oldItemPosition, newItemPosition ->
                val old = oldAllMap[oldItemPosition]
                if (old is StampCenter.Task) {
                    taskMap.containsKey(newItemPosition)
                }else {
                    titleMap.containsKey(newItemPosition)
                }
            },
            isContentsTheSame = { oldItemPosition, newItemPosition ->
                val old = oldAllMap[oldItemPosition]
                if (old is StampCenter.Task) {
                    val task = taskMap[newItemPosition]
                    if (task != null) {
                        task == old
                    }else false
                }else {
                    true
                }
            }
        )
    }

    private val kinds = ArrayList<ArrayList<StampCenter.Task>>(2)
    private val titleMap = HashMap<Int, String>()
    private val taskMap = HashMap<Int, StampCenter.Task>()
    private val oldAllMap = HashMap<Int, Any>()
    private fun resetData(tasks: List<StampCenter.Task>) {
        oldAllMap.clear()
        oldAllMap.putAll(titleMap)
        oldAllMap.putAll(taskMap)
        kinds.clear()
        titleMap.clear()
        taskMap.clear()
        kinds.add(ArrayList())
        kinds.add(ArrayList())
        for (task in tasks) {
            if (task.type == "base") {
                kinds[0].add(task)
            }else {
                kinds[1].add(task)
            }
        }
        titleMap[kinds[0].size] = "更多任务"
        for (i in 0 until kinds[0].size) {
            taskMap[i] = kinds[0][i]
        }
        for (i in 0 until kinds[1].size) {
            taskMap[kinds[0].size + 1 + i] = kinds[1][i]
        }
    }
}