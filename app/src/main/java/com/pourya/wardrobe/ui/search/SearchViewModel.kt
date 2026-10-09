package com.pourya.wardrobe.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.data.repository.WardrobeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

data class SearchResult(
    val item: Item,
    val storageName: String,
    val storageIcon: String,
    val storageColor: String
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = WardrobeRepository(application)

    private val _searchResults = MutableLiveData<List<SearchResult>>(emptyList())
    val searchResults: LiveData<List<SearchResult>> = _searchResults

    private var searchJob: Job? = null

    fun search(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) { _searchResults.value = emptyList(); return }

        searchJob = viewModelScope.launch {
            // Observe raw items, enrich with storage info
            repo.searchItems(query).observeForever { items ->
                viewModelScope.launch {
                    val storages = repo.getAllStoragesSync().associateBy { it.id }
                    val results = items.map { item ->
                        val st = storages[item.storageId]
                        SearchResult(
                            item = item,
                            storageName = st?.name ?: "?",
                            storageIcon = st?.type?.icon ?: "📦",
                            storageColor = st?.colorHex ?: "#6366F1"
                        )
                    }
                    _searchResults.postValue(results)
                }
            }
        }
    }
}
