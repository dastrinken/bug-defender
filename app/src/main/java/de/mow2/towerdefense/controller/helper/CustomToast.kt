package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import de.mow2.towerdefense.R
import de.mow2.towerdefense.model.core.GameManager

/**
 * Custom toast message to display hints and warnings
 */
class CustomToast(val context: Context, val parent: ConstraintLayout) {

    private val snackBarLayout: View = View.inflate(context, R.layout.toast, null)
    private val snackBar = Snackbar.make(parent, "", Snackbar.LENGTH_SHORT)
    private val snackbarLayout = snackBar.view as Snackbar.SnackbarLayout
    private val text = snackBarLayout.findViewById<TextView>(R.id.toast_text)
    private val image = snackBarLayout.findViewById<ImageView>(R.id.toast_icon)
    private val layout = snackBarLayout.findViewById<LinearLayout>(R.id.toast_type)
    private val params = snackBar.view.layoutParams as ViewGroup.MarginLayoutParams
    private val display = parent.resources.displayMetrics
    private val height = display.heightPixels
    private val width = display.widthPixels

    /**
     * Initializes Toast Message, sets functionality and decides which message will be displayed
     * @param type used to decide which type of message will be displayed
     */
    fun setUpSnackbar(type: String) {
        snackBar.view.setBackgroundResource(R.color.transparent)
        snackBar.animationMode = Snackbar.ANIMATION_MODE_FADE
        snackbarLayout.addView(snackBarLayout, 0)
        snackBarLayout.setOnClickListener {
            snackBar.dismiss()
        }

        when (type) {
            "wave" -> {
                text.text = buildString {
                    append(context.getString(R.string.wave))
                    append(" ")
                    append(GameManager.gameLevel + 1)
                }
                image.setImageResource(R.drawable.wave_up)
                layout.setBackgroundResource(R.drawable.wave_toast_shape)
            }
            "money" -> {
                text.setText(R.string.moneyWarning)
                image.setImageResource(R.drawable.coins)
                layout.setBackgroundResource(R.drawable.warning_toast_shape)
            }
            "bossLevel" -> {
                text.setText(R.string.bossLevel)
                image.setImageResource(R.drawable.boss_wave_icon)
                layout.setBackgroundResource(R.drawable.boss_toast_shape)
            }
        }
        params.setMargins(width / 5, height / 2, width / 5, height / 2)
        snackBar.view.layoutParams = params
        snackBar.show()
    }
}
