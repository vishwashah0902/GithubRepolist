package com.example.githubusers.adapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.githubusers.R
import com.example.githubusers.data.db.entities.GithubRepo
import com.example.githubusers.databinding.GitHubUserItemBinding
import com.example.githubusers.presentation.githubuserlist.GithubUserVM
import java.util.*

class GithubUserListRecyclerViewAdapter(
    private val userList: List<GithubRepo>,
    private val clickListener: (GithubRepo, Int) -> Unit,
    private val viewModel: GithubUserVM,
    private var context: Context
) : RecyclerView.Adapter<GithubUserListRecyclerViewAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: GitHubUserItemBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.git_hub_user_item, parent, false)
        context = parent.context
        return RecyclerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val currentItem = userList[position]
        holder.bind(currentItem, viewModel)
        val userListNew: GithubRepo = userList[position]
        holder.binding.githubUserList = userListNew
        holder.binding.sno.text = userListNew.login.toUpperCase(Locale.getDefault()).substring(0,1).toString()
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.OVAL
        shape.cornerRadii = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        val random = Random()
        val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        shape.setColor(color)
        holder.binding.sno.background = shape
        holder.binding.customerName.text = userListNew.login
        /*holder.binding.view.setOnClickListener {
            val intent = Intent(context, GithubUserDetails::class.java)
            intent.putExtra("id", userListNew.id)
            context.startActivity(intent)
        }*/
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class RecyclerViewHolder(val binding: GitHubUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(userList: GithubRepo, viewModel: GithubUserVM) {
            binding.view.setOnClickListener {
                clickListener(userList, adapterPosition)
            }
        }
    }
}