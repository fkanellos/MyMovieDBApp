package com.example.myMovieApp.feature_movieApp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myMovieApp.feature_movieApp.data.api.repository.dao.MovieResultModel

@Database(
    entities = [MovieResultModel::class],
    version = 1
)

abstract class AppDatabase: RoomDatabase() {
    abstract fun movieDao(): MovieDbDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDataBase(context).also { instance = it}
        }
        private fun createDataBase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "movies_db.db"
            ).allowMainThreadQueries()
                .build()
    }
}