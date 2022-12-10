package de.mow2.towerdefense.controller

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.SoundManager.musicSetting
import de.mow2.towerdefense.controller.SoundManager.soundPool
import de.mow2.towerdefense.controller.SoundManager.soundSetting
import de.mow2.towerdefense.controller.fragments.PopupFragment
import de.mow2.towerdefense.controller.helper.BitmapPreloader
import de.mow2.towerdefense.controller.helper.GameState
import de.mow2.towerdefense.databinding.ActivityMainBinding
import de.mow2.towerdefense.model.core.GameManager

/**
 * This class is the main entry point
 */
class MainActivity : AppCompatActivity() {
    private val gameState = GameState(this)
    private val fm = supportFragmentManager
    private var dialogPopup = PopupFragment()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        //preload all images
        if (!BitmapPreloader.bitmapsLoaded) {
            val graphics =
                PreferenceManager.getDefaultSharedPreferences(this).getString("quality_pref", "Low")
            BitmapPreloader(resources).preloadGraphics(graphics)
        }
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        // decides if resume game button should be shown
        if (gameState.defineFile(this).exists()) {
            binding.resumeGameBtn.visibility = View.VISIBLE
        }
        // loads music and sounds and sets them based on saved preferences
        SoundManager.loadPreferences(this)
        SoundManager.playSounds()
        SoundManager.loadSounds(this)
        SoundManager.initMediaPlayer(this, R.raw.eight_bit_adventure_loop)
        if (!soundSetting) {
            soundPool.release()
        }
        if (!musicSetting) {
            SoundManager.pauseMusic()
        }
    }

    override fun onPause() {
        super.onPause()
        // releases MediaPlayer to make changing music possible
        SoundManager.mediaPlayer.release()
    }

    override fun onDestroy() {
        super.onDestroy()
        // releases MediaPlayer and SoundPool onDestroy
        soundPool.release()
        SoundManager.mediaPlayer.release()
    }

    /**
     * Starts GameActivity called by a Button
     */
    fun startGame(view: View) {
        GameManager.reset()
        startActivity(Intent(this, GameActivity::class.java))
    }

    /**
     * Load saved game
     */
    fun resumeGame(view: View) {
        GameManager.reset()
        val intent = Intent(this, GameActivity::class.java)
        intent.putExtra("loadGame", true)
        startActivity(intent)
    }

    /**
     * Loads Dialog Fragment popUpWindow on button Click based on button id
     */
    fun popUpButton(view: View) {
        when (view.id) {
            R.id.about_button -> {
                dialogPopup.show(fm, "aboutDialog")
            }
            R.id.preference_button -> {
                dialogPopup.show(fm, "settingsDialog")
            }
        }
    }
}