package com.pourya.wardrobe.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.pourya.wardrobe.data.model.Item
import com.pourya.wardrobe.data.model.Storage
import com.pourya.wardrobe.data.model.StorageType

class Converters {
    @TypeConverter
    fun fromStorageType(type: StorageType): String = type.name

    @TypeConverter
    fun toStorageType(name: String): StorageType = StorageType.valueOf(name)
}

@Database(
    entities = [Storage::class, Item::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class WardrobeDatabase : RoomDatabase() {

    abstract fun storageDao(): StorageDao
    abstract fun itemDao(): ItemDao

    companion object {
        const val DATABASE_NAME = "wardrobe_db"

        @Volatile
        private var INSTANCE: WardrobeDatabase? = null

        fun getInstance(context: Context): WardrobeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WardrobeDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
