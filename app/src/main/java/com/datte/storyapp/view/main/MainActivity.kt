package com.datte.storyapp.view.main


import com.datte.storyapp.model.ViewModelFactory
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.recyclerview.widget.LinearLayoutManager
import com.datte.storyapp.R
import com.datte.storyapp.data.response.ListStoryItem
import com.datte.storyapp.databinding.ActivityMainBinding
import com.datte.storyapp.model.UserPreference
import com.datte.storyapp.view.adapter.ListStoryAdapter
import com.datte.storyapp.view.addstory.AddStoryActivity
import com.datte.storyapp.view.login.LoginActivity


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(
            UserPreference.getInstance(dataStore)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        binding.root.setOnRefreshListener {
            setupView()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.app_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_story -> startActivity(Intent(this, AddStoryActivity::class.java))
            R.id.logout -> showLogoutDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this).apply {
            setMessage("Anda yakin ingin Logout?")
            setPositiveButton("Ya") { _, _ ->
                logout()
            }
            setNegativeButton("Tidak", null)
            create()
            show()
        }
    }

    private fun logout() {
        mainViewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun setupView() {
        binding.root.isRefreshing = false
        mainViewModel.getAllStories()
    }

    private fun setupViewModel() {
        mainViewModel.listStory.observe(this) {
            setRecycleView(it)
        }
        mainViewModel.loadingScreen.observe(this) {
            showLoading(it)
        }
    }

//    private fun checkLoginStatus() {//cek status login
//        MainViewModel.isLogin().observe(this) { isLogin ->
//            if (!isLogin) {
//                startActivity(Intent(this, LoginActivity::class.java))
//                finish()
//            }
//        }
//    }

//    private fun checkLogin() {
//
//        var isLogin = false
//
//        mainViewModel.getUser().observe(this) { user ->
//            isLogin = if(user.isLogin) {
//                UserPreference.setToken(user.tokenAuth)
//                true
//            } else {
//                false
//            }
//        }
//                if(isLogin) {
//                    startActivity(Intent(this@MainActivity, MainActivity::class.java))
//                } else {
//                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
//                }
//
//    }

    private fun showLoading(value: Boolean) {
        binding.progressBar.isVisible = value
        binding.rvListStory.isVisible = !value
    }

    private fun setRecycleView(list: List<ListStoryItem>) {
        with(binding) {
            val manager = LinearLayoutManager(this@MainActivity)
            rvListStory.apply {
                adapter = ListStoryAdapter(list)
                layoutManager = manager
            }
        }
    }




}