package com.pierreduchemin.smsforward.ui.redirectlist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.ui.redirectlist.RedirectListFragment

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
