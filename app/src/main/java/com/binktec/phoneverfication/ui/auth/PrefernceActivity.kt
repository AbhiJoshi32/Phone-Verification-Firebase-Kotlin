package com.binktec.phoneverfication.ui.auth

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import com.binktec.phoneverfication.R
import com.binktec.phoneverfication.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_prefernce.*

class PrefernceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prefernce)
        val countryAdapter = ArrayAdapter.createFromResource(this,R.array.country,android.R.layout.simple_spinner_item)
        countryAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        country_spinner.adapter = countryAdapter


        val languageAdapter = ArrayAdapter.createFromResource(this,R.array.language,android.R.layout.simple_spinner_item)
        languageAdapter .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        language_spinner.adapter = languageAdapter
        continue_btn.setOnClickListener{
            val i = Intent(this,MainActivity::class.java)
//            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(i)
            finish()
        }
    }
}
