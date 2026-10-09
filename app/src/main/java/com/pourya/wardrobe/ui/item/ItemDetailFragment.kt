package com.pourya.wardrobe.ui.item

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.pourya.wardrobe.data.db.WardrobeDatabase
import com.pourya.wardrobe.databinding.FragmentItemDetailBinding
import kotlinx.coroutines.launch
import java.io.File

class ItemDetailFragment : Fragment() {

    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    private val itemId: Long by lazy { requireArguments().getLong("itemId", -1L) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }

        lifecycleScope.launch {
            val db = WardrobeDatabase.getInstance(requireContext())
            val item = db.itemDao().getItemById(itemId) ?: return@launch
            binding.tvName.text = item.name
            binding.tvNotes.text = item.notes
            binding.tvNotes.visibility = if (item.notes.isNotBlank()) View.VISIBLE else View.GONE
            item.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    Glide.with(this@ItemDetailFragment).load(file).into(binding.ivFull)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
