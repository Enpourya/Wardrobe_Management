package com.pourya.wardrobe.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.data.model.Storage

@Dao
interface StorageDao {

    @Query("SELECT * FROM storages ORDER BY createdAt DESC")
    fun getAllStorages(): LiveData<List<Storage>>

    @Query("SELECT * FROM storages ORDER BY createdAt DESC")
    suspend fun getAllStoragesSync(): List<Storage>

    @Query("SELECT * FROM storages WHERE id = :id")
    suspend fun getStorageById(id: Long): Storage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(storage: Storage): Long

    @Update
    suspend fun update(storage: Storage)

    @Delete
    suspend fun delete(storage: Storage)

    @Query("SELECT COUNT(*) FROM storages")
    suspend fun getCount(): Int
}

@Dao
interface ItemDao {

    @Query("SELECT * FROM items WHERE storageId = :storageId ORDER BY createdAt DESC")
    fun getItemsByStorage(storageId: Long): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE storageId = :storageId ORDER BY createdAt DESC")
    suspend fun getItemsByStorageSync(storageId: Long): List<Item>

    @Query("SELECT * FROM items ORDER BY createdAt DESC")
    suspend fun getAllItemsSync(): List<Item>

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchItems(query: String): LiveData<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id")
    suspend fun getItemById(id: Long): Item?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Item): Long

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT COUNT(*) FROM items WHERE storageId = :storageId")
    suspend fun getCountByStorage(storageId: Long): Int

    @Query("DELETE FROM items WHERE storageId = :storageId")
    suspend fun deleteByStorage(storageId: Long)
}
