package com.abhay.weather.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.abhay.weather.data.models.Cities
import com.abhay.weather.util.DB_NAME

@Database(
    entities = [Cities::class],
    version = 2
)
abstract class CityDatabase : RoomDatabase() {

    abstract fun getCity(): CityDao

    companion object {

        @Volatile
        private var instance: CityDatabase? = null
        private val LOCK = Any()

        fun getDatabase(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, CityDatabase::class.java, DB_NAME)
                .build()
    }
}