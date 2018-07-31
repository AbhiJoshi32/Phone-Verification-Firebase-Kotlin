package com.binktec.phoneverfication.ui

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.binktec.phoneverfication.R
import com.binktec.phoneverfication.ui.auth.AuthActivity
import java.util.*
import kotlin.concurrent.timerTask

class SplashActivity : AppCompatActivity() {
    private val delay = 1 //1 sec delay
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        Timer().schedule(timerTask{
            nextScreen()
        },delay*1000L)
    }

    private fun nextScreen() {
        startActivity(Intent(this,AuthActivity::class.java))
        finish()
    }
}
