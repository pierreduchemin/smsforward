package com.pierreduchemin.smsforward.ui

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {

    private lateinit var ui: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = MainActivityBinding.inflate(layoutInflater)
        setContentView(ui.root)

        setSupportActionBar(ui.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_redirectlist, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miAbout) {
            startAbout()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun startAbout() {
        findNavController(R.id.nav_host_fragment).navigate(R.id.action_redirectListFragment_to_aboutActivity)
    }
}
