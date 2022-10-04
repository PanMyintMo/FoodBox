package com.pan.foodbox.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pan.foodbox.dao.CartItemDao
import com.pan.foodbox.dao.DaoRecord
import com.pan.foodbox.entity.CartItem
import com.pan.foodbox.entity.ItemHistory

@Database(
    version =2,
    entities = [CartItem::class,ItemHistory::class],
    exportSchema = true
)
@TypeConverters(Converters::class)

abstract class AppDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao
    abstract fun daoRecord():DaoRecord

  companion  object {
        private const val DB_NAME = "app_database"

      private val MIGRATION_1_2 = object : Migration(1, 2) {
          override fun migrate(database: SupportSQLiteDatabase) {
              database.execSQL("CREATE TABLE IF NOT EXISTS `item_records` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cart_items` TEXT NOT NULL, `restaurant_name` TEXT NOT NULL, `customerName` TEXT NOT NULL, `created_at` INTEGER NOT NULL)")
          }
      }

        @Volatile
        var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also {
                    instance = it }
            }
        }
      private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(MIGRATION_1_2)
                .build()
        }
  }
}

