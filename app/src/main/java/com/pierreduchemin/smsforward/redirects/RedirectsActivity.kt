package com.pierreduchemin.smsforward.redirects

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.R


class RedirectsActivity : AppCompatActivity() {

    private lateinit var presenter: RedirectsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirects_activity)

        val view = supportFragmentManager.findFragmentById(R.id.mainContent) as RedirectsFragment?
            ?: RedirectsFragment.newInstance().also {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.mainContent, it)
                }.commit()
            }

        presenter = RedirectsPresenter(this, view)
    }

    override fun onStart() {
        super.onStart()
        presenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        presenter.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
