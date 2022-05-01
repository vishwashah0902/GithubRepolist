package com.example.githubusers.presentation.githubuserlist

import android.app.Application
import android.util.Log
import androidx.databinding.Observable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubusers.data.db.AppDB
import com.example.githubusers.data.db.entities.GithubRepo
import com.example.githubusers.data.model.TrendingRepos
import com.example.githubusers.presentation.Event
import com.example.githubusers.repository.UserListRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GithubUserVM(private val userListRepo: UserListRepo) : ViewModel(),Observable {
    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    var userList =
        ArrayList<GithubRepo>()

    fun createDB(application: Application) {
        val appDB = AppDB.getInstance(application, viewModelScope)
    }

    fun syncList() {
        var response: TrendingRepos
        viewModelScope.launch {
            deleteAll()

            withContext(Dispatchers.IO) {
                response = userListRepo.loadRepoList()
            }

            for(gitReponse in response.items){
                userListRepo.insert(gitReponse.owner)
                userList.add(gitReponse.owner)
            }
            statusMessage.value = Event("Data Synced")
        }
    }

    fun deleteAll(): Job = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            userListRepo.deleteAll()
        }
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}