package com.example.groceryapp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.groceryapp.data.models.Item
import com.example.groceryapp.databinding.ItemRowBinding
import java.text.SimpleDateFormat
import java.util.*

class ItemAdapter(
    private val listener: OnItemClickListener,
    private val userNickname: String // Added user nickname here
) : ListAdapter<Item, ItemAdapter.ViewHolder>(DiffCallback()) {

    interface OnItemClickListener {
        fun onItemClicked(item: Item)
        fun onEditButtonClicked(item: Item)
    }

    class DiffCallback: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Item, newItem: Item) = oldItem == newItem
    }

    inner class ViewHolder(private val binding: ItemRowBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            // Set the user's nickname on the left
            binding.userNicknameTv.text = item.lastModifiedBy

            // Set item details
            binding.itemNameTv.text = item.name
            binding.quantityTv.text = "Qty: ${item.quantity}"
            binding.priceTv.text = "$${item.price}"
            val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.US)
            binding.dateTv.text = item.buyBefore?.toDate()?.let { sdf.format(it) } ?: ""

            // Entire row click - show item details
            binding.root.setOnClickListener {
                listener.onItemClicked(item)
            }

            // Edit button click
            binding.editBtn.setOnClickListener {
                listener.onEditButtonClicked(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
