package com.smh.fbconnect.ui.start.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.databinding.ItemAppBinding
import com.smh.fbconnect.utils.delegates.viewBinding

class AppsAdapter(
    private var appList: ArrayList<AppEntity> = arrayListOf(),
    private val onItemClick: OnItemClick? = null
): RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder = AppViewHolder(
        itemAppBinding = parent.viewBinding(ItemAppBinding::inflate),
        onItemClick = onItemClick
    )

    fun interface OnItemClick {
        fun onCLick(appId: Int)
    }

    fun updateAppList(apps: ArrayList<AppEntity>, itemCount: Int) {
        this.appList = apps
        notifyItemRangeInserted(0, itemCount)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(app = appList[position])
    }

    override fun getItemCount(): Int = appList.size

    class AppViewHolder(
        itemAppBinding: ItemAppBinding,
        private val onItemClick: OnItemClick?
    ): RecyclerView.ViewHolder(itemAppBinding.root) {

        private val binding = ItemAppBinding.bind(itemAppBinding.root)

        fun bind(app: AppEntity) {

            binding.apply {

                root.setOnClickListener {
                    onItemClick?.onCLick(appId = app.id)
                }

                appNameTextView.text = app.name
            }

        }
    }
}