package com.pourya.wardrobe.ui.item

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.databinding.DialogAddItemBinding
import com.pourya.wardrobe.databinding.FragmentItemBinding
import com.pourya.wardrobe.utils.LocaleManager
import java.io.File
import androidx.activity.result.contract.ActivityResultContracts

class ItemFragment : Fragment() {

    private var _binding: FragmentItemBinding? = null
    private val binding get() = _binding!!

    // Read storageId from arguments bundle directly
    private val storageId: Long by lazy { requireArguments().getLong("storageId", -1L) }

    private val viewModel: ItemViewModel by viewModels()
    private lateinit var adapter: ItemAdapter
    private val isFarsi by lazy { LocaleManager.isPersian(requireContext()) }

    private var pendingDialogBinding: DialogAddItemBinding? = null
    private var pendingImageUri: Uri? = null
    private var cameraUri: Uri? = null

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            pendingImageUri = it
            pendingDialogBinding?.let { db ->
                Glide.with(this).load(it).centerCrop().into(db.ivPreview)
                db.ivPreview.visibility = View.VISIBLE
            }
        }
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingImageUri = cameraUri
            pendingDialogBinding?.let { db ->
                Glide.with(this).load(cameraUri).centerCrop().into(db.ivPreview)
                db.ivPreview.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadStorage(storageId)
        setupRecyclerView()
        observeData()
        binding.fabAdd.setOnClickListener { showAddItemDialog() }
        binding.ivBack.setOnClickListener { findNavController().popBackStack() }
    }

    private fun setupRecyclerView() {
        adapter = ItemAdapter(
            isFarsi = isFarsi,
            onClick = { item ->
                val bundle = Bundle().apply { putLong("itemId", item.id) }
                findNavController().navigate(
                    com.pourya.wardrobe.R.id.action_itemFragment_to_itemDetailFragment, bundle
                )
            },
            onLongClick = { item -> showItemOptions(item) }
        )
        binding.recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.storage.observe(viewLifecycleOwner) { storage ->
            storage?.let {
                binding.tvTitle.text = it.name
                binding.tvStorageType.text = if (isFarsi) it.type.labelFa else it.type.labelEn
            }
        }
        viewModel.items.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.emptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
            binding.tvCount.text = if (isFarsi) "${items.size} آیتم" else "${items.size} items"
        }
    }

    private fun showAddItemDialog(item: Item? = null) {
        val dialogBinding = DialogAddItemBinding.inflate(layoutInflater)
        pendingDialogBinding = dialogBinding
        pendingImageUri = null

        item?.let {
            dialogBinding.etName.setText(it.name)
            dialogBinding.etNotes.setText(it.notes)
            it.imagePath?.let { path ->
                val file = File(path)
                if (file.exists()) {
                    Glide.with(this).load(file).centerCrop().into(dialogBinding.ivPreview)
                    dialogBinding.ivPreview.visibility = View.VISIBLE
                }
            }
        }

        dialogBinding.btnGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        dialogBinding.btnCamera.setOnClickListener {
            val photoFile = File.createTempFile("item_", ".jpg", requireContext().cacheDir)
            cameraUri = FileProvider.getUriForFile(
                requireContext(), "${requireContext().packageName}.fileprovider", photoFile
            )
            cameraLauncher.launch(cameraUri!!)
        }

        val title = if (item != null) {
            if (isFarsi) "ویرایش آیتم" else "Edit Item"
        } else {
            if (isFarsi) "افزودن آیتم" else "Add Item"
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(title)
            .setView(dialogBinding.root)
            .setPositiveButton(if (isFarsi) "ذخیره" else "Save") { _, _ ->
                val name = dialogBinding.etName.text?.toString()?.trim() ?: ""
                val notes = dialogBinding.etNotes.text?.toString()?.trim() ?: ""
                if (name.isNotEmpty()) {
                    if (item != null) viewModel.updateItem(item, name, pendingImageUri, notes)
                    else viewModel.addItem(storageId, name, pendingImageUri, notes)
                }
            }
            .setNegativeButton(if (isFarsi) "انصراف" else "Cancel", null)
            .setOnDismissListener { pendingDialogBinding = null }
            .show()
    }

    private fun showItemOptions(item: Item) {
        val opts = if (isFarsi) arrayOf("✏️ ویرایش", "🗑️ حذف") else arrayOf("✏️ Edit", "🗑️ Delete")
        MaterialAlertDialogBuilder(requireContext())
            .setItems(opts) { _, which ->
                when (which) {
                    0 -> showAddItemDialog(item)
                    1 -> {
                        val msg = if (isFarsi) "حذف «${item.name}»؟" else "Delete \"${item.name}\"?"
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(msg)
                            .setPositiveButton(if (isFarsi) "حذف" else "Delete") { _, _ -> viewModel.deleteItem(item) }
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
