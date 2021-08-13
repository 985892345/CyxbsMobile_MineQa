package com.mredrock.cyxbs.store.ui.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.LayoutRes
import androidx.fragment.app.DialogFragment
import com.mredrock.cyxbs.common.utils.extensions.onClick
import com.mredrock.cyxbs.store.R
import kotlinx.android.synthetic.main.store_dialog_exchange_product.*

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/8/1 13:13
 */
class ProductExchangeDialogFragment : DialogFragment() {
    @LayoutRes
    private var dialogRes: Int = 0
    private var positiveString: String = "确定"
    private var negativeString: String = "取消"
    private var exchangeTips: String = ""
    private var onPositiveClick: (() -> Unit)? = null
    private var onNegativeClick: (() -> Unit)? = null

    fun initView(
            @LayoutRes
            dialogRes: Int = 0,
            positiveString: String = "确定",
            negativeString: String = "取消",
            exchangeTips: String = "",
            onPositiveClick: (() -> Unit)? = null,
            onNegativeClick: (() -> Unit)? = null
    ) {
        this.dialogRes = dialogRes
        this.positiveString = positiveString
        this.negativeString = negativeString
        this.exchangeTips = exchangeTips
        this.onPositiveClick = onPositiveClick
        this.onNegativeClick = onNegativeClick
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val v = inflater.inflate(dialogRes, dialog?.window?.findViewById(android.R.id.content)
                ?: container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val dm = DisplayMetrics()
        activity?.windowManager?.defaultDisplay?.getMetrics(dm)
        dialog?.window?.setLayout((dm.widthPixels * 0.75).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //这里判断是否为需要两个选项的Dialog
        when (dialogRes) {
            R.layout.store_dialog_exchange_product -> {
                store_dialog_btn_negative.apply {
                    text = negativeString
                    onClick {
                        onNegativeClick?.invoke()
                    }
                }
            }
        }

        store_dialog_btn_positive.apply {
            text = positiveString
            onClick {
                onPositiveClick?.invoke()
            }
        }
        store_tv_dialog_exchange_content.text = exchangeTips
    }
}