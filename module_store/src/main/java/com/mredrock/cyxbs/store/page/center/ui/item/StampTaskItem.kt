package com.mredrock.cyxbs.store.page.center.ui.item

import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.bean.StampCenter

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskItem: SimpleRVAdapter.VHItem<StampTaskItem.StampShopTitleVH>(
    R.layout.store_item_stamp_task
) {

    private lateinit var mStampTaskTitleItem: StampTaskTitleItem
    private lateinit var mStampTaskListItem: StampTaskListItem
    private var mAdapter = SimpleRVAdapter()
    fun refreshData(tasks: List<StampCenter.Task>) {
        resetData(tasks)
        if (mAdapter.itemCount == 0) {
            mStampTaskTitleItem = StampTaskTitleItem(titleMap)
            mStampTaskListItem = StampTaskListItem(taskMap)
            mAdapter = SimpleRVAdapter()
                .addItem(mStampTaskTitleItem)
                .addItem(mStampTaskListItem)
                .show()
            return
        }
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

    class StampShopTitleVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerView: RecyclerView = itemView.findViewById(R.id.store_rv_stamp_task)
    }

    override fun isInHere(position: Int): Boolean {
        return position == 1
    }

    override fun getNewViewHolder(itemView: View): StampShopTitleVH {
        return StampShopTitleVH(itemView)
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun create(holder: StampShopTitleVH) {
        holder.recyclerView.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.recyclerView.layoutAnimation =
            LayoutAnimationController(
                AnimationUtils.loadAnimation(
                    holder.recyclerView.context,
                    R.anim.store_slide_from_left_to_right_in
                )
            )
        holder.recyclerView.adapter = mAdapter
    }

    override fun refactor(holder: StampShopTitleVH, position: Int) {
    }

    override fun refresh(holder: StampShopTitleVH, position: Int) {
    }
}