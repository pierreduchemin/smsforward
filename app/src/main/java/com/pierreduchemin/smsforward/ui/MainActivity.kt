package com.pierreduchemin.smsforward.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private val ui: MainActivityBinding by lazy { MainActivityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui.root)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment).navigateUp() || super.onSupportNavigateUp()
    }
}
