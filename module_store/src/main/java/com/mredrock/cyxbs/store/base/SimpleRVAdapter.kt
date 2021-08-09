package com.mredrock.cyxbs.store.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import java.lang.RuntimeException

/**
 * 实现像数组一样的直接添加不同的 item, 使用方法可以点击查看不同的实例
 *
 * 本 Adapter 的实现涉及到了: 泛型擦除的解决(如何绑定不同的 DataBinding 或 ViewHolder)、
 * 官方推荐的 DiffUtil 刷新帮助类、带有三个参数的 onBindViewHolder 的使用
 *
 * ***在使用前, 请先看懂下面的例子***
 *
 * 此时我有这样的一个需求: 我有多个 Animal 的具体实现类(比如有: Dog, Cat, Bird)
 * 需要保存, 但是, 我不想分开它们, 我想保存在一起, 并且保存后在拿出来时我还必须知道
 * 它是什么具体的类型, 但因为泛型擦除, 我拿出来是时只知道它是 Animal, 却无法得到它究竟
 * 是什么动物(总结: 我需要给你什么类型的动物, 你在回调时就返回什么类型的动物, 而不是 Animal)
 *
 * 而对应的 DataBinding 就是这样, 我的一个 RecyclerView 想要写多个不同的 Item,
 * 有多个不同的 DataBinding, 如果都用他们的父类 ViewDataBinding 来保存确实省事,
 * 但在回调给调用者的时候, 总不能给 ViewDataBinding, 如果给了 ViewDataBinding,
 * 那么在我具体 DataBinding 里的 View 该怎么拿到呢?
 *
 * 可能你会想到为什么不让调用者在收到回调的时候强转?
 *
 * 强转确实是一个解决方法, 但这却是一个不好的解决方法, 会给调用者增加负担
 *
 * 在到处翻阅资料后, 的确找不到任何关于 java 解决泛型擦除的解决方案, 最后在苦思冥想中
 * 突然灵感闪现, 一个对象不是能保存一中具体的类型吗? 那如果我把 Animal 进行拆分, 每次
 * 在保存 Anima 具体实现类时, 专门生成一个对象来保存这个具体的 Anima 实现类. 比如:
 * 我保存 Dog 类, 那么在保存时生成一个以 Dog 为具体类型的对象来保存, 保存 Cat 时也是
 * 生成一个以 Cat 为具体类型的对象来保存, 就相当于保存他们的具体类型, 而在拿出来使用时
 * 就利用多态的性质, 调用的方法通过生成的具体的对象来回调, 这样有部分变量就可以通过具体的对象
 * 来进行强转而回调给调用者
 *
 * 就相当于你给一个类(A)传入了(或者你调用 A 的一个带有泛型的方法生成)一个带有泛型的类(B),
 * 在请求 A 返回具体泛型时写一个接口给 A, 然后 A 把这个接口给 B, B 拿到后进行强转, 通过
 * 这个接口把具体的东西返回给了调用者(最后真正的回调者是 B)
 *
 * 最后, 写一下该解决方案的缺点:
 *
 * 1、如果具体的类型过多, 则会生成过多的对象浪费空间
 *
 * 2、该解决方案并不是真正的解决了泛型擦除问题, 只是绕了一个弯来解决了泛型擦除
 *
 * 3、该方案只能用于回调中, 如果你想要实现数组中的 getter 方法, 则每次 get 都需要回调才能拿到
 *
 * 4、在具体类型很多时, 更推荐使用强转
 *
 * @author 985892345 (Guo Xiangrui)
 * @email 2767465918@qq.com
 * @data 2021/5/31 (在开发邮票商城项目前在自己的项目中开发的, 后续进行了许多优化和修改)
 * 开发邮票商城项目时间: 2021-8
 */
