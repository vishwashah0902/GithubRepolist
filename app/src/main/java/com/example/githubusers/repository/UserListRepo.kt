package com.example.githubusers.repository

import com.example.githubusers.data.db.dao.UserListDao
import com.example.githubusers.data.db.entities.GithubRepo
import com.example.githubusers.data.model.TrendingRepos
import com.example.githubusers.network.NetworkConnectionInterceptor

import com.example.githubusers.retrofit.RetrofitClient

class UserListRepo(private val dao: UserListDao,
                   private val networkConnectionInterceptor: NetworkConnectionInterceptor) {
    suspend fun loadRepoList(): TrendingRepos {
        return RetrofitClient(networkConnectionInterceptor).apiInterface.userListRequest()
    }

    suspend fun insert(githubRepo: GithubRepo) {
        dao.insert(githubRepo)
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}