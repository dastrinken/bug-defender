package de.mow2.towerdefense.controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.databinding.TutorialPopupBinding

/**
 * Fragment for the tutorial
 * decides in when statement which text to show and which element should be highlighted an hidden
 */
class TutorialFragment : DialogFragment() {
    private var _binding: TutorialPopupBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TutorialPopupBinding.inflate(inflater, container, false)
        dialog?.setCancelable(false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val tutText = binding.tutText
        val tutBtn = binding.goOnBtn
        val tutClose = binding.closeTutBtn
        val gameActivity = (activity as GameActivity)

        dialog?.window?.also { window ->
            window.attributes?.also { attributes ->
                attributes.dimAmount = 0f
                window.attributes = attributes
            }
        }
        tutText.setText(R.string.tutorialWelcome)
        gameActivity.highlight("tutorial")

        tutBtn.setOnClickListener {
            when (tutText.text) {
                getString(R.string.tutorialWelcome) -> {
                    tutText.setText(R.string.tutorialScroll)
                    gameActivity.highlight("playfield")
                }
                getString(R.string.tutorialScroll) -> {
                    tutText.setText(R.string.tutorialBottomGui)
                    gameActivity.highlight("bottomGui")
                }
                getString(R.string.tutorialBottomGui) -> {
                    tutText.setText(R.string.tutorialBuildBtn)
                    gameActivity.highlight("buildBtn")
                }
                getString(R.string.tutorialBuildBtn) -> {
                    tutText.setText(R.string.tutorialUpgradeBtn)
                    gameActivity.highlight("upgradeBtn")
                }
                getString(R.string.tutorialUpgradeBtn) -> {
                    tutText.setText(R.string.tutorialDeleteBtn)
                    gameActivity.highlight("deleteBtn")
                }
                getString(R.string.tutorialDeleteBtn) -> {
                    tutText.setText(R.string.tutorialTopGui)
                    gameActivity.highlight("topGui")
                }
                getString(R.string.tutorialTopGui) -> {
                    tutText.setText(R.string.tutorialCoins)
                    gameActivity.highlight("coins")
                }
                getString(R.string.tutorialCoins) -> {
                    tutText.setText(R.string.tutorialWave)
                    gameActivity.highlight("wave")
                }
                getString(R.string.tutorialWave) -> {
                    tutText.setText(R.string.tutorialLifeBar)
                    gameActivity.highlight("healthBar")
                }
                getString(R.string.tutorialLifeBar) -> {
                    tutText.setText(R.string.tutoriaWaveBar)
                    gameActivity.highlight("progressBar")
                }
                getString(R.string.tutoriaWaveBar) -> {
                    tutText.setText(R.string.tutorialMenuButton)
                    gameActivity.highlight("menuBtn")
                    tutBtn.setText(R.string.close_button)
                    tutBtn.setOnClickListener {
                        gameActivity.highlight("endTutorial")
                        gameActivity.displayTutorial(false)
                        dismiss()
                    }
                }
            }
        }

        tutClose.setOnClickListener {
            gameActivity.highlight("endTutorial")
            gameActivity.displayTutorial(false)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}