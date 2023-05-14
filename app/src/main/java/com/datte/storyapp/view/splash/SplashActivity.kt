package com.datte.storyapp.view.splash

import android.content.Context
import com.datte.storyapp.model.ViewModelFactory
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.datte.storyapp.databinding.ActivitySplashBinding
import com.datte.storyapp.model.UserPreference
import com.datte.storyapp.view.login.LoginActivity
import com.datte.storyapp.view.main.MainActivity
import kotlinx.coroutines.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val activityScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        var isLogin = false
        val splashViewModel by viewModels<SplashViewModel> {
            ViewModelFactory(
                UserPreference.getInstance(dataStore)
            )
        }

        splashViewModel.getUser().observe(this) { user ->
            isLogin = if(user.isLogin) {
                UserPreference.setToken(user.tokenAuth)
                true
            } else {
                false
            }
        }

        activityScope.launch {
            delay(delaySplash)
            runOnUiThread {
                if(isLogin) {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                } else {
                    startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                }
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activityScope.coroutineContext.cancelChildren()
    }

    companion object {
        private var delaySplash = 1500L
    }
}