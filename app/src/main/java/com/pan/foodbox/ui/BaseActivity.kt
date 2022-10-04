package com.pan.foodbox.ui


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      //  supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

  /*  override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val intent = Intent(this@BaseActivity, MainActivity::class.java)
            startActivity(intent)

        }
        //  onBackPressed()
        return super.onOptionsItemSelected(item)
    }*/

    fun toPx(dp: Int): Int {
        val scale = this.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}