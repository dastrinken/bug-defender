package de.mow2.towerdefense.controller.helper

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.children
import de.mow2.towerdefense.R

/**
 * class to highlight elements in tutorial
 */
class TutorialHighlighter(
    private val healthBar: ConstraintLayout,
    private val progressBar: ConstraintLayout,
    private val time: ConstraintLayout,
    private val coins: ConstraintLayout,
    private val bottomGui: ConstraintLayout,
    private val gameContainer: ScrollView,
    private val menuBtn: Button,
    private val deleteBtn: ImageButton,
    private val upgradeBtn: ImageButton,
    private val buildBtn: ImageButton
) {

    /**
     * sets alpha for elements to show or hide them
     * @param element for view or view.children
     * @param show true = show, false = don't show
     * @param hide true = hide, false = show
     */
    private fun highlight(element: View, show: Boolean) {
        if (show) {
            element.alpha = 1F
        } else {
            element.alpha = 0.1F
        }
    }

    private fun highlight(element: Sequence<View>, show: Boolean) {
        if (show) {
            element.forEach { it.alpha = 1F }
        } else {
            element.forEach { it.alpha = 0.1F }
        }
    }

    private fun dimElements(element: View, hide: Boolean, context: Context) {
        if (hide) {
            element.foreground =
                ColorDrawable(ContextCompat.getColor(context, R.color.black_overlay))
        } else {
            element.foreground = ColorDrawable(ContextCompat.getColor(context, R.color.transparent))
        }
    }

    /**
     * highlight Elements in Tutorial
     * @param element defines which Element should be shown
     */
    fun showElements(element: String, context: Context) {
        highlight(bottomGui.children, false)
        highlight(time.children, false)
        highlight(coins.children, false)
        highlight(healthBar.children, false)
        highlight(progressBar.children, false)
        highlight(menuBtn, false)
        when (element) {
            "tutorial" -> {
                dimElements(gameContainer, true, context)
            }
            "playfield" -> {
                dimElements(gameContainer, false, context)
            }
            "bottomGui" -> {
                dimElements(gameContainer, true, context)
                highlight(bottomGui.children, true)
            }
            "deleteBtn" -> {
                highlight(deleteBtn, true)
            }
            "upgradeBtn" -> {
                highlight(upgradeBtn, true)
            }
            "buildBtn" -> {
                highlight(buildBtn, true)
            }
            "topGui" -> {
                highlight(time.children, true)
                highlight(coins.children, true)
                highlight(healthBar.children, true)
                highlight(progressBar.children, true)
                highlight(menuBtn, true)
            }
            "wave" -> {
                highlight(time.children, true)
            }
            "coins" -> {
                highlight(coins.children, true)
            }
            "healthBar" -> {
                highlight(healthBar.children, true)
            }
            "progressBar" -> {
                highlight(progressBar.children, true)
            }
            "menuBtn" -> {
                highlight(menuBtn, true)
            }
            "endTutorial" -> {
                dimElements(gameContainer, false, context)
                highlight(bottomGui.children, true)
                highlight(time.children, true)
                highlight(coins.children, true)
                highlight(healthBar.children, true)
                highlight(progressBar.children, true)
                highlight(menuBtn, true)
            }

        }
    }
}