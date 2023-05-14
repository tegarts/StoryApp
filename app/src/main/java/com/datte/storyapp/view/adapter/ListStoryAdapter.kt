package com.datte.storyapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.datte.storyapp.R
import com.datte.storyapp.data.response.ListStoryItem
import com.datte.storyapp.databinding.StoryCardBinding
import com.datte.storyapp.view.detail.DetailActivity

class ListStoryAdapter (
    private val listStory: List<ListStoryItem>
) : RecyclerView.Adapter<ListStoryAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = StoryCardBinding.bind(itemView)
        fun bind(data: ListStoryItem) {

            val userId = data.id
            val username = data.name
            val desc = data.description
            val photoUrl = data.photoUrl

            binding.tvItemUsername.text = username
            binding.tvItemDescription.text = desc

                Glide.with(itemView)
                    .load(photoUrl)
                    .into(binding.ivItemName)

                itemView.setOnClickListener {
                    DetailActivity.start(
                        itemView.context,
                        photoUrl as String,
                        userId as String,
                        Pair(binding.ivItemName, "ivItemName")
                    )
                }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.story_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listStory[position])
    }

    override fun getItemCount(): Int = listStory.size
}