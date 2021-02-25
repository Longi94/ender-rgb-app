package dev.longi.enderrgb.ui.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.*
import dev.longi.enderrgb.R
import java.util.*


class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        initSummary(preferenceScreen)
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        val pref: Preference? = findPreference(key)
        pref?.let { updatePrefSummary(it) }
    }

    private fun initSummary(p: Preference) {
        if (p is PreferenceGroup) {
            val pGrp: PreferenceGroup = p
            for (i in 0 until pGrp.preferenceCount) {
                initSummary(pGrp.getPreference(i))
            }
        } else {
            updatePrefSummary(p)
        }
    }

    private fun updatePrefSummary(p: Preference) {
        if (p is ListPreference) {
            p.setSummary(p.entry)
        }
        if (p is EditTextPreference) {
            if (p.getTitle().toString().toLowerCase(Locale.getDefault()).contains("password")) {
                p.setSummary("******")
            } else {
                p.setSummary(p.text)
            }
        }
        if (p is MultiSelectListPreference) {
            val editTextPref = p as EditTextPreference
            p.setSummary(editTextPref.text)
        }
    }
}