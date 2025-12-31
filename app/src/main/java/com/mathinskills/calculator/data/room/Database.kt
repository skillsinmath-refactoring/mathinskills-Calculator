package com.mathinskills.calculator.data.room

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.mathinskills.calculator.data.entity.CalculationEntity
import com.mathinskills.calculator.data.entity.RegionStandardEntity
import com.mathinskills.calculator.data.dao.CalculationDao
import com.mathinskills.calculator.data.dao.RegionStandardDao

@Database(
    entities = [CalculationEntity::class, RegionStandardEntity::class],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun calculationDao(): CalculationDao
    abstract fun regionStandardDao(): RegionStandardDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "math_skill_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
