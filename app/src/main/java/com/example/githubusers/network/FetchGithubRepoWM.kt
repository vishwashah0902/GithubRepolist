package com.example.githubusers.network

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.githubusers.data.db.AppDB
import com.example.githubusers.data.model.TrendingRepos
import com.example.githubusers.presentation.Event
import com.example.githubusers.repository.UserListRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FetchGithubRepoWM(
    private val context: Context,
    private val params: WorkerParameters
) :
    CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val appDB = AppDB.getInstance(context, CoroutineScope(Dispatchers.IO))
        // Do the work here--in this case, upload the images.
        var response: TrendingRepos
        appDB.userListDao().deleteAll()

        withContext(Dispatchers.IO) {
            response = UserListRepo(
                appDB.userListDao(),
                NetworkConnectionInterceptor(context)
            ).loadRepoList()
        }

        for (gitReponse in response.items) {
            UserListRepo(appDB.userListDao(), NetworkConnectionInterceptor(context)).insert(
                gitReponse.owner
            )
        }

        Log.e("workmanager", "workmanager")

        // Indicate whether the work finished successfully with the Result
        return Result.success()
    }
}
