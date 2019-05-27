package com.pierreduchemin.smsforward.redirects

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.R


class RedirectsActivity : AppCompatActivity() {

    private lateinit var redirectsPresenter: RedirectsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.redirects_activity)

        val redirectsFragment = supportFragmentManager.findFragmentById(R.id.mainContent) as RedirectsFragment?
            ?: RedirectsFragment.newInstance().also {
                supportFragmentManager.beginTransaction().apply {
                    replace(R.id.mainContent, it)
                }.commit()
            }

        redirectsPresenter = RedirectsPresenter(this, redirectsFragment)
    }

    override fun onStart() {
        super.onStart()
        redirectsPresenter.onStart()
    }

    override fun onStop() {
        super.onStop()
        redirectsPresenter.onStop()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        redirectsPresenter.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
