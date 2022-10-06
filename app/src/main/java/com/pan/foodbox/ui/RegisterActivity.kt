package com.pan.foodbox.ui

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.pan.foodbox.R
import com.pan.foodbox.databinding.ActivityRegisterBinding
import com.pan.foodbox.models.User

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private var uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.btnRegister.setOnClickListener {
            checkDetailRegister()
        }

        binding.imgProfile.setOnClickListener {
            pickImage()
        }
        updateUI(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK

                startActivity(intent)

            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateUI(isCheck: Boolean) {
        binding.progressbar.visibility = if (isCheck) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isCheck

    }

    private fun checkDetailRegister() {
        val name = binding.editName.text.toString()
        val email = binding.editMail.text.toString()
        val pass = binding.editPass.text.toString()
        val confirmPass = binding.confirmPassword.text.toString()


        if (TextUtils.isEmpty(name)) {
            binding.editName.error = "Name is required"
            binding.editName.requestFocus()
        } else if (TextUtils.isEmpty(email)) {
            binding.editMail.error = "Email is required"
            binding.editMail.requestFocus()
        } else if (TextUtils.isEmpty(pass)) {
            binding.editPass.error = "Password is required"
            binding.editPass.requestFocus()
        } else if (TextUtils.isEmpty(confirmPass)) {
            binding.confirmPassword.error = "Confirm Password is required"
            binding.confirmPassword.requestFocus()
        } else {
            if (pass != confirmPass) {
                Toast.makeText(
                    this@RegisterActivity,
                    "Password are not matched",
                    Toast.LENGTH_SHORT
                ).show()
                binding.editPass.requestFocus()
                binding.confirmPassword.requestFocus()
            } else {
                if (checkNetwork()) {
                        startRegister(name, email, pass)
                    }
                else{
                    Toast.makeText(this@RegisterActivity, "Check your internet connection", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

    private fun startRegister(name: String, email: String, pass: String) {

            updateUI(true)
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    updateUI(false)
                    if (it.isSuccessful) {
                        it.result.user?.let { user ->
                            uploadProfileImage(user.uid, name, email)
                        }
                    }
                }
        }




    private fun pickImage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = "image/*"
        activityResult.launch(Intent.createChooser(intent, "Choose Image"))
    }

    private val activityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                updateProfileImage(result.data!!)
            }
        }

    private fun updateProfileImage(data: Intent) {
        data.data.let { imageUri ->
            uri = imageUri
            binding.imgProfile.setImageBitmap(getProfileBitmap(uri))

        }
    }

    private fun getProfileBitmap(uri: Uri?): Bitmap {
        return if (uri != null) {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } else {
            BitmapFactory.decodeResource(resources, R.drawable.user_image)
        }
    }

    private fun uploadProfileImage(
        uid: String,
        name: String,
        email: String,

        ) {

        val bitmap = getProfileBitmap(uri)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val imageByte = stream.toByteArray()
        val profilePath = "profiles/$uid/profile.jpg"
        val metaData = storageMetadata {
            contentType = "image/jpeg"
        }

        val profileReference = Firebase.storage.reference.child(profilePath)
        profileReference.putBytes(imageByte, metaData)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    addUserToFirestore(
                        User(
                            uid = uid,
                            name = name,
                            imageProfilePath = profilePath,
                            email = email
                        )
                    )
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "Register Fail", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addUserToFirestore(user: User) {
        val fireStore = Firebase.firestore
        fireStore.collection("users")
            .document(user.uid)
            .set(user)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this@RegisterActivity, "$it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkNetwork(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities != null && networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}