package com.example.mycabbooking

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

/**
 * Loading screen activity
 */
class SplashActivity : AppCompatActivity() {
    var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        handler = Handler()
        handler!!.postDelayed({
            val intent: Intent =
                Intent(
                    this@SplashActivity,
                    StartActivity::class.java
                )
            startActivity(intent)
            finish()
        }, 3000)
    }
}