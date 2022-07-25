package com.pierreduchemin.smsforward.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private val ui: MainActivityBinding by lazy { MainActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
    }
}
