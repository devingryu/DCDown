package com.ibd.dcdown.main

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.ibd.dcdown.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}