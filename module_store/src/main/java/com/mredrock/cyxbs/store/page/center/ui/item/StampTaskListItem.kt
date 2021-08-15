package com.mredrock.cyxbs.store.page.center.ui.item

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampCenter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampTaskListBinding

/**
 * 自己写了个用于解耦不同的 item 的 Adapter 的封装类, 详情请看 [SimpleRVAdapter]
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskListItem(
    private var taskMap: Map<Int, StampCenter.Task>
) : SimpleRVAdapter.DBItem<StoreRecyclerItemStampTaskListBinding>(
    R.layout.store_recycler_item_stamp_task_list
) {

    fun resetData(taskMap: Map<Int, StampCenter.Task>) {
        this.taskMap = taskMap
    }

    override fun getItemCount(): Int {
        return taskMap.size
    }

    override fun isInHere(position: Int): Boolean {
        return taskMap.containsKey(position)
    }

    override fun create(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        binding.storeBtnStampTaskListGo.setOnClickListener {
            // 点击事件的跳转
        }
    }

    @SuppressLint("SetTextI18n")
    override fun refactor(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
        val task = taskMap[position]
        if (task != null) {
            binding.storeProgressBarStampTask.max = task.maxProgress
            binding.storeProgressBarStampTask.post {
                binding.storeProgressBarStampTask.setProgressCompat(task.currentProgress, true)
            }
            binding.storeTvStampTaskListProgress.text = "${task.currentProgress}/${task.maxProgress}"
            binding.storeTvStampTaskListName.text = task.title
            binding.storeTvStampTaskListDescribe.text = task.description
            binding.storeTvStampTaskListGainNumber.text = "+${task.gainStamp}"
            if (task.currentProgress != task.maxProgress) {
                binding.storeBtnStampTaskListGo.text = "去签到"
                binding.storeBtnStampTaskListGo.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.store_stamp_task_go_btn_bg)
                )
            }else {
                binding.storeBtnStampTaskListGo.text = "已完成"
                binding.storeBtnStampTaskListGo.setBackgroundColor(
                    ContextCompat.getColor(context, R.color.store_stamp_task_go_btn_bg_ok)
                )
            }
        }
    }
}