package com.pourya.wardrobe.ui.storage

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pourya.wardrobe.data.model.Storage
import com.pourya.wardrobe.data.model.StorageType
import com.pourya.wardrobe.data.repository.WardrobeRepository
import kotlinx.coroutines.launch

class StorageViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = WardrobeRepository(application)
    val allStorages: LiveData<List<Storage>> = repo.allStorages

    private val _operationResult = MutableLiveData<OperationResult>()
    val operationResult: LiveData<OperationResult> = _operationResult

    // Item counts per storage (storageId -> count)
    private val _itemCounts = MutableLiveData<Map<Long, Int>>()
    val itemCounts: LiveData<Map<Long, Int>> = _itemCounts

    fun loadItemCounts(storages: List<Storage>) {
        viewModelScope.launch {
            val counts = mutableMapOf<Long, Int>()
            storages.forEach { counts[it.id] = repo.getItemCountByStorage(it.id) }
            _itemCounts.postValue(counts)
        }
    }

    fun addStorage(name: String, type: StorageType, color: String = "#6366F1") {
        if (name.isBlank()) {
            _operationResult.value = OperationResult.Error("Name cannot be empty")
            return
        }
        viewModelScope.launch {
            repo.insertStorage(Storage(name = name.trim(), type = type, colorHex = color))
            _operationResult.postValue(OperationResult.Success)
        }
    }

    fun updateStorage(storage: Storage, newName: String, newType: StorageType, newColor: String) {
        viewModelScope.launch {
            repo.updateStorage(storage.copy(name = newName.trim(), type = newType, colorHex = newColor))
            _operationResult.postValue(OperationResult.Success)
        }
    }

    fun deleteStorage(storage: Storage) {
        viewModelScope.launch {
            repo.deleteStorage(storage)
            _operationResult.postValue(OperationResult.Success)
        }
    }

    sealed class OperationResult {
        object Success : OperationResult()
        data class Error(val message: String) : OperationResult()
    }
}
