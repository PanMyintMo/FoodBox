package com.pan.foodbox.ui

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.pan.foodbox.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val actionBar = supportActionBar
        actionBar?.hide()
        lifecycleScope.launch {
            delay(2500)
            val sharePreference = getSharedPreferences("success", MODE_PRIVATE)
            val resultData= sharePreference.getBoolean("isRegister",false)
            if (resultData){
                goToMain()
            }
            else{
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
            }
      
        /* if (sharePreference.contains("isRegister")) {
                goToMain()
            }
            else{
                startActivity(Intent(this@SplashActivity,LoginActivity::class.java))
            }*/
        }
    }
    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
