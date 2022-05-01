package com.example.githubusers.presentation.githubuserlist

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import androidx.work.PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS
import androidx.work.PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS
import com.example.githubusers.R
import com.example.githubusers.adapter.GithubUserListRecyclerViewAdapter
import com.example.githubusers.data.db.AppDB
import com.example.githubusers.data.db.entities.GithubRepo
import com.example.githubusers.databinding.ActivityGitHubUserListBinding
import com.example.githubusers.databinding.GitHubUserDetailsBinding
import com.example.githubusers.network.FetchGithubRepoWM
import com.example.githubusers.network.NetworkConnectionInterceptor
import com.example.githubusers.repository.UserListRepo
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class GitHubUserList : AppCompatActivity() {
    private lateinit var binding: ActivityGitHubUserListBinding
    private lateinit var githubUserVM: GithubUserVM
    lateinit var networkConnectionInterceptor: NetworkConnectionInterceptor
    private lateinit var adapter: GithubUserListRecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_git_hub_user_list)
        val dao =
            AppDB.getInstance(this, CoroutineScope(Dispatchers.IO)).userListDao()
        val repo =
            UserListRepo(dao, networkConnectionInterceptor)
        val factory = GithubUserVMFactory(repo)
        githubUserVM = ViewModelProvider(this, factory).get(GithubUserVM::class.java)
        binding.githubUserVM = githubUserVM
        binding.lifecycleOwner = this
        githubUserVM.createDB(application)
        refreshList()

        binding.refreshLayout.setOnRefreshListener {
            refreshList()
            binding.refreshLayout.isRefreshing = false
        }
    }

    private fun refreshList() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val fetchRequest = PeriodicWorkRequest.Builder(
            FetchGithubRepoWM::class.java,15 , TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager
            .getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "uniqueWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                fetchRequest
            )

        if (isNetworkConnected()) {
            binding.pBar.visibility = View.VISIBLE
            binding.userListRecyclerView.isEnabled = false
            githubUserVM.syncList()
            githubUserVM.message.observe(this, {
                it.getContentIfNotHandled()?.let { it1 ->
                    binding.pBar.visibility = View.GONE
                    binding.root.snackBar(it1)
                    binding.userListRecyclerView.isEnabled = true
                    initRecyclerView()
                }
            })
        } else {
            Toast.makeText(
                applicationContext,
                "For user list please enable data connectivity",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun initRecyclerView() {
        binding.userListRecyclerView.layoutManager = GridLayoutManager(this, 1)
        binding.userListRecyclerView.setHasFixedSize(true)
        adapter = GithubUserListRecyclerViewAdapter(
            githubUserVM.userList,
            { userList: GithubRepo, position: Int ->
                clickEvents(
                    userList,
                    position
                )
            },
            githubUserVM,
            this
        )
        binding.userListRecyclerView.adapter = adapter
    }

    private fun clickEvents(
        userList: GithubRepo,
        position: Int
    ) {
        initAlertDialog(userList)
    }

    private fun initAlertDialog(
        userList: GithubRepo
    ) {
        val view = DataBindingUtil.inflate<GitHubUserDetailsBinding>(
            LayoutInflater.from(this),
            R.layout.git_hub_user_details,
            null,
            false
        )
        view.githubUserList = userList

        val builder = AlertDialog.Builder(this)
        builder.setView(view.root)
        builder.setCancelable(true)

        val alertDialog: AlertDialog = builder.create()
        alertDialog.window!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        );
        alertDialog.show()
    }

    fun View.snackBar(message: String) {
        val snackBar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
        snackBar.show()
    }

    private fun isNetworkConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                connectivityManager.activeNetwork
            } else {
                TODO("VERSION.SDK_INT < M")
            }
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}