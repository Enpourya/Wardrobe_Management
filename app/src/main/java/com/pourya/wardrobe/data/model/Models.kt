package com.pourya.wardrobe.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// Types of storage
enum class StorageType(val labelEn: String, val labelFa: String, val icon: String) {
    WARDROBE("Wardrobe", "کمد لباس", "🚪"),
    DRAWER("Drawer", "کشو", "📦"),
    SHELF("Shelf", "قفسه", "🗄️"),
    BOX("Box", "جعبه", "📫"),
    CABINET("Cabinet", "کابینت", "🗃️"),
    OTHER("Other", "سایر", "📋")
}

@Entity(tableName = "storages")
data class Storage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: StorageType,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val colorHex: String = "#6366F1" // default indigo
)

@Entity(
    tableName = "items",
    foreignKeys = [
        ForeignKey(
            entity = Storage::class,
            parentColumns = ["id"],
            childColumns = ["storageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["storageId"])]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storageId: Long,
    val name: String,
    val imagePath: String? = null,
    val category: String = "",
    val color: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class StorageWithItems(
    val storage: Storage,
    val items: List<Item>
)

data class ItemWithStorage(
    val item: Item,
    val storage: Storage
)
