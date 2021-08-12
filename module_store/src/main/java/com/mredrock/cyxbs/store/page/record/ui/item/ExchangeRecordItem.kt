package com.mredrock.cyxbs.store.page.record.ui.item


import android.content.Intent
import android.view.animation.AlphaAnimation
import com.mredrock.cyxbs.common.BaseApp.Companion.context
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.store.R
import com.mredrock.cyxbs.store.base.SimpleRVAdapter
import com.mredrock.cyxbs.store.databinding.StoreRecyclerItemExchangeRecordBinding
import com.mredrock.cyxbs.store.page.record.ui.activity.ExchangeDetailActivity

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/10 10:28
 */
class ExchangeRecordItem : SimpleRVAdapter.DBItem<StoreRecyclerItemExchangeRecordBinding>(
    R.layout.store_recycler_item_exchange_record
) {
    private val anim = AlphaAnimation(0f, 1f)

    override fun isInHere(position: Int): Boolean {
        return true //即全部
    }

    override fun create(
        binding: StoreRecyclerItemExchangeRecordBinding,
        holder: SimpleRVAdapter.BindingVH
    ) {
        //设置点击事件
        binding.storeLayoutExchangeRecord.onClick {
            val intent = Intent(context, ExchangeDetailActivity::class.java)
            context.startActivity(intent)
        }
        //设置待领取提示的出场动画
        anim.duration = 600
    }

    override fun refactor(
        binding: StoreRecyclerItemExchangeRecordBinding,
        holder: SimpleRVAdapter.BindingVH,
        position: Int
    ) {
        //启动动画
        binding.storeBtnProductReceiveTips.startAnimation(anim)
    }
}