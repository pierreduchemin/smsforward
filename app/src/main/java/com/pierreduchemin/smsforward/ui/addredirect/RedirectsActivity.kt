package com.pierreduchemin.smsforward.ui.addredirect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.App.Companion.APPSCOPE
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.di.ActivityModule
import com.pierreduchemin.smsforward.ui.about.AboutActivity
import kotlinx.android.synthetic.main.redirects_activity.*
import toothpick.ktp.KTP


const val REQUEST_CODE_SMS_PERMISSION = 9954

class RedirectsActivity : AppCompatActivity() {

    companion object {
        private val TAG by lazy { RedirectsActivity::class.java.simpleName }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirects_activity)

        setSupportActionBar(toolbar)

        KTP.openRootScope()
            .openSubScope(APPSCOPE)
            .openSubScope(this)
            .installModules(ActivityModule(this))
            .inject(this)

        supportFragmentManager.findFragmentById(R.id.mainContent) as RedirectsFragment?
            ?: RedirectsFragment.newInstance().also {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.mainContent, it)
                }.commit()
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.miAbout) {
            startActivity(Intent(this, AboutActivity::class.java))
        }
        return super.onOptionsItemSelected(item)
    }
}
