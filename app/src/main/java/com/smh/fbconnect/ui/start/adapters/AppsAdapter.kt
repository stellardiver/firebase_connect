package com.smh.fbconnect.ui.start.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.databinding.ItemAppBinding
import com.smh.fbconnect.utils.delegates.viewBinding

class AppsAdapter(
    private var appList: ArrayList<AppEntity> = arrayListOf(),
    private val onItemClick: OnItemClick? = null,
    private val onItemDelete: OnItemDelete? = null
): RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder = AppViewHolder(
        itemAppBinding = parent.viewBinding(ItemAppBinding::inflate),
        onItemClick = onItemClick,
        onItemDelete = onItemDelete
    )

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(app = appList[position])
    }

    override fun getItemCount(): Int = appList.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun interface OnItemClick {
        fun onCLick(appId: Int)
    }

    fun interface OnItemDelete {
        fun onClick(appId: Int, position: Int)
    }

    fun updateAppList(apps: ArrayList<AppEntity>, itemCount: Int) {
        this.appList = apps
        notifyItemRangeInserted(0, itemCount)
    }

    fun deleteItem(position: Int, appList: ArrayList<AppEntity>) {
        this.appList = appList
        notifyItemRemoved(position)
    }

    class AppViewHolder(
        itemAppBinding: ItemAppBinding,
        private val onItemClick: OnItemClick?,
        private val onItemDelete: OnItemDelete?
    ): RecyclerView.ViewHolder(itemAppBinding.root) {

        private val binding = ItemAppBinding.bind(itemAppBinding.root)

        fun bind(app: AppEntity) {

            binding.apply {

                root.setOnClickListener {
                    onItemClick?.onCLick(appId = app.id)
                }

                deleteItemButton.setOnClickListener {
                    onItemDelete?.onClick(
                        appId = app.id,
                        position = bindingAdapterPosition
                    )
                }

                appNameTextView.text = app.name
            }

        }
    }
}