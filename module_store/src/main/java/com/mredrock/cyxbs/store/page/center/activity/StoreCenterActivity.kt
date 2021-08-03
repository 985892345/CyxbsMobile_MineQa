package com.mredrock.cyxbs.store.page.center.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.example.module_store.R
import com.mredrock.cyxbs.common.config.STORE_CENTER
import com.mredrock.cyxbs.common.config.STORE_EXCHANGE

@Route(path = STORE_CENTER)
class StoreCenterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.store_activity_store_center)
    }
}