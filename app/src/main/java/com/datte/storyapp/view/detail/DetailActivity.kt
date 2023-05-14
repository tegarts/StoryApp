package com.datte.storyapp.view.detail

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.datte.storyapp.data.response.ListStoryItem
import com.datte.storyapp.databinding.ActivityDetailBinding
import com.google.android.material.snackbar.Snackbar

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val detailStoryViewModel by viewModels<DetailViewModel>()

    private val userId by lazy { intent.getStringExtra(USER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupView()
        setupViewModel()
    }

    override fun onResume() {
        super.onResume()
        detailStoryViewModel.getDetailStory(userId as String)
    }


    private fun setupView() {
        Glide.with(this@DetailActivity)
            .load(intent.getStringExtra(PHOTO_URL))
            .into(binding.ivDetailPhoto)
        detailStoryViewModel.getDetailStory(userId as String)
    }

    private fun setupViewModel() {
        detailStoryViewModel.detailStory.observe(this) {
            setDetailStory(it)
        }

        detailStoryViewModel.loadingScreen.observe(this) {
            showLoading(it)
        }

        detailStoryViewModel.snackBarText.observe(this) {
            it.getContentIfNotHandled().let { text ->
                Snackbar.make(binding.root, text.toString(), Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(value: Boolean) {
        with(binding) {
            progressBar.isVisible = value
            tvDetailName.isVisible = !value
            tvDetailDescription.isVisible = !value
        }
    }

    private fun setDetailStory(story: ListStoryItem) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
        }
    }

    companion object {
        @JvmStatic
        fun start(context: Context, photoUrl: String, userId: String, pair: Pair<ImageView, String>) {
            val starter = Intent(context, DetailActivity::class.java)
                .putExtra(USER_ID, userId)
                .putExtra(PHOTO_URL, photoUrl)

            val optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, androidx.core.util.Pair(pair.first as View, pair.second))
            context.startActivity(starter, optionsCompat.toBundle())
        }

        private const val USER_ID = "userId"
        private const val PHOTO_URL = "photo_url"
    }
}