package com.example.githubusers.data.db.dao

import androidx.room.*
import com.example.githubusers.data.db.entities.GithubRepo

@Dao
interface UserListDao {
    @Insert
    suspend fun insert(githubUsers: GithubRepo)

    @Query("DELETE from github_users")
    suspend fun deleteAll()
}