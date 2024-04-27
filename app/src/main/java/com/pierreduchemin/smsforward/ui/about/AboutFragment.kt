package com.pierreduchemin.smsforward.ui.about

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.pierreduchemin.smsforward.BuildConfig
import com.pierreduchemin.smsforward.R
import com.pierreduchemin.smsforward.databinding.AboutFragmentBinding
import com.pierreduchemin.smsforward.utils.SdkUtils
import mehdi.sakout.aboutpage.AboutPage
import mehdi.sakout.aboutpage.Element

class AboutFragment : Fragment() {

    private lateinit var ui: AboutFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreate(savedInstanceState)
        ui = AboutFragmentBinding.inflate(layoutInflater, container, false)

        setupToolbar()
        loadAbout()

        return ui.root
    }

    private fun setupToolbar() {
        val appCompatActivity = requireActivity() as AppCompatActivity
        appCompatActivity.setSupportActionBar(ui.toolbar.toolbar)
        appCompatActivity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ui.toolbar.ivHelp.isVisible = false
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

        val developersElement = Element(
            getString(R.string.about_info_developers),
            R.drawable.ic_developer_24dp
        )
            .setAutoApplyIconTint(false)

        val aboutPage = AboutPage(requireContext())
            .enableDarkMode(SdkUtils.isDarkTheme(resources))
            .isRTL(false)
            .setImage(R.mipmap.ic_launcher)
            .setDescription(getString(R.string.about_info_app_description))
            .addItem(versionElement)
            .addItem(licensesElement)
            .addItem(gitlabElement)
            .addItem(developersElement)
            .create()

        ui.flAbout.addView(aboutPage)
    }
}