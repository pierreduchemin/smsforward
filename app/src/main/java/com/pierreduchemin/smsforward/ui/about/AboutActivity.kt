package com.pierreduchemin.smsforward.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.pierreduchemin.smsforward.BuildConfig
import com.pierreduchemin.smsforward.R
import kotlinx.android.synthetic.main.activity_about.*
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        loadAbout()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    private fun loadAbout() {
        val versionElement = Element()
            .setTitle(
                getString(
                    R.string.about_info_version,
                    BuildConfig.VERSION_NAME
                )
            )
            .setGravity(Gravity.CENTER_HORIZONTAL)

        val gitlabElement = Element(
            getString(R.string.about_info_gitlab),
            R.drawable.ic_gitlab
        )
            .setAutoApplyIconTint(false)
            .setIntent(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://gitlab.com/pierreduchemin/smsforward")
                )
            )

        val licensesElement = Element(
            getString(R.string.about_info_license),
            R.drawable.ic_gnu
        )
            .setAutoApplyIconTint(false)
            .setIntent(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.gnu.org/licenses/gpl-3.0.txt")
                )
            )

        val aboutPage = AboutPage(this)
            .isRTL(false)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.about_info_app_description))
            .addItem(versionElement)
            .addItem(licensesElement)
            .addItem(gitlabElement)
            .create()

        flAbout.addView(aboutPage)
    }
}