class SimpleRVAdapter(
    private var itemCount: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * 点击传入的类查看注解
     */
    fun <DB: ViewDataBinding> addItem(
        dataBindingItem: DBItem<DB>
    ): SimpleRVAdapter {
        val call = BindingCallBack(dataBindingItem)
        // 一个 for 循环用于遍历全部 item 数量调用 isInHere 回调，添加进 mPositionsWithCallback 数组
        // 向上转型存进数组(能够解决泛型擦除的主要原因并不在这里,主要原因在于接口回调的强转写法)
        mLayoutIdWithCallback[call.item.layoutId] = call
        return this
    }

    /**
     * 点击传入的类查看注解
     */
    fun <VH: RecyclerView.ViewHolder> addItem(
        viewHolderItem: VHItem<VH>
    ): SimpleRVAdapter {
        val call = ViewHolderCallBack(viewHolderItem)
        // 一个 for 循环用于遍历全部 item 数量调用 isInHere 回调，添加进 mPositionsWithCallback 数组
        // 向上转型存进数组(能够解决泛型擦除的主要原因并不在这里,主要原因在于接口回调的强转写法)
        mLayoutIdWithCallback[call.item.layoutId] = call
        return this
    }

    /**
     * 增加 DataBinding 的 item, 使用 Lambda 创建
     *
     * **WARNING:** 请不要在 [refactor] 中创建对象，
     * 比如：设置点击监听、设置用于 item 整个生命周期的对象等需要创建对象的做法，
     * 写在 [refactor] 是错误的，因为 [refactor] 是指你的 item 离开屏幕再回到屏幕后的回调，
     * 对于相同的 item 来说，设置的监听都是一样的，无需重复创建 Listener 对象重新设置监听。
     * 正确的做法是写在 [create] 中。
     * ***->> [refactor] 只适合用于修改数据，如：修改文字、图片等 <<-***
     *
     * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
     *
     * @param layoutId XML文件id
     * @param isInHere 会根据你的返回值判断是否是该 item 显示的位置
     * @param create 建议在此处进行一些只需进行一次的操作，如：设置点击监听、设置用于 item 整个生命周期的对象
     * @param refactor 用于设置当前 item 每次进入屏幕显示的数据。
     * 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
     * **->> 请不要在此处创建新的对象 <<-**，
     * 比如：设置点击监听、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法，
     * ***->> 这些应写在 [create] 中 <<-***
     *
     * @param refresh 用于刷新当前 item，可用于：修改文字、图片等，但它的修改周期只会在屏幕内，离开后可能就会还原。
     * 因为离开后再回来就只会回调 [refactor]，解决办法是数据修改后就更改一个全局数组，在 [refactor] 中直接取数组中的值
     */
    fun <DB: ViewDataBinding> addItem(
        @LayoutRes layoutId: Int,
        isInHere: (position: Int) -> Boolean,
        create: (binding: DB, holder: BindingVH) -> Unit,
        refactor: (binding: DB, holder: BindingVH, position: Int) -> Unit,
        refresh: ((binding: DB, holder: BindingVH, position: Int) -> Unit)? = null
    ): SimpleRVAdapter {
        val item = object : DBItem<DB>(layoutId) {
            override fun isInHere(position: Int): Boolean = isInHere(position)
            override fun create(binding: DB, holder: BindingVH) = create(binding, holder)
            override fun refactor(binding: DB, holder: BindingVH, position: Int) = refactor(binding, holder, position)
            override fun refresh(binding: DB, holder: BindingVH, position: Int) {
                refresh?.invoke(binding, holder, position)
            }
        }
        return addItem(item)
    }

    /**
     * 增加 ViewHolder 的 item, 使用 Lambda 创建
     *
     * **WARNING:** 请不要在 [refactor] 中创建对象，
     * 比如：设置点击监听、设置用于 item 整个生命周期的对象等需要创建对象的做法，
     * 写在 [refactor] 是错误的，因为 [refactor] 是指你的 item 离开屏幕再回到屏幕后的回调，
     * 对于相同的 item 来说，设置的监听都是一样的，无需重复创建 Listener 对象重新设置监听。
     * 正确的做法是写在 [create] 中。***->> [refactor] 只适合用于修改数据，如：修改文字、图片等 <<-***
     *
     * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
     *
     * @param layoutId XML文件id
     * @param getNewViewHolder 返回一个新的 ViewHolder，**请不要返回相同的对象**
     * @param isInHere 会根据你的返回值判断是否是该 item 显示的位置
     * @param create 建议在此处进行一些只需进行一次的操作，如：设置点击监听、设置用于 item 整个生命周期的对象
     * @param refactor 用于设置当前 item 每次进入屏幕显示的数据。
     * 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
     * **->> 请不要在此处创建新的对象 <<-**，
     * 比如：设置点击监听、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法，
     * ***->> 这些应写在 [create] 中 <<-***
     *
     * @param refresh 用于刷新当前 item，可用于：修改文字、图片等，但它的修改周期只会在屏幕内，离开后可能就会还原。
     * 因为离开后再回来就只会回调 [refactor]，解决办法是数据修改后就更改一个全局数组，在 [refactor] 中直接取数组中的值
     */
    fun <VH: RecyclerView.ViewHolder> addItem(
        @LayoutRes layoutId: Int,
        isInHere: (position: Int) -> Boolean,
        getNewViewHolder: (itemView: View) -> VH,
        create: (holder: VH) -> Unit,
        refactor: (holder: VH, position: Int) -> Unit,
        refresh: ((holder: VH, position: Int) -> Unit)? = null
    ): SimpleRVAdapter {
        val item = object : VHItem<VH>(layoutId) {
            override fun isInHere(position: Int): Boolean = isInHere(position)
            override fun getNewViewHolder(itemView: View): VH = getNewViewHolder(itemView)
            override fun create(holder: VH) = create(holder)
            override fun refactor(holder: VH, position: Int) = refactor(holder, position)
            override fun refresh(holder: VH, position: Int) { refresh?.invoke(holder, position) }
        }
        return addItem(item)
    }

    /**
     * 更高效的自动判断刷新方式的刷新 ***(调用该刷新后的回调必须实现 item 中的 refresh() 方法)***
     *
     * 本方法使用了谷歌官方的 DiffUtil 来自动判断刷新方式替代 notifyDataSetChanged() 刷新
     *
     * **WARNING:** 用前须知, **不能在 refactor() 中设置点击事件和回调**,
     * 原因在于: https://blog.csdn.net/weixin_28318011/article/details/112872952
     *
     * @param detectMoves 如果你有移动了位置的 item, 请传入 true (**传入 true 后会增大计算量**, 因此没有移动时传入 false)
     * @param isItemTheSame 比较两个 item 拥有的唯一 id 是否相同, 应比较不会改变的数据, 比如: item 要显示的名字、编号等
     * ***(请注意不要在 refactor() 中设置点击事件)***
     * @param isContentsTheSame 比较两个 item 拥有的其他可变内容是否相同, 比如: item 要显示的介绍文字、图片等
     */
    fun refreshAuto(
        newItemCount: Int,
        detectMoves: Boolean,
        isItemTheSame: (oldItemPosition: Int, newItemPosition: Int) -> Boolean,
        isContentsTheSame: (oldItemPosition: Int, newItemPosition: Int) -> Boolean
    ) {
        val diffResult = DiffUtil.calculateDiff(
            object : DiffUtil.Callback() {
                override fun getOldListSize(): Int = itemCount
                override fun getNewListSize(): Int = newItemCount
                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                    return isItemTheSame(oldItemPosition, newItemPosition)
                }
                override fun areContentsTheSame(
                    oldItemPosition: Int,
                    newItemPosition: Int
                ): Boolean {
                     return isContentsTheSame(oldItemPosition, newItemPosition)
                }
                /**
                 * 下面这个方法返回值只要不为 null 就可以使用我封装好的在 [DBItem] 和 [VHItem] 中的
                 * refresh() 方法. 如果想知道原理, 请查看带有三个参数的 onBindViewHolder
                 */
                override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any = ""
            }, detectMoves)
        diffResult.dispatchUpdatesTo(this)
        itemCount = newItemCount
    }

    /**
     * 单个 item 刷新,  ***(调用该刷新后的回调必须实现 Item 中的 refresh() 方法)***
     */
    fun refreshItem(position: Int) {
        // payload 传入不为 null 都可以
        notifyItemChanged(position, "")
    }

    private val mLayoutIdWithCallback = HashMap<Int, Callback>() // LayoutId 与 CallBack 的对应关系

    override fun onCreateViewHolder(parent: ViewGroup, layoutId: Int): RecyclerView.ViewHolder {
        val callBack = mLayoutIdWithCallback[layoutId]
        if (callBack != null) {
            val viewHolder = callBack.createNewViewHolder(parent)
            callBack.create(viewHolder) // 在这里用于设置你在 create 接口中的点击监听或其他只用设置一次的东西
            return viewHolder
        }
        throw RuntimeException("这是一个不可能会发生的报错, 原因在于你使用的 SimpleRvAdapter 的某个 layoutId 被修改")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 因为每个 position 对应的 CallBack 不同, 但 CallBack 的数量取决于你的 item 类型
        // 所以遍历不会花很多时间
        for (map in mLayoutIdWithCallback) {
            if (map.value.item.isInHere(position)) {
                map.value.refactor(holder, position)
                break
            }
        }
    }

    override fun onBindViewHolder( // 如果不知道该方法为什么要重写,请自己百度: 带有三个参数的 onBindViewHolder
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position)
        }else {
            for (map in mLayoutIdWithCallback) {
                if (map.value.item.isInHere(position)) {
                    map.value.refresh(holder, position)
                    break
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return itemCount
    }
    override fun getItemViewType(position: Int): Int {
        for (map in mLayoutIdWithCallback) {
            if (map.value.item.isInHere(position)) {
                return map.value.item.layoutId
            }
        }
        return 0
    }

    class BindingVH(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

    private abstract class Callback(
        val item: Item
    ) {
        abstract fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
        abstract fun create(holder: RecyclerView.ViewHolder)
        abstract fun refactor(holder: RecyclerView.ViewHolder, position: Int)
        abstract fun refresh(holder: RecyclerView.ViewHolder, position: Int)
    }

    /**
     * 该类使用了装饰器模式: 将原始对象作为一个参数传入给装饰者的构造器
     */
    private class BindingCallBack<DB: ViewDataBinding>(
        private val DBItem: DBItem<DB>
    ) : Callback(DBItem) {
        override fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            return BindingVH(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    DBItem.layoutId, parent, false
                )
            )
        }

        override fun create(holder: RecyclerView.ViewHolder) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.create(binding, holder)
        }

        override fun refactor(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.refactor(binding, holder, position)
            binding.executePendingBindings() // 必须调用, 原因: https://stackoom.com/question/3yD45
        }

        override fun refresh(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            val binding = (holder as BindingVH).binding as DB
            DBItem.refresh(binding, holder, position)
        }
    }

    /**
     * 该类使用了装饰器模式: 将原始对象作为一个参数传入给装饰者的构造器
     */
    private class ViewHolderCallBack<VH: RecyclerView.ViewHolder>(
        private val VHItem: VHItem<VH>
    ) : Callback(VHItem) {
        override fun createNewViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
            val rootView =
                LayoutInflater.from(parent.context)
                    .inflate(VHItem.layoutId, parent, false)
            return VHItem.getNewViewHolder(rootView)
        }

        override fun create(holder: RecyclerView.ViewHolder) {
            VHItem.create(holder as VH)
        }

        override fun refactor(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            VHItem.refactor(holder as VH, position)
        }

        override fun refresh(
            holder: RecyclerView.ViewHolder,
            position: Int
        ) {
            holder.adapterPosition
            VHItem.refresh(holder as VH, position)
        }
    }

    abstract class Item(
        val layoutId: Int
    ) {

        /**
         * 会根据你的返回值判断是否是该 item 显示的位置
         */
        abstract fun isInHere(position: Int): Boolean
    }

    /**
     * 用于添加 DataBinding 的 item
     */
    abstract class DBItem<DB: ViewDataBinding>(@LayoutRes layoutId: Int) : Item(layoutId) {
        /**
         * 在 item 创建时的回调, 建议在此处进行一些只需进行一次的操作, 如: 设置点击监听、设置用于 item 整个生命周期的对象
         *
         * **WARNING:** 在该方法中并**不能直接得到当前 item 的 position**, 但对于设置**点击事件等回调除外**,
         * 对于点击事件, 它本身是一种回调, 可以在回调时通过 holder 得到 position, 此时 item 已经被加载
         *
         * (简单插一句, 对于 holder.adapterPosition 与 holder.layoutPosition 的区别
         * 可以查看: https://blog.csdn.net/u013467495/article/details/109078905?utm_
         * medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogComme
         * ndFromBaidu%7Edefault-10.pc_relevant_baidujshouduan&depth_1-utm_sour
         * ce=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFr
         * omBaidu%7Edefault-10.pc_relevant_baidujshouduan)
         */
        abstract fun create(binding: DB, holder: BindingVH)

        /**
         * 用于设置当前 item **每次进入屏幕**显示的数据(包括离开屏幕又回到屏幕)
         *
         * **NOTE:** 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
         *
         * **WARNING:** **->> 请不要在此处创建任何新的对象 <<-**
         * 比如：设置点击监听(会生成匿名内部类)、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法,
         * ***->> 这些做法应写在 [create] 中 <<-***
         *
         * **点击事件等回调不能写在这里**, 原因在于: https://blog.csdn.net/weixin_28318011/article/details/112872952
         *
         * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
         */
        abstract fun refactor(binding: DB, holder: BindingVH, position: Int)

        /**
         * 刷新当前 item 的回调, 可用于: 修改文字、图片等
         *
         * **NOTE:** 它的修改周期只会在屏幕内, 离开后可能就会还原,
         * 因为离开后再回来就只会回调 [refactor]，解决办法是数据修改后就更改一个全局数组，在 [refactor] 中直接取数组中的值
         */
        open fun refresh(binding: DB, holder: BindingVH, position: Int) {}
    }

    /**
     * 用于添加 ViewHolder 的 item
     */
    abstract class VHItem<VH: RecyclerView.ViewHolder>(@LayoutRes layoutId: Int) : Item(layoutId) {
        /**
         * 返回一个新的 ViewHolder，**请不要返回相同的对象**
         */
        abstract fun getNewViewHolder(itemView: View): VH

        /**
         * 在 item 创建时的回调, 建议在此处进行一些只需进行一次的操作, 如: 设置点击监听、设置用于 item 整个生命周期的对象
         *
         * **WARNING:** 在该方法中并**不能直接得到当前 item 的 position**, 但对于设置**点击事件等回调除外**,
         * 对于点击事件, 它本身是一种回调, 可以在回调时通过 holder 得到 position, 此时 item 已经被加载
         *
         * (简单插一句, 对于 holder.adapterPosition 与 holder.layoutPosition 的区别
         * 可以查看: https://blog.csdn.net/u013467495/article/details/109078905?utm_
         * medium=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogComme
         * ndFromBaidu%7Edefault-10.pc_relevant_baidujshouduan&depth_1-utm_sour
         * ce=distribute.pc_relevant.none-task-blog-2%7Edefault%7EBlogCommendFr
         * omBaidu%7Edefault-10.pc_relevant_baidujshouduan)
         */
        abstract fun create(holder: VH)

        /**
         * 用于设置当前 item **每次进入屏幕**显示的数据(包括离开屏幕又回到屏幕)
         *
         * **NOTE:** 会在第一次创建 item 或者当前 item 离开屏幕再回到屏幕后调用。
         *
         * **WARNING:** **->> 请不要在此处创建任何新的对象 <<-**
         * 比如：设置点击监听(会生成匿名内部类)、设置只需用于 item 整个生命周期的对象等其他需要创建对象的做法,
         * ***->> 这些做法应写在 [create] 中 <<-***
         *
         * **点击事件等回调不能写在这里**, 原因在于: https://blog.csdn.net/weixin_28318011/article/details/112872952
         *
         * **上方 WARNING 原因请了解 RecyclerView 的真正回调流程**
         */
        abstract fun refactor(holder: VH, position: Int)

        /**
         * 刷新当前 item 的回调, 可用于: 修改文字、图片等
         *
         * **NOTE:** 它的修改周期只会在屏幕内, 离开后可能就会还原,
         * 因为离开后再回来就只会回调 [refactor]，解决办法是数据修改后就更改一个全局数组，在 [refactor] 中直接取数组中的值
         */
        open fun refresh(holder: VH, position: Int) {}
    }
}