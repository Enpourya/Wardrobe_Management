package com.pourya.wardrobe.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pourya.wardrobe.databinding.FragmentSearchBinding
import com.pourya.wardrobe.utils.LocaleManager

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchResultAdapter
    private val isFarsi by lazy { LocaleManager.isPersian(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAdapter()
        setupSearch()
        observeResults()
    }

    private fun setupAdapter() {
        adapter = SearchResultAdapter(isFarsi)
        binding.recyclerResults.adapter = adapter
    }

    private fun setupSearch() {
        binding.etSearch.doAfterTextChanged { text ->
            val query = text?.toString()?.trim() ?: ""
            if (query.length >= 1) {
                viewModel.search(query)
                binding.emptyHint.visibility = View.GONE
            } else {
                adapter.submitList(emptyList())
                binding.emptyHint.visibility = View.VISIBLE
                binding.noResults.visibility = View.GONE
            }
        }
    }

    private fun observeResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { results ->
            adapter.submitList(results)
            val query = binding.etSearch.text?.toString()?.trim() ?: ""
            binding.noResults.visibility =
                if (results.isEmpty() && query.isNotEmpty()) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
