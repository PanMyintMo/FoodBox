package com.pan.foodbox

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.PopupMenu
import android.widget.Toast
import com.pan.foodbox.databinding.ActivitySettingBinding
import com.pan.foodbox.ui.BaseActivity
import com.pan.foodbox.ui.MainActivity

class SettingActivity : BaseActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: ActivitySettingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Setting"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreferences = getSharedPreferences("preference", Context.MODE_PRIVATE)

        if (sharedPreferences.getString("lan", "en").equals("en")) {
            binding.language.text = "English"
        } else {
            binding.language.text = "Myanmar"
        }
        binding.edit.setOnClickListener {

            val popupMenu = PopupMenu(this, binding.edit)
            popupMenu.menuInflater.inflate(R.menu.languages_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.eng -> {
                        binding.language.text = "English"
                        sharedPreferences.edit().putString("lan", "en").commit()

                    }
                    R.id.myanmar -> {
                        binding.language.text = "Myanmar"
                        sharedPreferences.edit().putString("lan", "mm").commit()

                    }

                    else -> Toast.makeText(
                        this@SettingActivity,
                        "Something is wrong",
                        Toast.LENGTH_SHORT
                    ).show()


                }
                true
            })
            popupMenu.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this@SettingActivity, MainActivity::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
