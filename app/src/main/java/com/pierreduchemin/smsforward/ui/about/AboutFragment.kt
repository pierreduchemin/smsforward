package com.pierreduchemin.smsforward.ui.about

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.pierreduchemin.smsforward.BuildConfig
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.AboutActivityBinding
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutFragment : Fragment() {

    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var ui: AboutActivityBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ui = AboutActivityBinding.inflate(layoutInflater)

        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appCompatActivity.supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24)

        loadAbout()

        return ui.root
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
            R.drawable.ic_gitlab_24dp
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
            R.drawable.ic_gnu_24dp
        )
            .setAutoApplyIconTint(false)
            .setIntent(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.gnu.org/licenses/gpl-3.0.txt")
                )
            )

        val aboutPage = AboutPage(requireContext())
            .enableDarkMode(isDarkTheme())
            .isRTL(false)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.about_info_app_description))
            .addItem(versionElement)
            .addItem(licensesElement)
            .addItem(gitlabElement)
            .create()

        ui.flAbout.addView(aboutPage)
    }

    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
}
