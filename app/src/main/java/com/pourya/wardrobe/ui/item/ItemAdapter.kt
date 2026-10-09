package com.pourya.wardrobe.ui.item

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pourya.wardrobe.R
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.databinding.ItemClothingCardBinding
import java.io.File

class ItemAdapter(
    private val isFarsi: Boolean,
    private val onClick: (Item) -> Unit,
    private val onLongClick: (Item) -> Unit
) : ListAdapter<Item, ItemAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(val binding: ItemClothingCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Item) {
            binding.tvName.text = item.name
            if (item.notes.isNotBlank()) {
                binding.tvNotes.text = item.notes
                binding.tvNotes.visibility = android.view.View.VISIBLE
            } else {
                binding.tvNotes.visibility = android.view.View.GONE
            }

            val imageFile = item.imagePath?.let { File(it) }
            if (imageFile != null && imageFile.exists()) {
                Glide.with(binding.root)
                    .load(imageFile)
                    .centerCrop()
                    .placeholder(R.drawable.ic_hanger)
                    .into(binding.ivImage)
            } else {
                binding.ivImage.setImageResource(R.drawable.ic_hanger)
            }

            binding.root.setOnClickListener { onClick(item) }
            binding.root.setOnLongClickListener { onLongClick(item); true }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemClothingCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Item>() {
            override fun areItemsTheSame(o: Item, n: Item) = o.id == n.id
            override fun areContentsTheSame(o: Item, n: Item) = o == n
        }
    }
}
