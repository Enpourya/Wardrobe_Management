package com.pourya.wardrobe.data.repository

import android.content.Context
import com.pourya.wardrobe.data.db.WardrobeDatabase
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.data.model.Storage

class WardrobeRepository(context: Context) {

    private val db = WardrobeDatabase.getInstance(context)
    private val storageDao = db.storageDao()
    private val itemDao = db.itemDao()

    // Storage operations
    val allStorages = storageDao.getAllStorages()

    suspend fun getAllStoragesSync() = storageDao.getAllStoragesSync()
    suspend fun getStorageById(id: Long) = storageDao.getStorageById(id)
    suspend fun insertStorage(storage: Storage) = storageDao.insert(storage)
    suspend fun updateStorage(storage: Storage) = storageDao.update(storage)
    suspend fun deleteStorage(storage: Storage) = storageDao.delete(storage)

    // Item operations
    fun getItemsByStorage(storageId: Long) = itemDao.getItemsByStorage(storageId)
    suspend fun getItemsByStorageSync(storageId: Long) = itemDao.getItemsByStorageSync(storageId)
    suspend fun getAllItemsSync() = itemDao.getAllItemsSync()
    fun searchItems(query: String) = itemDao.searchItems(query)
    suspend fun getItemById(id: Long) = itemDao.getItemById(id)
    suspend fun insertItem(item: Item) = itemDao.insert(item)
    suspend fun updateItem(item: Item) = itemDao.update(item)
    suspend fun deleteItem(item: Item) = itemDao.delete(item)
    suspend fun getItemCountByStorage(storageId: Long) = itemDao.getCountByStorage(storageId)
}
