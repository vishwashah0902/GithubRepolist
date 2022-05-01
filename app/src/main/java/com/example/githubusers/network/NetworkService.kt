package com.example.githubusers.network


import com.example.githubusers.data.model.TrendingRepos
import retrofit2.http.*

interface NetworkService {
    /*@GET("search/repositories?q=created&sort=stars&order=desc&date=-v-7d")
    suspend fun userListRequest(
    ): List<GithubUsers>*/

    @GET("search/repositories?q=created&sort=stars&order=desc&date=-v-7d")
    suspend fun userListRequest(
    ): TrendingRepos
}

