package com.github.kimhyunjin.mycontentprovider.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            if (INSTANCE == null) {
                synchronized(ItemDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context, ItemDatabase::class.java,
                        "item-database.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}