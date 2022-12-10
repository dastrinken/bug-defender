package de.mow2.towerdefense.controller.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import de.mow2.towerdefense.R
import de.mow2.towerdefense.controller.GameActivity
import de.mow2.towerdefense.databinding.PopupViewBinding

/**
 * Dialog Fragment for popup window in mainActivity (about, info, prefs)
 * inflates view onCreate, sets Settings for each popup window
 * gets preferences through childFragment
 * functionality for close button in popup view
 **/
class PopupFragment : DialogFragment() {
    private var _binding: PopupViewBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PopupViewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeBtn.setOnClickListener {
            dismiss()
        }

        //decide which fragment will be inflated
        when (tag.toString()) {
            "aboutDialog" -> {
                binding.popupText.visibility = View.VISIBLE
                binding.popupText.setText(R.string.about_text)
                binding.popupTitleText.setText(R.string.about_button)
            }
            "settingsDialog" -> {
                binding.popupTitleText.setText(R.string.preference_button)
                binding.popupFragmentContainer.visibility = View.VISIBLE
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
            "menuDialog" -> {
                binding.tutorialBtn.visibility = View.VISIBLE
                binding.popupFragmentContainer.visibility = View.VISIBLE
                binding.pauseGameBtn.visibility = View.VISIBLE
                binding.menuDivider.visibility = View.VISIBLE
                binding.popupTitleText.setText(R.string.preference_button)
                binding.tutorialBtn.setText(R.string.tutorialTitel)
                binding.tutorialBtn.setOnClickListener {
                    (activity as GameActivity).displayTutorial(true)
                    dismiss()
                }
                childFragmentManager
                    .beginTransaction()
                    .replace(R.id.popupFragmentContainer, SettingsFragment())
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}