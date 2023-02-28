package com.example.darbo_uzduotis.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.darbo_uzduotis.dao.WeatherForecastDao
import com.example.darbo_uzduotis.data.ForecastDataDB

@Database(entities = [ForecastDataDB::class], version = 2)
abstract class WeatherForecastDatabase : RoomDatabase() {

    abstract fun weatherForecastDao(): WeatherForecastDao

    companion object {
        @Volatile
        private var INSTANCE: WeatherForecastDatabase? = null

        fun getInstance(context: Context): WeatherForecastDatabase = INSTANCE
            ?: synchronized(this) {
                INSTANCE
                    ?: createDatabase(
                        context
                    ).also {
                        INSTANCE = it
                    }
            }

        private fun createDatabase(context: Context): WeatherForecastDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                WeatherForecastDatabase::class.java, "WeatherForecast"
            ).fallbackToDestructiveMigration().build()
        }
    }
}