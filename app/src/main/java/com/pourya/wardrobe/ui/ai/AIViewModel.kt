package com.pourya.wardrobe.ui.ai

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pourya.wardrobe.data.repository.WardrobeRepository
import com.pourya.wardrobe.utils.AIService
import kotlinx.coroutines.launch

class AIViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = WardrobeRepository(application)

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _result = MutableLiveData<String?>()
    val result: LiveData<String?> = _result

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun getSuggestions(context: Context, occasion: String, isFarsi: Boolean) {
        _isLoading.value = true
        _result.value = null
        _error.value = null

        viewModelScope.launch {
            val allItems = repo.getAllItemsSync()
            val itemNames = allItems.map { it.name }

            if (itemNames.isEmpty()) {
                val msg = if (isFarsi) "ابتدا لباس‌هایی به کمدتان اضافه کنید." else "Please add items to your wardrobe first."
                _error.postValue(msg)
                _isLoading.postValue(false)
                return@launch
            }

            AIService.getOutfitSuggestions(context, occasion, itemNames, isFarsi)
                .onSuccess { _result.postValue(it) }
                .onFailure { _error.postValue(it.message) }

            _isLoading.postValue(false)
        }
    }
}
