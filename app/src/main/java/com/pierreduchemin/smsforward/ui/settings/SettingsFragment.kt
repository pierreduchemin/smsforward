package com.pierreduchemin.smsforward.ui.settings
import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.pierreduchemin.smsforward.R


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }
}
