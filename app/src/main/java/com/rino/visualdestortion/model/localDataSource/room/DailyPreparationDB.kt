package com.rino.visualdestortion.model.localDataSource.room

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities = arrayOf(DailyPreparation::class), version = 1, exportSchema = false)
@TypeConverters(MapConverter::class)
public abstract class DailyPreparationDB : RoomDatabase() {
    abstract fun DailyPreparationDao(): DailyPreparationDao

    companion object {
        @Volatile
        private var INSTANCE: DailyPreparationDB? = null
        fun getDatabase(application: Application): DailyPreparationDB {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    application.applicationContext,
                    DailyPreparationDB::class.java,
                    "DailyPreparation_database"
                ).build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}