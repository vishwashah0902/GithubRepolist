package com.example.githubusers.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.githubusers.data.db.dao.UserListDao
import com.example.githubusers.data.db.entities.GithubRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Database(
    entities = [GithubRepo::class],
    version = 1
)
abstract class AppDB : RoomDatabase() {
    abstract fun userListDao(): UserListDao

    private class DatabaseCallback(private val scope: CoroutineScope) :
        RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {

                }
            }
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDB? = null
        fun getInstance(context: Context, scope: CoroutineScope): AppDB {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AppDB::class.java,
                        "github_user_db"
                    )
                        .addCallback(DatabaseCallback(scope))
                        .build()

                    INSTANCE = instance
                }

                return instance
            }
        }
    }
}