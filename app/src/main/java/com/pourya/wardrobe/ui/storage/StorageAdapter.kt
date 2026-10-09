package com.pourya.wardrobe.ui.storage

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pourya.wardrobe.data.model.Storage
import com.pourya.wardrobe.databinding.ItemStorageCardBinding

class StorageAdapter(
    private val isFarsi: Boolean,
    private val onItemClick: (Storage) -> Unit,
    private val onLongClick: (Storage) -> Unit
) : ListAdapter<Storage, StorageAdapter.ViewHolder>(DIFF_CALLBACK) {

    private val itemCounts = mutableMapOf<Long, Int>()

    fun updateCounts(counts: Map<Long, Int>) {
        itemCounts.clear()
        itemCounts.putAll(counts)
        notifyItemRangeChanged(0, itemCount)
    }

    inner class ViewHolder(val binding: ItemStorageCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storage: Storage) {
            binding.tvName.text = storage.name
            binding.tvIcon.text = storage.type.icon
            val typeLabel = if (isFarsi) storage.type.labelFa else storage.type.labelEn
            binding.tvType.text = typeLabel

            val count = itemCounts[storage.id] ?: 0
            binding.tvCount.text = if (isFarsi) "$count آیتم" else "$count items"

            try {
                val color = Color.parseColor(storage.colorHex)
                binding.viewColorBar.setBackgroundColor(color)
                binding.tvIcon.setBackgroundColor(color and 0x30FFFFFF or 0x15000000)
            } catch (_: Exception) {}

            binding.root.setOnClickListener { onItemClick(storage) }
            binding.root.setOnLongClickListener { onLongClick(storage); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStorageCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Storage>() {
            override fun areItemsTheSame(o: Storage, n: Storage) = o.id == n.id
            override fun areContentsTheSame(o: Storage, n: Storage) = o == n
        }
    }
}
