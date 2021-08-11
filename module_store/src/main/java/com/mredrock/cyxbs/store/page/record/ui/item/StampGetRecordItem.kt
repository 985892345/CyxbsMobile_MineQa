package com.mredrock.cyxbs.store.page.record.ui.item

import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemStampGetRecordBinding

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/10 11:24
 */
class StampGetRecordItem : SimpleRVAdapter.DBItem<StoreRecyclerItemStampGetRecordBinding>(
        R.layout.store_recycler_item_stamp_get_record
) {
    override fun isInHere(position: Int): Boolean {
        return true//全部
    }

    override fun create(binding: StoreRecyclerItemStampGetRecordBinding, holder: SimpleRVAdapter.BindingVH) {

    }

    override fun refactor(binding: StoreRecyclerItemStampGetRecordBinding, holder: SimpleRVAdapter.BindingVH, position: Int) {

    }

    override fun refresh(binding: StoreRecyclerItemStampGetRecordBinding, holder: SimpleRVAdapter.BindingVH, position: Int) {
        super.refresh(binding, holder, position)
    }
}