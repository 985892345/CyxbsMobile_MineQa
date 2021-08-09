package com.mredrock.cyxbs.store.page.center.ui.item

import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampTaskListBinding

/**
 * ...
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/8/9
 */
class StampTaskListItem : SimpleRVAdapter.DBItem<StoreRecyclerItemStampTaskListBinding>(
    R.layout.store_recycler_item_stamp_task_list
) {
    override fun isInHere(position: Int): Boolean {
        return position != 4
    }

    override fun create(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        val progressBar = binding.storeProgressBarStampTask
        progressBar.post {
            progressBar.setProgressCompat(progressBar.max, true)
        }
    }

    override fun refactor(
        binding: StoreRecyclerItemStampTaskListBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
    }
}