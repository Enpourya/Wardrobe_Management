package com.pourya.wardrobe.ui.search

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pourya.wardrobe.R
import com.pourya.wardrobe.databinding.ItemSearchResultBinding
import java.io.File

class SearchResultAdapter(private val isFarsi: Boolean) :
    ListAdapter<SearchResult, SearchResultAdapter.ViewHolder>(DIFF) {

    inner class ViewHolder(val binding: ItemSearchResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(result: SearchResult) {
            binding.tvItemName.text = result.item.name
            binding.tvStorageName.text = "${result.storageIcon} ${result.storageName}"
            if (result.item.notes.isNotBlank()) {
                binding.tvNotes.text = result.item.notes
                binding.tvNotes.visibility = android.view.View.VISIBLE
            } else {
                binding.tvNotes.visibility = android.view.View.GONE
            }

            try {
                binding.viewDot.setBackgroundColor(Color.parseColor(result.storageColor))
            } catch (_: Exception) {}

            val imageFile = result.item.imagePath?.let { File(it) }
            if (imageFile?.exists() == true) {
                Glide.with(binding.root).load(imageFile).centerCrop()
                    .placeholder(R.drawable.ic_hanger).into(binding.ivThumb)
            } else {
                binding.ivThumb.setImageResource(R.drawable.ic_hanger)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(getItem(position))

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<SearchResult>() {
            override fun areItemsTheSame(o: SearchResult, n: SearchResult) = o.item.id == n.item.id
            override fun areContentsTheSame(o: SearchResult, n: SearchResult) = o == n
        }
    }
}
