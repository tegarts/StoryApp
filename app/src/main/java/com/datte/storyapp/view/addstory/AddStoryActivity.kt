package com.datte.storyapp.view.addstory

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import com.datte.storyapp.R
import com.datte.storyapp.data.utility.createCustomTempFile
import com.datte.storyapp.data.utility.rotateImage
import com.datte.storyapp.data.utility.uriToFile
import com.datte.storyapp.databinding.ActivityAddStoryBinding
import com.datte.storyapp.view.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class AddStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddStoryBinding

    private lateinit var currentPhotoPath: String
    private val addStoryViewModel by viewModels<AddStoryViewModel>()

    private var getFile: File? = null

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                addStoryViewModel.setFile(myFile)
            }
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                val bitmap = BitmapFactory.decodeFile(file.path)
                rotateImage(bitmap, currentPhotoPath).compress(
                    Bitmap.CompressFormat.JPEG,
                    100,
                    FileOutputStream(file)
                )
                addStoryViewModel.setFile(file)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this@AddStoryActivity,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        setupAction()
        setupViewModel()
    }

    private fun setupViewModel() {
        addStoryViewModel.isPosted.observe(this) { isSuccess ->
            if (isSuccess) {
                val dialogBuilder = AlertDialog.Builder(this)
                    .setTitle("Berhasil")
                    .setMessage("Story anda berhasil diupload !")
                    .setPositiveButton("Oke") { _,_ ->
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                    .setOnDismissListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                val dialog = dialogBuilder.create()
                dialog.show()
            } else {
                Snackbar.make(binding.root, "Gagal Diupload", Snackbar.LENGTH_SHORT).show()
            }
        }

        addStoryViewModel.hasUploaded.observe(this) { file ->
            if (file != null) {
                getFile = file
                binding.imagePhoto.setImageBitmap(BitmapFactory.decodeFile(file.path))
                binding.imagePhoto.setPadding(0)
            } else {
                binding.imagePhoto.setImageResource(R.drawable.ic_baseline_image_24)
                binding.imagePhoto.setPadding(32)
            }
        }

        addStoryViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun showLoading(value: Boolean) {
        binding.progressBar.isVisible = value
        binding.buttonAdd.isInvisible = value
        binding.buttonGallery.isEnabled = !value
        binding.buttonCamera.isEnabled = !value
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    "Tidak mendapatkan permission.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun setupAction() {
        with(binding) {
            buttonCamera.setOnClickListener { startTakePhoto() }
            buttonGallery.setOnClickListener { openGallery() }
            buttonAdd.setOnClickListener {
                edAddDescription.clearFocus()
                uploadImage()
            }
        }
    }

    private fun uploadImage() {
        when {
            getFile == null -> Snackbar.make(binding.root, getString(R.string.input_image), Snackbar.LENGTH_SHORT).show()
            binding.edAddDescription.text.isNullOrBlank() -> {
                binding.edAddDescription.error = getString(R.string.error_description)
                binding.edAddDescription.requestFocus()
            }
            else -> {
                val file = getFile as File
                val description = binding.edAddDescription.text.toString()
                addStoryViewModel.postStory(file, description)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoUri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.datte.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            launcherIntentCamera.launch(intent)
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}