package com.pourya.wardrobe.ui.storage

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pourya.wardrobe.R
import com.pourya.wardrobe.data.model.Storage
import com.pourya.wardrobe.data.model.StorageType
import com.pourya.wardrobe.databinding.DialogAddStorageBinding
import com.pourya.wardrobe.databinding.FragmentStorageBinding
import com.pourya.wardrobe.utils.LocaleManager

class StorageFragment : Fragment() {

    private var _binding: FragmentStorageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StorageViewModel by viewModels()
    private lateinit var adapter: StorageAdapter
    private val isFarsi by lazy { LocaleManager.isPersian(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStorageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeData()
        binding.fabAdd.setOnClickListener { showAddStorageDialog() }
    }

    private fun setupRecyclerView() {
        adapter = StorageAdapter(
            isFarsi = isFarsi,
            onItemClick = { storage ->
                val bundle = Bundle().apply { putLong("storageId", storage.id) }
                findNavController().navigate(R.id.action_storageFragment_to_itemFragment, bundle)
            },
            onLongClick = { storage -> showEditDeleteDialog(storage) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.allStorages.observe(viewLifecycleOwner) { storages ->
            adapter.submitList(storages)
            binding.emptyState.visibility = if (storages.isEmpty()) View.VISIBLE else View.GONE
            viewModel.loadItemCounts(storages)
        }
        viewModel.itemCounts.observe(viewLifecycleOwner) { counts ->
            adapter.updateCounts(counts)
        }
    }

    private fun showAddStorageDialog(existing: Storage? = null) {
        val dialogBinding = DialogAddStorageBinding.inflate(layoutInflater)
        val isEdit = existing != null

        existing?.let { dialogBinding.etName.setText(it.name) }

        val colors = listOf("#6366F1","#EC4899","#10B981","#F59E0B","#3B82F6","#EF4444","#8B5CF6","#14B8A6")
        var selectedColor = existing?.colorHex ?: colors[0]

        dialogBinding.chipGroupColors.removeAllViews()
        colors.forEach { hex ->
            val chip = Chip(requireContext()).apply {
                text = "  "
                isCheckable = true
                isChecked = hex == selectedColor
                try { setChipBackgroundColorResource(android.R.color.transparent)
                    chipBackgroundColor = android.content.res.ColorStateList.valueOf(Color.parseColor(hex))
                } catch (_: Exception) {}
                setOnCheckedChangeListener { _, checked -> if (checked) selectedColor = hex }
            }
            dialogBinding.chipGroupColors.addView(chip)
        }

        val types = StorageType.values()
        val typeLabels = if (isFarsi) types.map { it.labelFa } else types.map { it.labelEn }

        dialogBinding.chipGroupType.removeAllViews()
        types.forEachIndexed { index, type ->
            val chip = Chip(requireContext()).apply {
                text = "${type.icon} ${typeLabels[index]}"
                isCheckable = true
                isChecked = if (isEdit) type == existing?.type else index == 0
            }
            dialogBinding.chipGroupType.addView(chip)
        }

        val title = if (isEdit) (if (isFarsi) "ویرایش" else "Edit")
                    else (if (isFarsi) "افزودن کمد / کشو" else "Add Storage")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(if (isFarsi) "ذخیره" else "Save") { _, _ ->
                val name = dialogBinding.etName.text?.toString() ?: return@setPositiveButton
                val checkedIndex = (0 until dialogBinding.chipGroupType.childCount)
                    .firstOrNull { i -> (dialogBinding.chipGroupType.getChildAt(i) as? Chip)?.isChecked == true } ?: 0
                val selectedType = types[checkedIndex]
                if (isEdit && existing != null)
                    viewModel.updateStorage(existing, name, selectedType, selectedColor)
                else
                    viewModel.addStorage(name, selectedType, selectedColor)
            }
            .setNegativeButton(if (isFarsi) "انصراف" else "Cancel", null)
            .show()
    }

    private fun showEditDeleteDialog(storage: Storage) {
        val options = if (isFarsi) arrayOf("✏️ ویرایش", "🗑️ حذف") else arrayOf("✏️ Edit", "🗑️ Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(options) { _, which ->
                when (which) {
                    0 -> showAddStorageDialog(storage)
                    1 -> {
                        val msg = if (isFarsi)
                            "آیا از حذف «${storage.name}» مطمئن هستید؟ تمام محتویات آن نیز حذف خواهد شد."
                        else "Delete \"${storage.name}\"? All its items will also be deleted."
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(msg)
                            .setPositiveButton(if (isFarsi) "حذف" else "Delete") { _, _ -> viewModel.deleteStorage(storage) }
                            .setNegativeButton(if (isFarsi) "انصراف" else "Cancel", null)
                            .show()
                    }
                }
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
