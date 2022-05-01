package com.example.githubusers.presentation.githubuserlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubusers.repository.UserListRepo


class GithubUserVMFactory(private val repository: UserListRepo) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GithubUserVM::class.java)) {
            return GithubUserVM(repository) as T
        }

        throw IllegalArgumentException("Unknown View Model Class")
    }
}