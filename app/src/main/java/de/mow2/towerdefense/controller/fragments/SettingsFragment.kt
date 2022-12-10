package de.mow2.towerdefense.controller.fragments

import android.os.Bundle
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager

/**
 * class SettingsFragment sets layout.xml for preferences
 * sets onPreferenceChangeListener for musicSettings, soundSettings and image quality settings for checkbox functionality
 * */
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

        // background music // MediaPlayer
        findPreference<CheckBoxPreference>("music_pref")?.setOnPreferenceChangeListener { _, newValue ->
            if (newValue as Boolean) {
                SoundManager.resumeMusic()
            } else {
                SoundManager.pauseMusic()
            }
            true
        }

        // sound sprites / SoundPool
        findPreference<CheckBoxPreference>("sound_pref")?.setOnPreferenceChangeListener { _, newValue ->
            if (!(newValue as Boolean)) {
                SoundManager.soundPool.release()
            } else {
                SoundManager.playSounds()
                context?.let { SoundManager.loadSounds(it) }
            }
            true
        }

        // image quality
        findPreference<ListPreference>("quality_pref")?.setOnPreferenceChangeListener { _, newValue ->
            when (newValue) {
                "Low" -> {} //low
                "Avg" -> {} //avg
                "High" -> {} //high
            }
            true
        }
    }
}