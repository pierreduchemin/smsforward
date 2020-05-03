package com.pierreduchemin.smsforward.ui.redirectlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.R

class RedirectListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirect_list_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, RedirectListFragment.newInstance())
                .commitNow()
        }
    }
}
