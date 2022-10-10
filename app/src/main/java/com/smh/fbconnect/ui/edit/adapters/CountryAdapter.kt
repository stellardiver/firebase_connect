package com.smh.fbconnect.ui.edit.adapters

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.recyclerview.widget.RecyclerView
import com.smh.fbconnect.R
import com.smh.fbconnect.data.local.model.Country
import com.smh.fbconnect.databinding.ItemCountryBinding
import com.smh.fbconnect.utils.delegates.viewBinding

class CountryAdapter(
    private val onCheckedChanged: OnCheckedChanged
): RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countryList = arrayListOf<Country>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder = CountryViewHolder(
        parent.viewBinding(ItemCountryBinding::inflate),
        onCheckedChanged
    )

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(country = countryList[position])
    }

    override fun getItemCount(): Int {
        return countryList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun interface OnCheckedChanged {
        fun onCheckChange(bindingPosition: Int, isChecked: Boolean)
    }

    fun updateAdapter(countryList: ArrayList<Country>) {
        this.countryList = countryList
        notifyItemRangeInserted(0, itemCount)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAllItems(countryList: ArrayList<Country>) {
        this.countryList = countryList
        notifyDataSetChanged()
    }

    class CountryViewHolder(
        itemCountryBinding: ItemCountryBinding,
        private val onCheckedChanged: OnCheckedChanged
    ): RecyclerView.ViewHolder(itemCountryBinding.root) {

        private val binding = ItemCountryBinding.bind(itemCountryBinding.root)

        fun bind(country: Country) {

            binding.apply {

                countryCheckBox.text = country.name
                countryCheckBox.isChecked = country.is_checked

                root.setOnClickListener {

                    countryCheckBox.isChecked = !countryCheckBox.isChecked

                    onCheckedChanged.onCheckChange(
                        bindingPosition = bindingAdapterPosition,
                        isChecked = countryCheckBox.isChecked
                    )
                }
            }
        }
    }
}