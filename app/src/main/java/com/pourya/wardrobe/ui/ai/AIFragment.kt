package com.pourya.wardrobe.ui.ai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pourya.wardrobe.databinding.FragmentAiBinding
import com.pourya.wardrobe.utils.AIService
import com.pourya.wardrobe.utils.LocaleManager

class AIFragment : Fragment() {

    private var _binding: FragmentAiBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AIViewModel by viewModels()
    private val isFarsi by lazy { LocaleManager.isPersian(requireContext()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val hasKey = AIService.isApiKeySet(requireContext())
        if (!hasKey) {
            binding.layoutNoKey.visibility = View.VISIBLE
            binding.layoutMain.visibility = View.GONE
        } else {
            binding.layoutNoKey.visibility = View.GONE
            binding.layoutMain.visibility = View.VISIBLE
        }

        binding.btnSuggest.setOnClickListener {
            val occasion = binding.etOccasion.text?.toString()?.trim() ?: ""
            if (occasion.isNotEmpty()) {
                viewModel.getSuggestions(requireContext(), occasion, isFarsi)
            }
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { loading ->
            binding.progressBar.visibility = if (loading) View.VISIBLE else View.GONE
            binding.btnSuggest.isEnabled = !loading
        }

        viewModel.result.observe(viewLifecycleOwner) { result ->
            result?.let {
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = it
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { err ->
            err?.let {
                binding.tvResult.visibility = View.VISIBLE
                binding.tvResult.text = if (isFarsi) "خطا: $it" else "Error: $it"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
