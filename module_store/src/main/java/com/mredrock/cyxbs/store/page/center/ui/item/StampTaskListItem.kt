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
            binding.task = task
            binding.storeProgressBarStampTask.post { // 不加 post 就不显示进度条加载动画, 很奇怪
                binding.storeProgressBarStampTask.setProgressCompat(
                    task.currentProgress, task.currentProgress != 0
                )
            }
            if (task.currentProgress != task.maxProgress) {
                if (position == 0) {
                    binding.storeBtnStampTaskListGo.text = "去签到"
                }else {
                    binding.storeBtnStampTaskListGo.text = "去完成"
                }

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

    override fun onViewRecycled(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        super.onViewRecycled(binding, holder)
        // 当 item 被回收时就设置进度为 0, 防止因为刷新而出现 item 复用时闪进度的 bug
        binding.storeProgressBarStampTask.progress = 0
    }
}