package com.pourya.wardrobe.ui.item

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.data.model.Storage
import com.pourya.wardrobe.data.repository.WardrobeRepository
import com.pourya.wardrobe.utils.ImageManager
import kotlinx.coroutines.launch

class ItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = WardrobeRepository(application)

    private val _storage = MutableLiveData<Storage?>()
    val storage: LiveData<Storage?> = _storage

    private lateinit var _items: LiveData<List<Item>>
    val items get() = _items

    private val _operationResult = MutableLiveData<Boolean>()
    val operationResult: LiveData<Boolean> = _operationResult

    fun loadStorage(storageId: Long) {
        _items = repo.getItemsByStorage(storageId)
        viewModelScope.launch {
            _storage.postValue(repo.getStorageById(storageId))
        }
    }

    fun addItem(storageId: Long, name: String, imageUri: Uri?, notes: String) {
        viewModelScope.launch {
            val imagePath = imageUri?.let { ImageManager.saveImage(getApplication(), it) }
            repo.insertItem(Item(storageId = storageId, name = name.trim(), imagePath = imagePath, notes = notes))
            _operationResult.postValue(true)
        }
    }

    fun updateItem(item: Item, newName: String, newImageUri: Uri?, newNotes: String) {
        viewModelScope.launch {
            var imagePath = item.imagePath
            if (newImageUri != null) {
                ImageManager.deleteImage(imagePath)
                imagePath = ImageManager.saveImage(getApplication(), newImageUri)
            }
            repo.updateItem(item.copy(name = newName.trim(), imagePath = imagePath, notes = newNotes))
            _operationResult.postValue(true)
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            ImageManager.deleteImage(item.imagePath)
            repo.deleteItem(item)
        }
    }
}
