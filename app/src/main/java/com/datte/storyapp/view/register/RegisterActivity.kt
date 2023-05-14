package com.datte.storyapp.view.register

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowInsets
import android.view.WindowManager
import com.datte.storyapp.R
import com.datte.storyapp.databinding.ActivityRegisterBinding
import com.datte.storyapp.view.login.LoginActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<RegisterViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupViewModel()
        setupAction()
    }

    @Suppress("DEPRECATION")
    private fun setupView() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        supportActionBar?.hide()
    }

    private fun setupViewModel() {
        registerViewModel.register.observe(this) { isSuccess ->
            if (isSuccess) {
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Registrasi Berhasil")
                    .setMessage("Akun anda berhasil didaftar. Silahkan Login Terlebih dahulu untuk mengakses daftar story")
                    .setPositiveButton("Oke") { _,_ ->
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                    .setOnDismissListener {
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
            }
        }

        registerViewModel.snackbarText.observe(this) { text ->
            when {
                text.contains("taken") -> {
                    binding.emailEditText.error = getString(R.string.email_created)
                    binding.emailEditText.requestFocus()
                }
                text.contains("must be a valid email") -> {
                    binding.emailEditText.error = getString(R.string.email_must_valid)
                    binding.emailEditText.requestFocus()
                }
                text.contains("Password must be at least 8 characters long") -> {
                    binding.passwordEditText.error = getString(R.string.password_invalid_input)
                    binding.passwordEditText.requestFocus()
                }
                else -> Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
    }

    private fun setupAction() {
            binding.loginButton.setOnClickListener {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            binding.registerButton.setOnClickListener {
                val name = binding.nameEditText.text.toString()
                val email = binding.emailEditText.text.toString()
                val password = binding.passwordEditText.text.toString()

                registerViewModel.signUp(name, email, password)
            }

    }
}