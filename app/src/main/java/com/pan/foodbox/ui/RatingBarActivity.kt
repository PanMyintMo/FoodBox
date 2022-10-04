package com.pan.foodbox.ui

import android.os.Bundle
import android.view.MenuItem
import com.pan.foodbox.databinding.ActivityRatingBarBinding

class RatingBarActivity : BaseActivity() {

    private lateinit var  binding: ActivityRatingBarBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityRatingBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title ="Rating Bar"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        binding.btnSubmit.setOnClickListener {
            binding.txtRate.text = "Your rating is :" + binding.ratingBar.rating
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}