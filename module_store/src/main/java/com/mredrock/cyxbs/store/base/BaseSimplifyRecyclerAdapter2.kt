package com.mredrock.cyxbs.store.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

/**
 *    author : zz
 *    e-mail : 1140143252@qq.com
 *    date   : 2021/7/16
 *
 * 可复用的RecyclerAdapter
 * 支持常规的RecyclerAdapter以及含DataBinding的RecyclerAdapter
 *
 * 使用方法:
 * 1.创建ReusableRecyclerAdapter<Any>对象 传入集合(有多少个item就传入多少个与item对应的数据集合)
 * 2.根据需求是否需要Binding来调用含Binding/常规的onBindView方法
 * 方法中传入相应数据 包含两个函数式调用方法
 * 确定item所在position-->isItemPosition: (position: Int) -> Boolean
 * 需要在onBindViewHolder中回调的方法-->onBindView
 * 若集合size发生变化，务必调用refresh()方法 更新ItemCount
 */
@Deprecated("现已废弃, 请使用 SimpleRVAdapter 代替")
open class BaseSimplifyRecyclerAdapter2<T>(private vararg var dataList: List<T>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemCount = 0//总item数
    private var itemType = 0//item类型 为每个item的layoutId
    private var bindingCallBackList = arrayListOf<Callback>()//含Binding的CallBack
    private var commonCallBackList = arrayListOf<Callback>()//含常规的CallBack
    private var isRefresh = false//是否刷新itemCount数量

    /**
     * 含Binding的回调方法
     */
    fun <DB : ViewDataBinding> onBindView(
        bindingCallBack: BindingCallBack<DB>
    ): BaseSimplifyRecyclerAdapter2<T> {
        bindingCallBackList.add(bindingCallBack)
        return this
    }

    /**
     * 常规的回调方法
     * 注:需传入一个继承自RecyclerView.ViewHolder的类 XXXViewHolder::class.java
     */
    fun <VH : RecyclerView.ViewHolder> onBindView(
        commonCallBack: CommonCallBack<VH>
    ): BaseSimplifyRecyclerAdapter2<T> {
        commonCallBackList.add(commonCallBack)
        return this
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //根据两种情况中viewType的正负不同 分别返回对应的ViewHolder
        if (viewType > 0) {
            val itemBinding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                viewType,
                parent,
                false
            )
            return BindingVH(itemBinding)
        } else {
            lateinit var viewHolder: RecyclerView.ViewHolder
            for (i in 0 until commonCallBackList.size) {
                if (commonCallBackList[i].layoutId == -viewType) {
                    val rootView = LayoutInflater.from(parent.context)
                        .inflate(commonCallBackList[i].layoutId, parent, false)
                    viewHolder = (commonCallBackList[i] as CommonCallBack<*>).getClass(rootView)
                }
            }
            return viewHolder
        }
    }

    /**
     * 回调onBindView
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /**
         * 分别遍历两个不同CallBackList
         * 通过判断position是否符合来回调对应的onBindView()
         */
        for (i in 0 until bindingCallBackList.size) {
            if (bindingCallBackList[i].isItemPosition.invoke(position)) {
                bindingCallBackList[i].invoke(holder, position, (holder as BindingVH).binding)
            }
        }
        for (i in 0 until commonCallBackList.size) {
            if (commonCallBackList[i].isItemPosition.invoke(position)) {
                commonCallBackList[i].invoke(holder, position, null)
            }
        }

    }

    /**
     * 当集合 size 改变时必须调用该方法刷新 itemCount
     */
    fun refresh() {
        isRefresh = true
        notifyDataSetChanged()
    }

    /**
     * 计算传入集合的 size 总数为ItemCount
     */
    override fun getItemCount(): Int {

        //初始化ItemCount
        if (itemCount == 0) {
            dataList.forEach {
                itemCount += it.size
            }
        }

        //刷新ItemCount
        if (isRefresh) {
            itemCount = 0
            dataList.forEach {
                itemCount += it.size
            }
            isRefresh = false
        }
        return itemCount
    }

    /**
     * 为不同item设置ViewType
     */
    override fun getItemViewType(position: Int): Int {
        //设置含binding的viewType为其layoutId的绝对值
        for (i in 0 until bindingCallBackList.size) {
            if (bindingCallBackList[i].isItemPosition(position)) {
                itemType = abs(bindingCallBackList[i].layoutId)
            }
        }
        //设置不含binding的viewType为其layoutId绝对值的负值
        for (i in 0 until commonCallBackList.size) {
            if (commonCallBackList[i].isItemPosition(position)) {
                itemType = -commonCallBackList[i].layoutId
            }
        }
        return itemType
    }

    /**
     * 含Binding时默认的ViewHolder
     */
    class BindingVH(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    /**
     * 抽象类Callback及其抽象方法invoke
     * 用于解决得不到泛型的问题
     */
    abstract class Callback(
        val layoutId: Int,
        val isItemPosition: (position: Int) -> Boolean
    ) {
        abstract fun invoke(
            holder: RecyclerView.ViewHolder,
            position: Int,
            binding: ViewDataBinding?
        )
    }

    /**
     * 含Binding的CallBack
     * @param layoutId XML文件id
     * @param isItemPosition  用于确认 item 需显示的位置
     * @param onBindView onBindViewHolder() 中的回调方法 用于绑定数据
     */
    open class BindingCallBack<DB : ViewDataBinding>(
        layoutId: Int,
        isItemPosition: (position: Int) -> Boolean,
        private val onBindView: (binding: DB, holder: BindingVH, position: Int) -> Unit
    ) : Callback(layoutId, isItemPosition) {
        override fun invoke(
            holder: RecyclerView.ViewHolder,
            position: Int,
            binding: ViewDataBinding?
        ) {
            onBindView.invoke(binding as DB, holder as BindingVH, position)
        }
    }

    /**
     * 常规的CallBack
     * @param layoutId XML文件id
     * @param viewHolderClass  ViewHolder 的 Class 对象  ViewHolder 的构造器为（View）
     * @param isItemPosition 用于确认 item 需显示的位置
     * @param onBindView onBindViewHolder() 中的回调方法 用于绑定数据
     */
    open class CommonCallBack<VH : RecyclerView.ViewHolder>(
        layoutId: Int,
        val viewHolderClass: Class<VH>,
        isItemPosition: (position: Int) -> Boolean,
        private val onBindView: ((holder: VH, position: Int) -> Unit)
    ) : Callback(layoutId, isItemPosition) {
        override fun invoke(
            holder: RecyclerView.ViewHolder,
            position: Int,
            binding: ViewDataBinding?
        ) {
            onBindView.invoke(holder as VH, position)
        }

        /**
         * 通过反射 得到传入ViewHolder类的的对象
         */
        fun getClass(itemView: View): VH {
            val constructor = viewHolderClass.getConstructor(View::class.java)
            return constructor.newInstance(itemView)
        }
    }
}