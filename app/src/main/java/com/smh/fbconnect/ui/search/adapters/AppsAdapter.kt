package com.smh.fbconnect.ui.search.adapters

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.smh.fbconnect.data.local.entity.AppEntity
import com.smh.fbconnect.databinding.ItemAppBinding
import com.smh.fbconnect.utils.delegates.viewBinding

class AppsAdapter(
    private var appList: ArrayList<AppEntity> = arrayListOf()
): RecyclerView.Adapter<AppsAdapter.AppViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AppViewHolder = AppViewHolder(
        parent.viewBinding(ItemAppBinding::inflate)
    )

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        holder.bind(app = appList[position])
    }

    override fun getItemCount(): Int = appList.size

    class AppViewHolder(
        itemAppBinding: ItemAppBinding
    ): RecyclerView.ViewHolder(itemAppBinding.root) {

        fun bind(app: AppEntity) {

        }
    }
}