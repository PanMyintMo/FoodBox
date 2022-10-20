package com.pan.foodbox.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.pan.foodbox.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : BaseActivity() {


    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        updateUI(false)
        binding.textLogin.setOnClickListener {
            checkLoginDetail()
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, VerifyActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun updateUI(login: Boolean) {
        binding.textProgress.visibility = if (login) View.VISIBLE else View.GONE
        binding.textProgress.isIndeterminate = false
        binding.textLogin.isEnabled=!login

    }

    private fun updatedUI(isCheck: Boolean) {
        binding.textProgress.visibility = if (isCheck) View.VISIBLE else View.GONE

    }

    private fun checkLoginDetail() {
        val email = binding.textLoginMail.text.toString()
        val pass = binding.textPass.text.toString()

        if (TextUtils.isEmpty(email)) {
            binding.textLoginMail.error = "Email is required"
            binding.textPass.requestFocus()
        }
        if (TextUtils.isEmpty(pass)) {
            binding.textPass.error = "Password is required"
            binding.textLoginMail.requestFocus()
        } else {
            loginUser(email, pass)
        }
    }

    private fun loginUser(email: String, pass: String) {
        if (checkNetwork()) {
            updateUI(true)
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        updatedUI(true)
                        updateUI(false)
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {

                        updatedUI(false)
                        Toast.makeText(
                            this@LoginActivity,
                            "Check your internet connection",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }
                .addOnFailureListener {
                    Snackbar.make(
                        binding.root, it.message.toString(), Snackbar.LENGTH_SHORT
                    ).show()
                }
        } else {

            Toast.makeText(this@LoginActivity, "Check your internet connection", Toast.LENGTH_SHORT)
                .show()
            updatedUI(true)

        }
    }

    private fun checkNetwork(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)

        return networkCapabilities != null && networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}