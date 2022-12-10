package de.mow2.towerdefense.controller

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.SoundPool
import androidx.preference.PreferenceManager
import de.mow2.towerdefense.R

/**
 * class SoundManager
 * for playing background music and in-game Sound Sprites
 * contains MediaPlayer and SoundPool and initializes music & sound settings
 * */

enum class Sounds(var id: Int) {
    DESTROYER(0), WAVE(0), GAMEOVER(0), BUILD(0), CREEPDEATH(0), TOWERDESTROY(0), LIVELOSS(0), MAGICSHOT(
        0
    ),
    ARROWSHOT(0), AOESHOT(0), SLOWSHOT(0), BOSSLEVEL(0)
}

object SoundManager {
    lateinit var mediaPlayer: MediaPlayer
    lateinit var soundPool: SoundPool
    var musicSetting: Boolean = true
    var soundSetting: Boolean = true

    /**
     * SoundPool functions:
     * playSounds -> initializes SoundPool with settings
     * loadSounds -> load audio streams from R
     * variables in enum class with integer for ID to make code readable
     */
    fun playSounds() {
        val audioattributes: AudioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioattributes)
            .build()
    }

    fun loadSounds(context: Context) {
        Sounds.GAMEOVER.id = soundPool.load(context, R.raw.gameover, 1)
        Sounds.BUILD.id = soundPool.load(context, R.raw.building_sound, 1)
        Sounds.TOWERDESTROY.id = soundPool.load(context, R.raw.tower_explosion, 1)
        Sounds.LIVELOSS.id = soundPool.load(context, R.raw.lost_live, 1)
        Sounds.CREEPDEATH.id = soundPool.load(context, R.raw.creep_death, 1)
        Sounds.MAGICSHOT.id = soundPool.load(context, R.raw.magic_shot, 2)
        Sounds.SLOWSHOT.id = soundPool.load(context, R.raw.slow_shot, 2)
        Sounds.ARROWSHOT.id = soundPool.load(context, R.raw.arrow_shot, 2)
        Sounds.AOESHOT.id = soundPool.load(context, R.raw.aoe_shot, 2)
        Sounds.WAVE.id = soundPool.load(context, R.raw.wave_success, 1)
        Sounds.DESTROYER.id = soundPool.load(context, R.raw.destroyer_sound, 1)
        Sounds.BOSSLEVEL.id = soundPool.load(context, R.raw.boss_level_sound, 1)
    }

    /**
     * MediaPlayer functions:
     * initMediaPlayer -> initializes MediaPlayer, starts it with setting for Loop
     * pauseMusic & resumeMusic enables to start and stop background music
     */
    fun initMediaPlayer(context: Context, song: Int) {
        mediaPlayer = MediaPlayer.create(context, song)
        mediaPlayer.isLooping = true
        mediaPlayer.start()
    }

    fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        }
    }

    fun resumeMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
        }
    }

    /**
     * function loadPreferences sets music and soundSettings as Preferences for checkbox
     */
    fun loadPreferences(context: Context) {
        val musicPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        musicSetting = musicPreferences.getBoolean("music_pref", true)
        soundSetting = musicPreferences.getBoolean("sound_pref", true)
    }

}