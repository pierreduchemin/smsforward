package com.pierreduchemin.smsforward.redirects

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.pierreduchemin.smsforward.di.ActivityModule
import com.pierreduchemin.smsforward.App.Companion.APPSCOPE
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.about.AboutActivity
import com.pierreduchemin.smsforward.data.ForwardModelRepository
import kotlinx.android.synthetic.main.redirects_activity.*
import toothpick.ktp.KTP
import toothpick.ktp.delegate.inject


const val REQUEST_CODE_SMS_PERMISSION = 9954

class RedirectsActivity : AppCompatActivity() {

    companion object {
        private val TAG by lazy { RedirectsActivity::class.java.simpleName }
    }

    private val forwardModelRepository: ForwardModelRepository by inject()

    private lateinit var presenter: RedirectsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirects_activity)

        setSupportActionBar(toolbar)

        KTP.openRootScope()
            .openSubScope(APPSCOPE)
            .openSubScope(this)
            .installModules(ActivityModule(this))
            .inject(this)

        val view = supportFragmentManager.findFragmentById(R.id.mainContent) as RedirectsFragment?
            ?: RedirectsFragment.newInstance().also {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.mainContent, it)
                }.commit()
            }

        presenter = RedirectsPresenter(this, forwardModelRepository, view)

        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECEIVE_SMS),
            REQUEST_CODE_SMS_PERMISSION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_SMS_PERMISSION) {
            Log.i(TAG, "REQUEST_CODE_SMS_PERMISSION ok")
            presenter.onViewCreated()
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
